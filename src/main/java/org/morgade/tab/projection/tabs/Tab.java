package org.morgade.tab.projection.tabs;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author x4rb
 */
public class Tab {
    public int tableNumber;
    public String waiter;
    public final List<TabItem> toServe = new LinkedList<>();
    public final List<TabItem> inPreparation = new LinkedList<>();
    public final List<TabItem> served = new LinkedList<>();

    public Tab(int tableNumber, String waiter) {
        this.tableNumber = tableNumber;
        this.waiter = waiter;
    }
    
}
