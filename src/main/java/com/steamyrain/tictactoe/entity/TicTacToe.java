package com.steamyrain.tictactoe.entity;

import java.util.Arrays;

import com.steamyrain.tictactoe.common.enumeration.GameStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicTacToe {
	private String gameId;
	private String[][] board;
	private String player1;
	private String player2;
	private String winner;
	private String turn;
	private GameStatus gameStatus;

	public TicTacToe() {
	}
	
	public TicTacToe(String gameId, String player1, String[][] board) {
		this.gameId = gameId;
		this.player1 = player1;
		this.player2 = null;
		this.turn = player1;
		this.board = board;
		Arrays.stream(this.board).forEach(a -> Arrays.fill(a, " "));
		this.gameStatus = GameStatus.WAITING;
	}

}
