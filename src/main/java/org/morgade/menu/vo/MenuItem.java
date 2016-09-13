package org.morgade.menu.vo;

/**
 *
 * @author x4rb
 */
public class MenuItem {
    private final int menuNumber;
    private final String description;
    private final float price;
    private final boolean isDrink;

    public MenuItem(int menuNumber, String description, float price, boolean isDrink) {
        this.menuNumber = menuNumber;
        this.description = description;
        this.price = price;
        this.isDrink = isDrink;
    }

    public MenuItem(int menuNumber, String description, float price) {
        this.menuNumber = menuNumber;
        this.description = description;
        this.price = price;
        this.isDrink = false;
    }
    
    public int getMenuNumber() {
        return menuNumber;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public boolean isDrink() {
        return isDrink;
    }
    
}
