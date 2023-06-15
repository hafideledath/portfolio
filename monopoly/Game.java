package Game;

import Helpers.Helpers;
import Helpers.Helpers.*;
import Player.Player;
import BoardLinkedList.BoardLinkedList;
import BoardLinkedList.BoardLinkedList.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    public static ListNode playerNode;
    public static ListNode opponentNode;
    public static Player player = new Player();
    public static Player opponent = new Player();
    public static BoardLinkedList board;
    private static Scanner sc = new Scanner(System.in);

    private static int playerJailCount;
    private static int opponentJailCount;
    private static int playerDoubleRollCount;
    private static int opponentDoubleRollCount;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(Helpers.PURPLE_BOLD + "Welcome to Monopoly!\n\n" + Helpers.RESET);
        Thread.sleep(500);
        
        board = new BoardLinkedList();
        board.initialize();
        playerNode = board.head;
        opponentNode = board.head;
        while (true) {
            System.out.print(board);
            playerTurn();
            opponentTurn();
        }
    }

    private static void playerTurn() throws IOException, InterruptedException {
        System.out.println(Helpers.PURPLE_BOLD + "It's your turn!" + Helpers.RESET);
        
        if (!player.inJail) {
            int[] rollArray = diceRoll();
            int roll = rollArray[0] + rollArray[1];
            System.out.printf(Helpers.GREEN + "You rolled a%s %d!\n\n" + Helpers.RESET, (roll == 8 || roll == 11) ? "n" : "", roll);
            Thread.sleep(500);
            if ((playerNode.id + roll) >= 40) {
                landedOnGo();
            }

            move(roll);

            if (rollArray[0] == rollArray[1]) {
                if (playerDoubleRollCount < 3) {
                    System.out.print(board);
                    System.out.println(Helpers.GREEN + "Since you rolled a double, you get to roll again.\n" + Helpers.RESET);
                    playerDoubleRollCount++;
                    playerTurn();
                } else {
                    System.out.println(Helpers.RED + "You have rolled a double for three turns in a row. You have been caught speeding and now must go to jail.\n" + Helpers.RESET);
                    playerDoubleRollCount = 0;
                    goToJail();
                }
            } else {
                playerDoubleRollCount = 0;
            }
        } else {
            if (jailTurn()) {
                player.inJail = false;
            }
        }
    }

    private static void opponentTurn() throws IOException, InterruptedException {
        System.out.println(Helpers.PURPLE_BOLD + "It's your opponent's turn!");
        
        if (!opponent.inJail) {
            int[] rollArray = opponentDiceRoll();
            int roll = rollArray[0] + rollArray[1];
            System.out.printf(Helpers.CYAN + "\nYour opponent rolled a%s %d!\n\n" + Helpers.RESET, (roll == 8 || roll == 11) ? "n" : "", roll);
            Thread.sleep(500);
            if ((opponentNode.id + roll) >= 40) {
                opponentLandedOnGo();
            }

            opponentMove(roll);

            if (rollArray[0] == rollArray[1]) {
                if (playerDoubleRollCount < 3) {
                    System.out.println(Helpers.CYAN + "Since your opponent rolled a double, they get to roll again.\n" + Helpers.RESET);
                    opponentDoubleRollCount++;
                    opponentTurn();
                } else {
                    System.out.println(Helpers.CYAN + "Your opponent has rolled a double for three turns in a row. They have been caught speeding and now must go to jail.\n" + Helpers.RESET);
                    opponentDoubleRollCount = 0;
                    opponentGoToJail();
                }
            }
        } else {
            if (opponentJailTurn()) {
                opponent.inJail = false;
            }
        }
    }

    public static void landedOnGo() throws InterruptedException {
        player.balance += 200;
        System.out.printf(Helpers.GREEN + "You landed on GO! You gained $200. Your new balance is %d.\n\n" + Helpers.RESET, player.balance);
        Thread.sleep(1000);
    }

    public static void opponentLandedOnGo() throws InterruptedException {
        opponent.balance += 200;
        System.out.printf(Helpers.CYAN + "Your opponent landed on GO! They gained $200. Their new balance is %d.\n\n" + Helpers.RESET, opponent.balance);
        Thread.sleep(1000);
    }
    
    public static void goToJail() throws IOException, InterruptedException {
        int tiles;
        if (playerNode.id > 10) {
            tiles = (40 + 10) - playerNode.id;
        } else {
            tiles = 10 - playerNode.id;
        }
        for (int i = 0; i < tiles; i++) {
            playerNode = playerNode.next;
        }

        System.out.println(Helpers.RED + "You are now in jail.\n" + Helpers.RESET);

        player.inJail = true;
    }

    public static void opponentGoToJail() throws IOException, InterruptedException {
        int tiles;
        if (opponentNode.id > 10) {
            tiles = (40 + 10) - opponentNode.id;
        } else {
            tiles = 10 - opponentNode.id;
        }
        for (int i = 0; i < tiles; i++) {
            opponentNode = opponentNode.next;
        }

        System.out.println(Helpers.CYAN + "Your opponent is now in jail.\n");

        opponent.inJail = true;

        System.out.print(board);
    }

    public static boolean jailTurn() throws IOException, InterruptedException {
        System.out.println(Helpers.RED + "You are currently in jail.\n");

        if (player.getOutOfJailFreeCards > 0) {
            player.getOutOfJailFreeCards -= 1;
            System.out.println(Helpers.GREEN + "Luckily, you have at least one get out of jail free card. You're free!\n");
            return true;
        }

        if (playerJailCount >= 3) {
            System.out.println(Helpers.RED + "You have been in jail for over 3 turns. You must now pay $50 to exit.");
            if (player.balance > 50) {
                player.balance -= 50;
                System.out.printf(Helpers.GREEN + "You are now free. Your new balance is $%d.\n\n" + Helpers.RESET, player.balance);
                return true;
            } else {
                gameFinished(false);
            }
        }

        playerJailCount += 1;

        System.out.println(Helpers.PURPLE_BOLD + "To get out early, you can:");
        System.out.println("1: Attempt to roll a double\n2: Pay $50");
        System.out.print("What would you like to do? [1/2] " + Helpers.RESET);
        return attemptEscapeJail();
    }

    public static boolean opponentJailTurn() throws IOException, InterruptedException {
        System.out.println(Helpers.CYAN + "Your opponent is currently in jail.\n");

        if (opponent.getOutOfJailFreeCards > 0) {
            opponent.getOutOfJailFreeCards -= 1;
            System.out.println("Luckily for them, have a get out of jail free card. They use it to free themselves.\n");
            return true;
        }

        if (opponentJailCount >= 3) {
            System.out.println("Your opponent has been in jail for over 3 turns. They must now pay $50 to exit.");
            if (opponent.balance > 50) {
                opponent.balance -= 50;
                System.out.printf("Your opponent is now free. Their new balance is $%d.\n\n" + Helpers.RESET, opponent.balance);
                return true;
            } else {
                gameFinished(true);
            }
        }

        opponentJailCount += 1;
        
        return opponentAttemptEscapeJail();
    }

    private static boolean attemptEscapeJail() throws IOException, InterruptedException {
        int choice = getInt(2);
        System.out.println(Helpers.RESET);

        switch (choice) {
            case 1:
                int[] rollArray = diceRoll();
                if (rollArray[0] == rollArray[1]) {
                    System.out.println(Helpers.GREEN + "Great job! You rolled a double. You are now free.\n" + Helpers.RESET);
                    playerJailCount = 0;
                    return true;
                } else {
                    System.out.println(Helpers.RED + "You did not roll a double.\n" + Helpers.RESET);
                    return false;
                }
        
            case 2:
                if (player.balance > 50) {
                    player.balance -= 50;
                    System.out.printf(Helpers.GREEN + "You are now free. Your balance is $%d.\n" + Helpers.RESET, player.balance);
                    playerJailCount = 0;
                    return true;
                } else {
                    System.out.println(Helpers.RED + "You cannot afford to pay $50. Please pick another option." + Helpers.RESET);
                    return attemptEscapeJail();
                }
        }
        return false;
    }

    private static boolean opponentAttemptEscapeJail() {
        if (opponent.getOutOfJailFreeCards > 0) {
            System.out.println("Your opponent has a get out of jail free card, which they use to free themselves.\n" + Helpers.RESET);
            opponent.getOutOfJailFreeCards -= 1;
            return true;
        }

        if (opponent.balance > 50) {
            opponent.balance -= 50;
            System.out.println("Your opponent pays $50 to free themselves.\n");
            System.out.printf("Their new balance is $%d.\n\n" + Helpers.RESET, opponent.balance);
            return true;
        }

        System.out.println("Your opponent attempts to roll a double...\n");
        if (ThreadLocalRandom.current().nextInt(1, 7) == 1) {
            System.out.println("They succeed!\n" + Helpers.RESET);
            return true;
        }

        return false;
    }

    private static int getInt(int max) {
        try {
            int num = Integer.parseInt(sc.nextLine());
            if (num > 0 && num <= max) {
                return num;
            } else {
                return getInt(max); 
            }
        } catch (Exception e) {
            return getInt(max);
        }
    }

    public static void gameFinished(boolean playerWon) throws IOException {
        FileWriter fileWriter = new FileWriter(BoardLinkedList.filename);
        if (playerWon) {
            System.out.println(Helpers.GREEN + """
                Your opponent is forced to declare bancrupcy!
                 /$$     /$$ /$$$$$$  /$$   /$$       /$$      /$$ /$$$$$$ /$$   /$$ /$$
                |  $$   /$$//$$__  $$| $$  | $$      | $$  /$ | $$|_  $$_/| $$$ | $$| $$
                 \\  $$ /$$/| $$  \\ $$| $$  | $$      | $$ /$$$| $$  | $$  | $$$$| $$| $$
                  \\  $$$$/ | $$  | $$| $$  | $$      | $$/$$ $$ $$  | $$  | $$ $$ $$| $$
                   \\  $$/  | $$  | $$| $$  | $$      | $$$$_  $$$$  | $$  | $$  $$$$|__/
                    | $$   | $$  | $$| $$  | $$      | $$$/ \\  $$$  | $$  | $$\\  $$$    
                    | $$   |  $$$$$$/|  $$$$$$/      | $$/   \\  $$ /$$$$$$| $$ \\  $$ /$$
                    |__/    \\______/  \\______/       |__/     \\__/|______/|__/  \\__/|__/""");
            fileWriter.write("""
                <div style=\"white-space: pre; color: green; font-family: Courier; font-weight: bold; font-size: 30px; line-height: 30px; filter: brightness(2);\"> 
                 /$$     /$$ /$$$$$$  /$$   /$$       /$$      /$$ /$$$$$$ /$$   /$$ /$$
                |  $$   /$$//$$__  $$| $$  | $$      | $$  /$ | $$|_  $$_/| $$$ | $$| $$
                 \\  $$ /$$/| $$  \\ $$| $$  | $$      | $$ /$$$| $$  | $$  | $$$$| $$| $$
                  \\  $$$$/ | $$  | $$| $$  | $$      | $$/$$ $$ $$  | $$  | $$ $$ $$| $$
                   \\  $$/  | $$  | $$| $$  | $$      | $$$$_  $$$$  | $$  | $$  $$$$|__/
                    | $$   | $$  | $$| $$  | $$      | $$$/ \\  $$$  | $$  | $$\\  $$$
                    | $$   |  $$$$$$/|  $$$$$$/      | $$/   \\  $$ /$$$$$$| $$ \\  $$ /$$
                    |__/    \\______/  \\______/       |__/     \\__/|______/|__/  \\__/|__/</div>""");
        } else {
            System.out.println(Helpers.RED + """
                You are forced to declare bancrupcy.
                 /$$     /$$ /$$$$$$  /$$   /$$       /$$        /$$$$$$   /$$$$$$  /$$$$$$$$            
                |  $$   /$$//$$__  $$| $$  | $$      | $$       /$$__  $$ /$$__  $$| $$_____/            
                 \\  $$ /$$/| $$  \\ $$| $$  | $$      | $$      | $$  \\ $$| $$  \\__/| $$                  
                  \\  $$$$/ | $$  | $$| $$  | $$      | $$      | $$  | $$|  $$$$$$ | $$$$$               
                   \\  $$/  | $$  | $$| $$  | $$      | $$      | $$  | $$ \\____  $$| $$__/               
                    | $$   | $$  | $$| $$  | $$      | $$      | $$  | $$ /$$  \\ $$| $$                  
                    | $$   |  $$$$$$/|  $$$$$$/      | $$$$$$$$|  $$$$$$/|  $$$$$$/| $$$$$$$$ /$$ /$$ /$$
                    |__/    \\______/  \\______/       |________/ \\______/  \\______/ |________/|__/|__/|__/""");
            fileWriter.write("""
                <div style=\"white-space: pre; color: red; font-family: Courier; font-weight: bold; font-size: 30px; line-height: 30px; filter: brightness(2);\"> 
                 /$$     /$$ /$$$$$$  /$$   /$$       /$$        /$$$$$$   /$$$$$$  /$$$$$$$$            
                |  $$   /$$//$$__  $$| $$  | $$      | $$       /$$__  $$ /$$__  $$| $$_____/            
                 \\  $$ /$$/| $$  \\ $$| $$  | $$      | $$      | $$  \\ $$| $$  \\__/| $$                  
                  \\  $$$$/ | $$  | $$| $$  | $$      | $$      | $$  | $$|  $$$$$$ | $$$$$               
                   \\  $$/  | $$  | $$| $$  | $$      | $$      | $$  | $$ \\____  $$| $$__/               
                    | $$   | $$  | $$| $$  | $$      | $$      | $$  | $$ /$$  \\ $$| $$                  
                    | $$   |  $$$$$$/|  $$$$$$/      | $$$$$$$$|  $$$$$$/|  $$$$$$/| $$$$$$$$ /$$ /$$ /$$
                    |__/    \\______/  \\______/       |________/ \\______/  \\______/ |________/|__/|__/|__/""");
        }
        fileWriter.flush();
        fileWriter.close();
        System.exit(0);
    }

    public static int[] diceRoll() throws IOException, InterruptedException {
        int x = ThreadLocalRandom.current().nextInt(1, 7);
        int y = ThreadLocalRandom.current().nextInt(1, 7);
    
        while (System.in.available() == 0) {
            x = ThreadLocalRandom.current().nextInt(1, 7);
            y = ThreadLocalRandom.current().nextInt(1, 7);
            System.out.printf("\rPress \u001B[1;30mEnter%s to continue %s[%d] [%d] " + Helpers.RESET, Helpers.RESET, Helpers.YELLOW, x, y);
            Thread.sleep(50);
        }

        System.in.read();
        System.out.println();
        return new int[] {x, y};
    }

    public static int[] opponentDiceRoll() throws IOException, InterruptedException {
        int x = ThreadLocalRandom.current().nextInt(1, 7);
        int y = ThreadLocalRandom.current().nextInt(1, 7);
    
        for (int i = 0; i < 50; i++) {
            x = ThreadLocalRandom.current().nextInt(1, 7);
            y = ThreadLocalRandom.current().nextInt(1, 7);
            System.out.printf(Helpers.RESET + "\rYour opponent is rolling the dice... [%d] [%d] ", x, y);
            Thread.sleep(50);
        }

        System.out.println();
        return new int[] {x, y};
    }
        
    public static void move(int tiles) throws IOException, InterruptedException {
        for (int i = 0; i < tiles; i++) {
            playerNode = playerNode.next;
        }

        System.out.println(playerNode.id);
        System.out.print(board);

        Tiles.onLand(playerNode.id);

        System.out.println();
        System.out.print(board);
        Thread.sleep(500);
    }   

    public static void opponentMove(int tiles) throws IOException, InterruptedException {
        for (int i = 0; i < tiles; i++) {
            opponentNode = opponentNode.next;
        }

        System.out.print(board);

        Tiles.onOpponentLand(opponentNode.id);

        System.out.println();
        Thread.sleep(500);
    }  
}