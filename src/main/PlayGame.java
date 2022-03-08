package main;

import soukoban.Soukoban;

import java.util.Scanner;

public class PlayGame {
    public void playGame(Soukoban game) {
        Scanner sc = new Scanner(System.in);
        while (!game.isClear()) {
            switch (sc.nextLine().charAt(0)) {
                //TODO
            }
        }
    }
}
