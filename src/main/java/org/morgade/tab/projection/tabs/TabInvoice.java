package org.morgade.tab.projection.tabs;

import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author x4rb
 */
public class TabInvoice {
    public final UUID tabId;
    public final int tableNumber;
    public final List<TabItem> items;
    public final float total;
    public final boolean hasUnservedItems;

    public TabInvoice(UUID tabId, int tableNumber, List<TabItem> items, float total, boolean hasUnservedItems) {
        this.tabId = tabId;
        this.tableNumber = tableNumber;
        this.items = unmodifiableList(new ArrayList(items));
        this.total = total;
        this.hasUnservedItems = hasUnservedItems;
    }
    
    
}
