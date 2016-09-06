package org.morgade.tab.event;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Event;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class DrinksOrdered implements Event {
    public final UUID id;
    public final List<OrderedItem> items;

    public DrinksOrdered(UUID id, List<OrderedItem> items) {
        this.id = id;
        this.items = Collections.unmodifiableList(items);
    }

    @Override
    public UUID getId() {
        return id;
    }
}
