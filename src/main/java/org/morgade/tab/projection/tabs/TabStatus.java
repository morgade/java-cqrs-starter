package org.morgade.tab.projection.tabs;

import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author x4rb
 */
public class TabStatus {
    public final UUID tabId;
    public final int tableNumber;
    public final List<TabItem> toServe;
    public final List<TabItem> inPreparation;
    public final List<TabItem> served;

    public TabStatus(UUID tabId, int tableNumber, List<TabItem> toServe, List<TabItem> inPreparation, List<TabItem> served) {
        this.tabId = tabId;
        this.tableNumber = tableNumber;
        this.toServe = unmodifiableList(new ArrayList(toServe));
        this.inPreparation = unmodifiableList(new ArrayList(inPreparation));
        this.served = unmodifiableList(new ArrayList(served));
    }

}
