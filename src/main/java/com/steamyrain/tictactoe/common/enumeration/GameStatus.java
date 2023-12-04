package com.steamyrain.tictactoe.common.enumeration;

public enum GameStatus {
    WAITING("waiting for another player"),
    P1_TURN("player-1's turn"),
    P2_TURN("player-2's turn"),
    P1_WON("player-1 won"),
    P2_WON("player-2 won"),
    DRAW("draw");

    String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
