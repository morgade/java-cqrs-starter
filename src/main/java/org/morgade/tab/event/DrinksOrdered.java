package org.morgade.tab.event;

import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Event;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class DrinksOrdered extends Event {
    public final List<OrderedItem> items;

    public DrinksOrdered(UUID id, List<OrderedItem> items) {
        super(id);
        this.items = items;
    }
}
