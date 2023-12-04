package com.steamyrain.tictactoe.common;

import com.steamyrain.tictactoe.dto.ws.TTTMsg;
import com.steamyrain.tictactoe.entity.TicTacToe;

public class Utils {

	public static TTTMsg gameToMessage(TicTacToe game) {
		TTTMsg message = new TTTMsg();
		message.setGameId(game.getGameId());
		message.setPlayer1(game.getPlayer1());
		message.setPlayer2(game.getPlayer2());
		message.setBoard(game.getBoard());
		message.setTurn(game.getTurn());
		message.setGameStatus(game.getGameStatus());
		message.setWinner(game.getWinner());
		return message;
	}

}
