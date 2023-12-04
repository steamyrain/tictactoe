package com.steamyrain.tictactoe.controller.view;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexViewController {

	@Value("${game.setting.board-dimension}")
	private Integer boardDimension;

	@GetMapping
	public ModelAndView index() {
		return ticTacToe();
	}

	@RequestMapping("/index")
	public ModelAndView ticTacToe() {
		ModelAndView modelAndView = new ModelAndView("index");
		String[][] board = new String[boardDimension][boardDimension];
		Arrays.stream(board).forEach(row -> Arrays.fill(row, " "));
		modelAndView.addObject("board", board);
		return modelAndView;
	}
}
