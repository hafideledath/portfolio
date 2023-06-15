package housemonopoly;

import Helpers.Helpers.*;
import Player.Player;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import Game.Game;

public class BoardLinkedList {
    public static ListNode head;
    private static String[] tileArray = Constants.tileArray();
    private static List<String> tileList = Arrays.asList(tileArray);
    private static ArrayList<Integer> boardLoopIndexes = loopIndexes(11);
    public static String filename = "board.md";
    public class ListNode {
        public ListNode next;
        public int id;

        public ListNode(int id) {
            this.id = id;
        }
    }
    ListNode currentNode = this.head;
    private static String[][] boardMatrix = new String[11][11];

    public static void main(String[] args) {
        return;
    }

    public static void initializeMatrix() {
        for (int i = 1; i < 10; i++) {
            Arrays.fill(boardMatrix[i], " ");
        }
            
        for (int i = 0; i < 11; i++) {
            boardMatrix[0][i] = tileArray[boardLoopIndexes.get(i)];
        }
    
        int n = 0;
    
        for (int i = 1; i <= 10; i++) {
            boardMatrix[i][0] = tileArray[boardLoopIndexes.get(n + 11)];
            boardMatrix[i][10] = tileArray[boardLoopIndexes.get(n + 12)];
            n += 2;
        }
    
        for (int i = 0; i < 11; i++) {
            boardMatrix[10][i] = tileArray[boardLoopIndexes.get(i + 29)];
        }
    }

    public void initialize() {
        ListNode current = new ListNode(0);
        head = current;

        for (int i = 1; i < 40; i++) {
            current.next = new ListNode(current.id + 1);
            current = current.next;
        }

        current.next = head;

        initializeMatrix();
    }

    @Override
    public String toString() {
        try {
            printTable(boardMatrix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    public static void printTable(String[][] matrix) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);

        int[] playerTileCoordinates = findMatrixCoordinates(matrix, tileArray[Game.playerNode.id]);
        int[] opponentTileCoordinates = findMatrixCoordinates(matrix, tileArray[Game.opponentNode.id]);

        int[] maxColumnWidths = new int[matrix[0].length];
        int boardIndex = 0;

        for (int i = 0; i < matrix.length; i++) {
            String[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                int extra = 0;
                if (Arrays.equals(playerTileCoordinates, new int[] {i, j})) {
                    extra += 4;
                }
                if (Arrays.equals(opponentTileCoordinates, new int[] {i, j})) {
                    extra += 9;
                }

                int length = 0;
                if (getInfo(row[j]).contains("you")) {
                    length = 12;
                } else {
                    length = 17;
                }

                maxColumnWidths[j] = Math.max(maxColumnWidths[j], Math.max(length, row[j].length() + extra));
            }
        }

        fileWriter.write("""
            <style>
            .logo {
                white-space: pre;
                font-family: courier;
                font-weight: 800;
                font-size: 16px;
                line-height: 20px;
                color: rgb(255, 120, 120);
                display: block;
                position: absolute;
                top: 130px;
                left: calc(500px + 25 * 3px);
            }
        
            .stat-container {
                position: absolute;
                top: 320px;
                display: flex;
                left: calc(370px + 25 * 3px);
                margin-top: 50px;
            }
        
            .stats {
                font-family: Courier;
                text-align: center;
                margin-right: 100px;
                width: 500px;
            }
        
            .stats h2 {
                margin-bottom: 20px;
                font-weight: 800;
                color: rgba(170, 240, 255, 0.5);
            }
        
            .stats h3 {
                line-height: 30px;
                font-size: 20px;
                color: rgba(170, 240, 255, 0.5);
            }
        
            p {
                filter: brightness(2);
                white-space: pre; 
                font-family: Courier;
                 margin-bottom: 50px;
            }
            </style>
            
            <div class=\"logo\">
             /$$   /$$  /$$$$$$  /$$$$$$$$ /$$$$$$ /$$$$$$$   /$$$$$$  /$$$$$$$   /$$$$$$  /$$   /$$     /$$
            | $$  | $$ /$$__  $$| $$_____/|_  $$_/| $$__  $$ /$$__  $$| $$__  $$ /$$__  $$| $$  |  $$   /$$/
            | $$  | $$| $$  \\ $$| $$        | $$  | $$  \\ $$| $$  \\ $$| $$  \\ $$| $$  \\ $$| $$   \\  $$ /$$/
            | $$$$$$$$| $$$$$$$$| $$$$$     | $$  | $$  | $$| $$  | $$| $$$$$$$/| $$  | $$| $$    \\  $$$$/
            | $$__  $$| $$__  $$| $$__/     | $$  | $$  | $$| $$  | $$| $$____/ | $$  | $$| $$     \\  $$/
            | $$  | $$| $$  | $$| $$        | $$  | $$  | $$| $$  | $$| $$      | $$  | $$| $$      | $$
            | $$  | $$| $$  | $$| $$       /$$$$$$| $$$$$$$/|  $$$$$$/| $$      |  $$$$$$/| $$$$$$$$| $$
            |__/  |__/|__/  |__/|__/      |______/|_______/  \\______/ |__/       \\______/ |________/|__/
            </div><p>""");
            
    
        StringBuilder edgeBorder = new StringBuilder("+");
        for (int i = 0; i < maxColumnWidths.length; i++) {
            edgeBorder.append("-".repeat(maxColumnWidths[i] + 2)).append("+");
        }

        StringBuilder middleBorder = new StringBuilder("+");
        for (int i = 0; i < maxColumnWidths.length; i++) {
            if (i == 0 || i == 10) {
                middleBorder.append("-".repeat(maxColumnWidths[i] + 2)).append("+");
            } else if (i == 9) {
                middleBorder.append(" ".repeat(maxColumnWidths[i] + 2)).append("+");
            } else {
                middleBorder.append(" ".repeat(maxColumnWidths[i] + 2)).append(" ");
            }
        }

        fileWriter.write(edgeBorder + "\n");
        for (int i = 0; i < matrix[0].length; i++) {
            String[] row = matrix[i];
            StringBuilder line = new StringBuilder("|");
            StringBuilder additionalLine = new StringBuilder("|");
            for (int j = 0; j < row.length; j++) {
                String element = row[j];
                String elementInfo = getInfo(element);

                if (Arrays.equals(playerTileCoordinates, new int[] {i, j})) {
                    element = element + " <span style=\"color: green;\">You</span>";
                }
                if (Arrays.equals(opponentTileCoordinates, new int[] {i, j})) {
                    element = element + " <span style=\"color: red;\">Opponent</span>";
                }

                line.append(" ").append(element);
                additionalLine.append(" ").append(elementInfo);

                int elementLength = element.length();
                int elementInfoLength = elementInfo.length();

                if (element.contains("<")) {
                    if (element.contains("You")) {
                        elementLength -= 35;
                    }
                    if (element.contains("Opponent")) {
                        elementLength -= 33;
                    }
                }

                if (elementInfo.contains("<")) {
                    if (elementInfo.contains("you")) {
                        elementInfoLength = 12;
                    }
                    if (elementInfo.contains("opponent")) {
                        elementInfoLength = 17;
                    }
                }

                line.append(" ".repeat(maxColumnWidths[j] - elementLength + 1));
                additionalLine.append(" ".repeat(maxColumnWidths[j] - elementInfoLength + 1));
                if (j == 0 || j == 9 || j == 10 || i == 0 || i == 10) {
                    line.append("|");
                    additionalLine.append("|");
                } else {
                    line.append(" ");
                    additionalLine.append(" ");
                }
            }
            fileWriter.write(line + "\n");
            fileWriter.write(additionalLine + "\n");
            if (i == 0 || i == 9 || i == 10) {
                fileWriter.write(edgeBorder + "\n");
            } else {
                fileWriter.write(middleBorder + "\n");
            }
        }
        
        fileWriter.write("<div class=\"stat-container\">");

        for (Player player : new Player[] {Game.player, Game.opponent}) {
            String playerName = (player.equals(Game.player) ? "Your" : "Opponent's");

            ArrayList<String> properties = new ArrayList<String>();
            for (int property : player.properties) {
                properties.add(tileArray[property]);
            }

            String propertiesString = String.join(", ", properties);
            if (propertiesString.isBlank()) {
                propertiesString = "None yet";
            }

            fileWriter.write(String.format("<div class=\"stats\"><h2>%s Stats:</h2><h3>Balance: $%d</h3><h3>Properties: %s</h3></div>", playerName, player.balance, propertiesString));
        }
        fileWriter.write("</div>");

        fileWriter.flush();
        fileWriter.close();
    }

    private static String getInfo(String tile) {
        int tileIndex = tileList.indexOf(tile);
        if (Game.player.properties.contains(tileIndex)) {
            return "<span style=\"color: green;\">Owned by you</span>";
        } else if (Game.opponent.properties.contains(tileIndex)) {
            return "<span style=\"color: red;\">Owned by opponent</span>";
        } else {
            return "";
        }
    }

    private static int[] findMatrixCoordinates(String[][] matrix, String target) {
        int i = 0;
        int j = 0;

        for (String[] row : matrix) {
            for (String element : row) {
                if (element == target) {
                    return new int[] {i, j};
                }
                j++;
            }
            i++;
            j = 0;
        }
        return new int[] {i, j};
    }
    

    private static ArrayList<Integer> loopIndexes(int sideLength) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i = 0; i < sideLength; i++) {
            result.add(i);
        }

        for (int i = 0; i < sideLength - 2; i++) {
            result.add(4 * sideLength - 5 - i);
            result.add(sideLength + i);
        }

        for (int i = (3 * sideLength) - 3; i >= (2 * sideLength) - 2; i--) {
            result.add(i);
        }
        
        Collections.reverse(result);

        return result;
    }
}