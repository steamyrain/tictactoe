package com.steamyrain.tictactoe.dto.ws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinGameMsg implements IGameMsg {
	private String type;
	private String gameId;
	private String playerName;
	private String content;
}
