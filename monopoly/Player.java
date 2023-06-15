package housemonopoly;

import java.util.*;

import Helpers.Helpers;
import Helpers.Helpers.*;

public class Player {
    public int balance = 1500;
    public ArrayList<Integer> properties = new ArrayList<Integer>();
    public int getOutOfJailFreeCards = 0;
    public boolean inJail = false;

    public static void main(String[] args) {
        return;
    }

    public boolean purchaseTile(int tileID) {
        int tilePrice = Tiles.purchasablePrices.get(tileID);
        if (tilePrice < this.balance) {
            this.balance -= tilePrice;
            properties.add(tileID);
            Helpers.Tiles.indexTypeDict.put(tileID, 1);
            return true;
        }
        return false;
    }
}