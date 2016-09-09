package org.morgade.tab.event;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Event;

/**
 *
 * @author x4rb
 */
public class FoodServed implements Event {
    public final UUID id;
    public final List<Integer> menuNumbers;

    public FoodServed(UUID id, List<Integer> menuNumbers) {
        this.id = id;
        this.menuNumbers = Collections.unmodifiableList(menuNumbers);
    }
    
    @Override
    public UUID getId() {
        return id;
    }

}
