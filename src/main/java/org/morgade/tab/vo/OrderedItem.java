package org.morgade.tab.vo;

/**
 *
 * @author x4rb
 */
public class OrderedItem {
    public final int menuNumber;
    public final String description;
    public final boolean isDrink;
    public final float price;

    public OrderedItem(int menuNumber, String description, float price, boolean isDrink) {
        this.menuNumber = menuNumber;
        this.description = description;
        this.isDrink = isDrink;
        this.price = price;
    }
}
