package org.morgade.tab.projection.tabs;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author x4rb
 */
public interface Tabs {

    List<Integer> activeTableNumbers();

    TabInvoice invoiceForTable(int table);

    TabStatus tabForTable(int table);

    UUID tabIdForTable(int table);

    Map<Integer, List<TabItem>> todoListForWaiter(String waiter);
    
}
