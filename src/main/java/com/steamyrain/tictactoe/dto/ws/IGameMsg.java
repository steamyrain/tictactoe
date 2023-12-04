package com.steamyrain.tictactoe.dto.ws;

public interface IGameMsg {
    String getType();
    String getGameId();
    String getContent();
}
