package main;

import soukoban.Soukoban;

import java.util.Scanner;

public class PlayGamePrompt {
    public void playGame(Soukoban game) {
        Scanner sc = new Scanner(System.in);
        while (!game.isClear()) {
            System.out.println(game);
            System.out.print(">");
            String inputStr = sc.nextLine();
            if (inputStr == null || inputStr.length() <= 0) {
                inputStr = " ";
            }

            switch (inputStr.toUpperCase().charAt(0)) {
                case 'W' -> game.moveUp();
                case 'S' -> game.moveDown();
                case 'D' -> game.moveRight();
                case 'A' -> game.moveLeft();
                case 'R' -> game.reset();
                case 'B' -> game.back();
                default -> {
                    //何もしない
                }

            }
        }
        System.out.println(game);
        System.out.println(game.getMoveCount() + "回でクリア");
    }
}
