package org.morgade.tab.event;

import java.util.UUID;
import org.morgade.cqrs.Event;

/**
 *
 * @author x4rb
 */
public class TabOpened implements Event {
    public final UUID id;
    public final int tableNumber;
    public final String waiter;

    public TabOpened(UUID id, int tableNumber, String waiter) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.waiter = waiter;
    }

    @Override
    public UUID getId() {
        return id;
    }

}
