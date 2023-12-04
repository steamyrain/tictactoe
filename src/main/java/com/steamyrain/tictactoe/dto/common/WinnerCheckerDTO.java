package com.steamyrain.tictactoe.dto.common;

public record WinnerCheckerDTO(String[][] board, Integer dimension, String mark, Integer row, Integer column) {
}
