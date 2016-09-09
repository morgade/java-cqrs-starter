package org.morgade.tab.projection.chef;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author x4rb
 */
public class ChefTodoListGroup {
    public final UUID tab;
    public final List<ChefTodoListItem> items;

    public ChefTodoListGroup(UUID tab, List<ChefTodoListItem> items) {
        this.tab = tab;
        this.items = items;
    }
    
}
