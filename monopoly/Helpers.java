package housemonopoly;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import Player.Player;
import Player.Player.*;
import Game.Game;

public class Helpers {
    private static Player player = Game.player;
    private static Player opponent = Game.opponent;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE_BOLD = "\u001B[1;35m";

    public static final String CYAN = "\u001B[96m";
    
    public static void main(String[] args) {
        return;
    }

    public class Constants {
        public static void main(String[] args) {
            return;
        }
    
        public static String[] tileArray() {
            return new String[] { 
                "GO", 
                "Mediterranean Ave", 
                "Community Chest [a]", 
                "Baltic Ave", 
                "Income Tax", 
                "Reading Railroad", 
                "Oriental Ave", 
                "Chance [a]", 
                "Vermont Ave", 
                "Connecticut Ave", 
                "Jail", 
                "St. Charles Place", 
                "Electric Company", 
                "States Avenue", 
                "Virginia Avenue", 
                "Pennsylvania Railroad", 
                "St. James Place", 
                "Community Chest [b]", 
                "Tennessee Ave", 
                "New York Ave", 
                "Free Parking", 
                "Kentucky Ave", 
                "Chance [b]", 
                "Indiana Ave", 
                "Illinois Ave", 
                "B&O Railroad", 
                "Atlantic Ave", 
                "Ventnor Ave", 
                "Water Works", 
                "Marvin Gardens", 
                "Go To Jail", 
                "Pacific Ave", 
                "North Carolina Ave", 
                "Community Chest [c]", 
                "Pennsylvania Ave", 
                "Short Line", 
                "Chance [c]", 
                "Park Place", 
                "Luxury Tax", 
                "Boardwalk"
            };
        }
    }

    public class Tiles {
        private static String[] tileArray = Constants.tileArray();
        private static Scanner sc = new Scanner(System.in);

        public static Map<Integer, Integer> indexTypeDict = new HashMap<Integer, Integer>() {{
            // 0: Purchasable, 1: Purchased, 2: Chance card, 3: Community chest, 4: Miscellaneous
            put(0, 4); // GO
            put(1, 0); // Mediterranean Avenue
            put(2, 3); // Community Chest
            put(3, 0); // Baltic Avenue
            put(4, 4); // Income Tax
            put(5, 0); // Reading Railroad
            put(6, 0); // Oriental Avenue
            put(7, 2); // Chance
            put(8, 0); // Vermont Avenue
            put(9, 0); // Connecticut Avenue
            put(10, 4); // Jail / Just Visiting
            put(11, 0); // St. Charles Place
            put(12, 0); // Electric Company
            put(13, 0); // States Avenue
            put(14, 0); // Virginia Avenue
            put(15, 0); // Pennsylvania Railroad
            put(16, 0); // St James Place
            put(17, 3); // Community Chest
            put(18, 0); // Tennessee Avenue
            put(19, 0); // New York Avenue
            put(20, 4); // Free Parking
            put(21, 0); // Kentucky Avenue
            put(22, 2); // Chance
            put(23, 0); // Indiana Avenue
            put(24, 0); // Illinois Avenue
            put(25, 0); // B.&O. Railroad
            put(26, 0); // Atlantic Avenue
            put(27, 0); // Ventnor Avenue
            put(28, 0); // Water Works
            put(29, 0); // Marvin Gardens
            put(30, 4); // Go to Jail
            put(31, 0); // Pacific Avenue
            put(32, 0); // North Carolina Avenue
            put(33, 3); // Community Chest
            put(34, 0); // Pennsylvania Avenue
            put(35, 0); // Short Line
            put(36, 2); // Chance
            put(37, 0); // Park Place
            put(38, 4); // Luxury Tax
            put(39, 0); // Boardwalk
        }};   

        private static void main(String[] args) {
            return;
        }

        public static void onLand(int tileID) throws IOException, InterruptedException {
            int tileType = indexTypeDict.get(tileID);

            switch (tileType) {
                case 0: // Purchasable
                    purchasable(tileID);
                    break;
                case 1: // Purchased
                    purchased(tileID);
                    break;
                case 2: // Chance card
                    chanceCard(tileID);
                    break;
                case 3: // Community chest
                    communityChest();
                    break;
                case 4: // Miscellaneous
                    miscellaneous(tileID);
                    break;
            }
        }

        public static Map<Integer, Integer> purchasablePrices = new HashMap<Integer, Integer>() {{
            put(1, 60);
            put(3, 60);
            put(5, 200);
            put(6, 100);
            put(8, 100);
            put(9, 120);
            put(11, 140);
            put(12, 150);
            put(13, 140);
            put(14, 160);
            put(15, 200);
            put(16, 180);
            put(18, 180);
            put(19, 200);
            put(21, 220);
            put(23, 220);
            put(24, 240);
            put(25, 200);
            put(26, 260);
            put(27, 260);
            put(28, 150);
            put(29, 280);
            put(31, 300);
            put(32, 300);
            put(34, 320);
            put(35, 200);
            put(37, 350);
            put(39, 400);
        }};

        public static void onOpponentLand(int tileID) throws IOException, InterruptedException {
            int tileType = indexTypeDict.get(tileID);
            
            switch (tileType) {
                case 0:
                    opponentPurchasable(tileID);
                    break;
                case 1:
                    opponentPurchased(tileID);
                    break;
                case 2:
                    opponentChanceCard(tileID);
                    break;
                case 3:
                    opponentCommunityChest();
                    break;
                case 4:
                    opponentMiscellaneous(tileID);
                    break;
            }
        }

        private static void purchasable(int tileID) {
            String tileName = tileArray[tileID];
            int tilePrice = purchasablePrices.get(tileID);
            boolean willBuy = ask(String.format(PURPLE_BOLD + "You have landed on %s. The asking price is %d.\nWould you like to purchase it? [y/n] " + RESET, tileName, tilePrice));
            System.out.println();
            if (willBuy) {
                if (player.purchaseTile(tileID)) {
                    System.out.printf(GREEN + "Successfully bought property! You now have $%d.\n" + RESET, player.balance);
                } else {
                    System.out.println(RED + "You do not have enough money." + RESET);
                }
            }
        }

        private static void opponentPurchasable(int tileID) throws InterruptedException {
            String tileName = tileArray[tileID];
            int tilePrice = purchasablePrices.get(tileID);
            System.out.printf(CYAN + "Your opponent has landed on %s. The price is $%d.\n\n", tileName, tilePrice);
            Thread.sleep(500);
            if ((tilePrice / opponent.balance) <= 0.75) {
                if (opponent.purchaseTile(tileID)) {
                    System.out.println("Your opponent has successfully bought the tile.");
                    System.out.printf("Their new balance is $%d.\n" + RESET, opponent.balance);
                }
            } else {
                System.out.println("Your opponent has decided not to buy the tile." + RESET);
            }
        }

        private static void purchased(int tileID) throws InterruptedException, IOException {
            String tileName = tileArray[tileID];
            if (player.properties.contains(tileID)) {
                System.out.printf(PURPLE_BOLD + "You landed on %s.\n\n" + RESET, tileName);
                Thread.sleep(500);
                System.out.println(GREEN + "You own this property, so you don't have to pay any rent." + RESET);
            } else {
                int rent = 25 * (int) Math.pow(2, railroadCount(opponent));
                System.out.printf(PURPLE_BOLD + "You landed on %s.\n" + RESET, tileName);
                Thread.sleep(500);
                System.out.printf(RED + "Your opponent owns this property, so you have to pay them $%d rent.\n" + RED, rent);
                deduct(rent);
                opponentAdd(rent);
            }
        }

        private static void opponentPurchased(int tileID) throws InterruptedException, IOException {
            String tileName = tileArray[tileID];
            if (opponent.properties.contains(tileID)) {
                System.out.printf(CYAN + "Your opponent landed on %s.\n\n", tileName);
                Thread.sleep(500);
                System.out.println("They own this property, so they don't have to pay any rent." + RESET);
            } else {
                int rent = 25 * (int) Math.pow(2, railroadCount(player));
                System.out.printf(CYAN + "Your opponent landed on %s.\n\n", tileName);
                Thread.sleep(500);
                System.out.printf("You own this property, so they have to pay you $%d rent.\n\n", rent);
                opponentDeduct(rent);
                add(rent);
            }
        }

        private static void chanceCard(int tileID) throws IOException, InterruptedException {
            System.out.println(PURPLE_BOLD + "You landed on a chance card tile. Please draw a card..." + RESET);
            waitForEnter(new String[] {"|", "/", "-", "\\"});
            switch (ThreadLocalRandom.current().nextInt(1, 17)) {
                case 1:
                    System.out.println(GREEN + "Advance to \"Go\" (Collect $200)\n");
                    Game.playerNode = Game.board.head;
                    Game.landedOnGo();
                    break;
                case 2:
                    System.out.println(GREEN + "Advance to Illinois Ave—If you pass Go, collect $200\n");
                    moveTo(tileID, 24);
                    break;
                case 3:
                    System.out.println(GREEN + "Advance to St. Charles Place - If you pass Go, collect $200\n");
                    moveTo(tileID, 11);
                    break;
                case 4:
                    System.out.println(YELLOW + "Advance token to nearest Utility\n");
                    moveTo(tileID, 12);
                    break;
                case 5:
                    System.out.println(YELLOW + "Advance token to the nearest Railroad\n");
                    if (tileID == 7) {
                        moveTo(tileID, 15);
                    } else {
                        moveTo(tileID, 5);
                    }
                    break;
                case 6:
                    System.out.println(GREEN + "Bank pays you dividend of $50\n");
                    add(50);
                    break;
                case 7:
                    System.out.println(GREEN + "Get Out of Jail Free - This card may be kept until needed\n");
                    player.getOutOfJailFreeCards += 1;
                    break;
                case 8:
                    System.out.println(YELLOW + "Go Back 3 Spaces\n");
                    Game.move(40 - 3);
                    break;
                case 9:
                    System.out.println(RED + "Go to Jail - Do not pass Go, do not collect $200\n");
                    Game.goToJail();
                    break;
                case 10:
                    System.out.println(RED + "Make general repairs on all your property - Pay $40 for every property you own\n");
                    deduct(player.properties.size() * 40);
                    break;
                case 11:
                    System.out.println(RED + "Pay poor tax of $15\n");
                    deduct(15);
                    break;
                case 12:
                    System.out.println(GREEN + "Take a trip to Reading Railroad - If you pass Go, collect $200\n");
                    moveTo(tileID, 5);
                    break;
                case 13:
                    System.out.println(GREEN + "Take a walk on the Boardwalk - Advance to Boardwalk\n");
                    moveTo(tileID, 39);
                    break;
                case 14:
                    System.out.println(RED + "You have been elected Chairman of the Board - Pay each player $50\n");
                    deduct(50);
                    opponentAdd(50);
                    break;
                case 15:
                    System.out.println(GREEN + "Your building loan matures - Collect $150\n");
                    add(150);
                    break;
                case 16:
                    System.out.println(GREEN + "You have won a crossword competition - Collect $100\n");
                    add(100);
                    break;
            }
            System.out.print(RESET);
        }

        private static void opponentChanceCard(int tileID) throws IOException, InterruptedException {
            System.out.println(CYAN + "Your opponent landed on a chance card tile. They drew a card:");
            switch (ThreadLocalRandom.current().nextInt(1, 17)) {
                case 1:
                    System.out.println("Advance to \"Go\" (Collect $200)\n");
                    Game.opponentNode = Game.board.head;
                    Game.opponentLandedOnGo();
                    break;
                case 2:
                    System.out.println("Advance to Illinois Ave—If you pass Go, collect $200\n");
                    opponentMoveTo(tileID, 24);
                    break;
                case 3:
                    System.out.println("Advance to St. Charles Place - If you pass Go, collect $200\n");
                    opponentMoveTo(tileID, 11);
                    break;
                case 4:
                    System.out.println("Advance token to nearest Utility\n");
                    opponentMoveTo(tileID, 12);
                    break;
                case 5:
                    System.out.println("Advance token to the nearest Railroad\n");
                    if (tileID == 7) {
                        opponentMoveTo(tileID, 15);
                    } else {
                        opponentMoveTo(tileID, 5);
                    }
                    break;
                case 6:
                    System.out.println("Bank pays you dividend of $50\n");
                    opponentAdd(50);
                    break;
                case 7:
                    System.out.println("Get Out of Jail Free - This card may be kept until needed\n");
                    opponent.getOutOfJailFreeCards += 1;
                    break;
                case 8:
                    System.out.println("Go Back 3 Spaces\n");
                    Game.opponentMove(40 - 3);
                    break;
                case 9:
                    System.out.println("Go to Jail - Do not pass Go, do not collect $200\n");
                    Game.opponentGoToJail();
                    break;
                case 10:
                    System.out.println("Make general repairs on all your property - Pay $40 for every property you own\n");
                    opponentDeduct(opponent.properties.size() * 40);
                    break;
                case 11:
                    System.out.println("Pay poor tax of $15\n");
                    opponentDeduct(15);
                    break;
                case 12:
                    System.out.println("Take a trip to Reading Railroad - If you pass Go, collect $200\n");
                    opponentMoveTo(tileID, 5);
                    break;
                case 13:
                    System.out.println("Take a walk on the Boardwalk - Advance to Boardwalk\n");
                    opponentMoveTo(tileID, 39);
                    break;
                case 14:
                    System.out.println("You have been elected Chairman of the Board - Pay each player $50\n");
                    opponentDeduct(50);
                    add(50);
                    break;
                case 15:
                    System.out.println("Your building loan matures - Collect $150\n");
                    opponentAdd(150);
                    break;
                case 16:
                    System.out.println("You have won a crossword competition - Collect $100\n");
                    opponentAdd(100);
                    break;
            }
            System.out.print(RESET);
        }

        private static void communityChest() throws IOException, InterruptedException {
            System.out.println(PURPLE_BOLD + "You landed on a community chest tile. Please draw a card..." + RESET);
            waitForEnter(new String[] {"|", "/", "-", "\\"});
            switch (ThreadLocalRandom.current().nextInt(1, 17)) {
                case 1:
                    System.out.println(GREEN + "Advance to \"Go\" (Collect $200)\n");
                    Game.playerNode = Game.board.head;
                    Game.landedOnGo();
                    break;
                case 2:
                    System.out.println(GREEN + "Bank error in your favor - Collect $200\n");
                    add(200);
                    break;
                case 3:
                    System.out.println(RED + "Doctor's fees - Pay $50\n");
                    deduct(50);
                    break;
                case 4:
                    System.out.println(GREEN + "Get Out of Jail Free - This card may be kept until needed\n");
                    player.getOutOfJailFreeCards += 1;
                    break;
                case 5:
                    System.out.println(RED + "Go to Jail - Do not pass Go, do not collect $200\n");
                    Game.goToJail();
                    break;
                case 6:
                    System.out.println(GREEN + "It's your birthday - Collect $10 from every player\n");
                    opponentDeduct(10);
                    add(10);
                    break;
                case 7:
                    System.out.println(GREEN + "Grand Opera Night - Collect $50 from every player for opening night seats\n");
                    opponentDeduct(50);
                    add(50);
                    break;
                case 8:
                    System.out.println(GREEN + "Income tax refund - Collect $20\n");
                    add(20);
                    break;
                case 9:
                    System.out.println(GREEN + "Life insurance matures - Collect $100\n");
                    add(100);
                    break;
                case 10:
                    System.out.println(RED + "Pay hospital feels of $100\n");
                    deduct(100);
                    break;
                case 11:
                    System.out.println(RED + "Pay school fees of $50\n");
                    deduct(50);
                    break;
                case 12:
                    System.out.println(GREEN + "Receive $25 consulting fee\n");
                    add(25);
                    break;
                case 13:
                    System.out.println(RED + "You are assessed for street repairs - pay $40 for every property you own\n");
                    deduct(player.properties.size() * 40);
                    break;
                case 14:
                    System.out.println(GREEN + "You have won second prize in a beauty contest - Collect $10\n");
                    add(10);
                    break;
                case 15:
                    System.out.println(GREEN + "You inherit $100\n");
                    add(100);
                    break;
                case 16:
                    System.out.println(GREEN + "From sale of stock, you get $50\n");
                    add(50);
                    break;
            }
            System.out.print(RESET);
        }

        private static void opponentCommunityChest() throws IOException, InterruptedException {
            System.out.println(CYAN + "Your opponent landed on a community chest tile. They drew a card...");
            switch (ThreadLocalRandom.current().nextInt(1, 17)) {
                case 1:
                    System.out.println("Advance to \"Go\" (Collect $200)\n");
                    Game.opponentNode = Game.board.head;
                    Game.opponentLandedOnGo();
                    break;
                case 2:
                    System.out.println("Bank error in your favor - Collect $200\n");
                    opponentAdd(200);
                    break;
                case 3:
                    System.out.println("Doctor's fees - Pay $50\n");
                    opponentDeduct(50);
                    break;
                case 4:
                    System.out.println("Get Out of Jail Free - This card may be kept until needed\n");
                    opponent.getOutOfJailFreeCards += 1;
                    break;
                case 5:
                    System.out.println("Go to Jail - Do not pass Go, do not collect $200\n");
                    Game.opponentGoToJail();
                    break;
                case 6:
                    System.out.println("It's your birthday - Collect $10 from every player\n");
                    deduct(10);
                    opponentAdd(10);
                    break;
                case 7:
                    System.out.println("Grand Opera Night - Collect $50 from every player for opening night seats\n");
                    deduct(50);
                    opponentAdd(50);
                    break;
                case 8:
                    System.out.println("Income tax refund - Collect $20\n");
                    opponentAdd(20);
                    break;
                case 9:
                    System.out.println("Life insurance matures - Collect $100\n");
                    opponentAdd(100);
                    break;
                case 10:
                    System.out.println("Pay hospital feels of $100\n");
                    opponentDeduct(100);
                    break;
                case 11:
                    System.out.println("Pay school fees of $50\n");
                    opponentDeduct(50);
                    break;
                case 12:
                    System.out.println("Receive $25 consulting fee\n");
                    opponentAdd(25);
                    break;
                case 13:
                    System.out.println("You are assessed for street repairs - pay $40 for every property you own\n");
                    opponentDeduct(player.properties.size() * 40);
                    break;
                case 14:
                    System.out.println("You have won second prize in a beauty contest - Collect $10\n");
                    opponentAdd(10);
                    break;
                case 15:
                    System.out.println("You inherit $100\n");
                    opponentAdd(100);
                    break;
                case 16:
                    System.out.println("From sale of stock, you get $50\n");
                    opponentAdd(50);
                    break;
            }
            System.out.print(RESET);
        }

        public static void deduct(int money) throws IOException {
            if (player.balance > money) {
                player.balance -= money;
                System.out.printf("Your new balance is $%d.\n", player.balance);
            } else {
                Game.gameFinished(false);
            }
        }

        public static void opponentDeduct(int money) throws IOException {
            if (opponent.balance > money) {
                opponent.balance -= money;
                System.out.printf("Your opponent's new balance is $%d.\n", opponent.balance);
            } else {
                Game.gameFinished(true);
            }
        }

        private static void add(int money) {
            player.balance += money;
            System.out.printf("Your new balance is $%d.\n", player.balance);
        }

        private static void opponentAdd(int money) {
            opponent.balance += money;
            System.out.printf("Your opponent's new balance is $%d.\n", opponent.balance);
        }

        private static void moveTo(int tileID, int targetID) throws IOException, InterruptedException {
            if (tileID > targetID) {
                Game.landedOnGo();
                Game.move((targetID + 40) - tileID);
            } else {
                Game.move(targetID - tileID);
            }
        }

        private static void opponentMoveTo(int tileID, int targetID) throws IOException, InterruptedException {
            if (tileID > targetID) {
                Game.opponentLandedOnGo();
                Game.opponentMove((targetID + 40) - tileID);
            } else {
                Game.opponentMove(targetID - tileID);
            }
        }

        private static void waitForEnter(String[] frames) throws IOException, InterruptedException {
            int i = 0;
            while (System.in.available() == 0) {
                System.out.printf("\rPress \u001B[1;30mEnter%s to continue %s " + Helpers.RESET, Helpers.RESET, frames[i % frames.length]);
                i++;
                Thread.sleep(50);
            }
    
            System.in.read();
            System.out.println();
        }

        private static void miscellaneous(int tileID) throws InterruptedException, IOException {
            switch (tileID) {
                case 0:
                    System.out.println(PURPLE_BOLD + "You are now on the GO tile." + RESET);
                    break;
                case 4:
                    System.out.println(RED + "You have landed on income tax. Please pay $200.");
                    deduct(200);
                    break;
                case 10:
                    System.out.println("You have landed on jail (just visiting).");
                    break;
                case 20:
                    System.out.println("You have landed on free parking.");
                    break;
                case 30:
                    System.out.println(RED + "You landed on a 'GO TO JAIL' tile. You now need to go directly to jail." + RESET);
                    Thread.sleep(500);
                    Game.goToJail();
                    break;
                case 38:
                    System.out.println(RED + "You have landed on luxury tax. Please pay $100.");
                    deduct(100);
                    break;
            }
        }

        private static void opponentMiscellaneous(int tileID) throws InterruptedException, IOException {
            System.out.print(CYAN);
            switch (tileID) {
                case 0:
                    System.out.println("Your opponent is now on the GO tile.");
                    break;
                case 4:
                    System.out.println("Your opponent has landed on income tax. They pay $200.");
                    opponentDeduct(200);
                    break;
                case 10:
                    System.out.println("Your opponent has landed on jail (just visiting).");
                    break;
                case 20:
                    System.out.println("Your opponent has landed on free parking.");
                    break;
                case 30:
                    System.out.println("Your opponent has landed on a 'GO TO JAIL' tile. They go directly to jail.");
                    Thread.sleep(500);
                    Game.opponentGoToJail();
                    break;
                case 38:
                    System.out.println("Your opponent has landed on luxury tax. They pay $100.");
                    opponentDeduct(100);
                    break;
            }
            System.out.print(RESET);
        }

        private static int railroadCount(Player playerObj) {
            int count = 0;
            for (int i = 5; i <= 35; i += 10) {
                if (playerObj.properties.contains(i)) {
                    count++;
                }
            }
            return count;
        }

        private static boolean ask(String question) {
            System.out.print(question);
            String answer = sc.nextLine();
            while (!(answer.equals("y") || answer.equals("n"))) {
                answer = sc.nextLine();
            }
            return answer.equals("y");
        }
    }
}