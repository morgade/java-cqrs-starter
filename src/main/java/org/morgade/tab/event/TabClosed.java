package org.morgade.tab.event;

import java.util.UUID;
import org.morgade.cqrs.Event;

/**
 *
 * @author x4rb
 */
public class TabClosed implements Event {
    public final UUID id;
    public final float amountPaid;
    public final float orderValue;
    public final float tipValue;

    public TabClosed(UUID id, float amountPaid, float orderValue, float tipValue) {
        this.id = id;
        this.amountPaid = amountPaid;
        this.orderValue = orderValue;
        this.tipValue = tipValue;
    }

    @Override
    public UUID getId() {
        return id;
    }

}
