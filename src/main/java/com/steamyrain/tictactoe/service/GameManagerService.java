package com.steamyrain.tictactoe.service;

import com.steamyrain.tictactoe.common.Utils;
import com.steamyrain.tictactoe.common.enumeration.GameStatus;
import com.steamyrain.tictactoe.dto.common.WinnerCheckerDTO;
import com.steamyrain.tictactoe.dto.ws.TTTMsg;
import com.steamyrain.tictactoe.entity.TicTacToe;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameManagerService {

    private final Map<String, TicTacToe> games;

    protected final Map<String, String> waitingPlayers;

    public GameManagerService() {
        games = new ConcurrentHashMap<>();
        waitingPlayers = new ConcurrentHashMap<>();
   }

    public synchronized TicTacToe joinGame(String player, Integer boardDmnsn) {
        if (games.values().stream().anyMatch(game -> 
                        game.getPlayer1().equals(player) 
                        && (game.getBoard() != null)
                        && (game.getBoard().length == boardDmnsn)
                        || game.getPlayer2() != null 
                        && game.getPlayer2().equals(player)
                    )
        ) {
            return games.values().stream().filter(game -> 
                    game.getPlayer1().equals(player)
                    || game.getPlayer2().equals(player)
                    && (game.getBoard() != null)
                    && (game.getBoard().length == boardDmnsn)).findFirst().get();
        }

        for (TicTacToe game : games.values()) {
            if ((game.getPlayer1() != null)
                    && (game.getBoard() != null)
                    && (game.getBoard().length == boardDmnsn)
                    && (game.getPlayer2() == null)
            ) {
                game.setPlayer2(player);
                game.setGameStatus(GameStatus.P1_TURN);
                return game;
            }
        }

	String gameId = UUID.randomUUID().toString();
	String[][] board = new String[boardDmnsn][boardDmnsn];
        TicTacToe game = new TicTacToe(gameId, player, board);
        games.put(game.getGameId(), game);
        waitingPlayers.put(player, game.getGameId());
        return game;
    }

    public synchronized TicTacToe leaveGame(String player, Integer boardDmnsn) {
        String gameId = getGameByPlayer(player) != null ? getGameByPlayer(player).getGameId() : null;
        if (gameId != null) {
            waitingPlayers.remove(player);
            TicTacToe game = games.get(gameId);
            if (player.equals(game.getPlayer1())) {
                if (game.getPlayer2() != null) {
                    game.setPlayer1(game.getPlayer2());
                    game.setPlayer2(null);
                    game.setGameStatus(GameStatus.WAITING);
                    game.setBoard(new String[boardDmnsn][boardDmnsn]);
                    waitingPlayers.put(game.getPlayer1(), game.getGameId());
                } else {
                    games.remove(gameId);
                    return null;
                }
            } else if (player.equals(game.getPlayer2())) {
                game.setPlayer2(null);
                game.setGameStatus(GameStatus.WAITING);
                game.setBoard(new String[boardDmnsn][boardDmnsn]);
                waitingPlayers.put(game.getPlayer1(), game.getGameId());
            }
            return game;
        }
        return null;
    }

    public Boolean isBoardFilled(String gameId) {
            TicTacToe game = getGame(gameId);
            return Arrays.stream(game.getBoard()).flatMap(arr -> Arrays.stream(arr)).allMatch( v -> v == "X" || v == "O");
    }
    
    public Boolean isBoardFilled(TicTacToe game) {
            return Arrays.stream(game.getBoard()).flatMap(arr -> Arrays.stream(arr)).allMatch( v -> v == "X" || v == "O");
    }

    public Boolean hasEnded(String gameId) {
            TicTacToe game = getGame(gameId);
            return game.getWinner() != null || isBoardFilled(gameId);
    }

    public Boolean hasEnded(TicTacToe game) {
            return game.getWinner() != null || isBoardFilled(game);
    }

    public TTTMsg makeMove(TTTMsg msg) {

            String player = msg.getSender();
            String gameId = msg.getGameId();
            Integer moveRow = msg.getMoveRow();
            Integer moveCell = msg.getMoveCell();

            TicTacToe game = getGame(gameId);

            if (game == null || hasEnded(game)) {
                    TTTMsg errorMessage = new TTTMsg();
                    errorMessage.setType("error");
                    errorMessage.setContent("Game either not found or has ended");
                    removeGame(gameId);
                    return errorMessage;
            } else if (game.getGameStatus().equals(GameStatus.WAITING)) {
                    TTTMsg errorMessage = new TTTMsg();
                    errorMessage.setType("error");
                    errorMessage.setContent("Game is waiting for another player to join");
                    return errorMessage;
            } else if (game.getTurn().equals(player)) {
                    TTTMsg gameStateMessage = new TTTMsg(game);
                    gameStateMessage.setType("game.move");
                    String[][] board = game.getBoard();
                    String player1 = game.getPlayer1();
                    String player2 = game.getPlayer2();

                    if (board[moveRow][moveCell].equals(" ")) {
                        String mark = player.equals(player1) ? "X" : "O";
                        board[moveRow][moveCell] = mark; 

                        checkAndSetWinner(game, mark, moveRow, moveCell);

                        game.setTurn(player.equals(player1) ? player2 : player1);
                        updateGameStatus(game);
                    }

                    if (hasEnded(game)) {
                            TTTMsg gameOverMessage = Utils.gameToMessage(game); 
                            gameOverMessage.setType("game.gameOver");
                            removeGame(gameId);
                            return gameOverMessage;
                    }
                    return gameStateMessage;
            } else {
                    TTTMsg errorMessage = new TTTMsg();
                    errorMessage.setType("error");
                    errorMessage.setContent("player "+player+" trying to move when it's not their turn yet");
                    return errorMessage;
            }
    }

    private void updateGameStatus(TicTacToe game) {
        String winner = game.getWinner();
        String p1 = game.getPlayer1();
        String turn = game.getTurn();
        if (winner != null) {
            game.setGameStatus(winner.equals(p1) ? GameStatus.P1_WON : GameStatus.P2_WON);
        } else if (isBoardFilled(game)) {
            game.setGameStatus(GameStatus.DRAW);
        } else {
            game.setGameStatus(turn.equals(p1) ? GameStatus.P1_TURN : GameStatus.P2_TURN); 
        }
    }

    private Boolean isWinMove (WinnerCheckerDTO dto, String direction) {
        for (int i=0;i<dto.dimension();i++) {
            switch (direction) {
                case "horizontal":
                    if(!dto.board()[dto.row()][i].equals(dto.mark())) return false;
                    break;
                case "vertical":
                    if(!dto.board()[i][dto.column()].equals(dto.mark())) return false;
                    break;
                case "diag":
                    if(!dto.board()[i][i].equals(dto.mark())) return false;
                    break;
                case "antidiag":
                    if(!dto.board()[i][dto.dimension()-i-1].equals(dto.mark())) return false;
                    break;
            }
        }
        return true;
    }

    private void checkAndSetWinner(TicTacToe game, String mark, Integer row, Integer cell) {
        String[][] board = game.getBoard();
        String[] directions = {"horizontal", "vertical", "diag", "antidiag"}; 
        WinnerCheckerDTO winnerCheckerDTO = new WinnerCheckerDTO(board, board.length, mark, row, cell);
        Boolean isWin = false;
        for (String direction : directions) {
            isWin = isWinMove(winnerCheckerDTO, direction);
            if(isWin) break;
        }
        if(isWin) {
            game.setWinner(game.getTurn());
        }
    }

    public TicTacToe getGame(String gameId) {
        return games.get(gameId);
    }

    public TicTacToe getGameByPlayer(String player) {
        return games.values().stream().filter(game -> game.getPlayer1().equals(player) || (game.getPlayer2() != null &&
                game.getPlayer2().equals(player))).findFirst().orElse(null);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }
}
