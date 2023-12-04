package com.steamyrain.tictactoe.dto.ws;

import com.steamyrain.tictactoe.common.enumeration.GameStatus;
import com.steamyrain.tictactoe.entity.TicTacToe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TTTMsg implements IGameMsg {
    private String type;
    private String gameId;
    private String player1;
    private String player2;
    private String winner;
    private String turn;
    private String content;
    private String[][] board;
    private Integer moveRow;
    private Integer moveCell;
    private GameStatus gameStatus;
    private String sender;

    public TTTMsg() {
    }

    public TTTMsg(TicTacToe game) {
        this.gameId = game.getGameId();
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.winner = game.getWinner();
        this.turn = game.getTurn();
        this.board = game.getBoard();
        this.gameStatus = game.getGameStatus();
    }
}
