package org.morgade.tab.projection.tabs;

/**
 *
 * @author x4rb
 */
public class TabItem {
    public final int menuNumber;
    public final String description;
    public final float price;

    public TabItem(int menuNumber, String description, float price) {
        this.menuNumber = menuNumber;
        this.description = description;
        this.price = price;
    }
    
}
