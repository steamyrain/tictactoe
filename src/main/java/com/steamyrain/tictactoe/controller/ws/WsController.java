package com.steamyrain.tictactoe.controller.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.steamyrain.tictactoe.common.Utils;
import com.steamyrain.tictactoe.dto.ws.JoinGameMsg;
import com.steamyrain.tictactoe.dto.ws.TTTMsg;
import com.steamyrain.tictactoe.entity.TicTacToe;
import com.steamyrain.tictactoe.service.GameManagerService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WsController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Value("${game.setting.board-dimension}")
	private Integer boardDimension;

	private final GameManagerService gameManagerService = new GameManagerService();

	@MessageMapping("/game.join")
	@SendTo("/topic/game.state")
	public Object joinGame(@Payload JoinGameMsg joinGameMsg, SimpMessageHeaderAccessor headerAccessor) {
		log.info("joinGameMsg: {}", joinGameMsg);
		TicTacToe game = gameManagerService.joinGame(joinGameMsg.getPlayerName(),boardDimension);
		log.info("finished creating game");
		headerAccessor.getSessionAttributes().put("gameId", game.getGameId());
		headerAccessor.getSessionAttributes().put("playerName", joinGameMsg.getPlayerName());
		log.info("finished setting session attribute");
		TTTMsg tttMsg = Utils.gameToMessage(game);
		log.info("finished converting game to message");
		tttMsg.setType("game.joined");
		return tttMsg;
	}

	@MessageMapping("/game.move")
	public void makeMove(@Payload TTTMsg message) {
		log.info("game move: {}",message);
		TTTMsg reply = gameManagerService.makeMove(message);
		log.info("finished making move: {}",reply);
		simpMessagingTemplate.convertAndSend("/topic/game." + message.getGameId(), reply);
	}

	@EventListener
	public void sessionDisconnectEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		log.info("{}", headerAccessor);
	}
	
}
