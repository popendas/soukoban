package main;

import soukoban.Soukoban;

public class Main {

	public static void main(String[] args) {
		PlayGame pg = new PlayGame();
		int[][] stage = {
				{ 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 0, 0, 0, 1, 1 },
				{ 1, 3, 4, 2, 0, 0, 1, 1 },
				{ 1, 1, 1, 0, 2, 3, 1, 1 },
				{ 1, 3, 1, 1, 2, 0, 1, 1 },
				{ 1, 0, 1, 0, 3, 0, 1, 1 },
				{ 1, 2, 0, 6, 2, 2, 3, 1 },
				{ 1, 0, 0, 0, 3, 0, 0, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1 } };
		Soukoban game = new Soukoban(stage);
		pg.playGame(game);
	}

}
