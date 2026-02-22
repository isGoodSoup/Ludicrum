package org.lud.engine.enums;

import org.lud.engine.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Turn {
	LIGHT,
	DARK;

	private static final Logger log = LoggerFactory.getLogger(Turn.class);

	Turn() {}

	public static void nextTurn(GameService gameService) {
		Turn[] turns = Turn.values();
		int nextIndex = (gameService.getCurrentTurn().ordinal() + 1) % turns.length;
		gameService.setCurrentTurn(turns[nextIndex]);
		log.info("Changed turns to {}", gameService.getCurrentTurn());
	}
}
