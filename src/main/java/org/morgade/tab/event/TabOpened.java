package org.morgade.tab.event;

import java.util.UUID;
import org.morgade.cqrs.Event;

/**
 *
 * @author x4rb
 */
public class TabOpened extends Event {
    public final int tableNumber;
    public final String waiter;

    public TabOpened(UUID id, int tableNumber, String waiter) {
        super(id);
        this.tableNumber = tableNumber;
        this.waiter = waiter;
    }

}
