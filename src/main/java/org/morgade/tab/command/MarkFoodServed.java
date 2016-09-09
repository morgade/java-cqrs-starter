package org.morgade.tab.command;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Command;

/**
 *
 * @author x4rb
 */
public class MarkFoodServed implements Command {
    public UUID id;
    public List<Integer> menuNumbers;

    public MarkFoodServed(UUID id, List<Integer> menuNumbers) {
        this.id = id;
        this.menuNumbers = Collections.unmodifiableList(menuNumbers);
    }

    @Override
    public UUID getId() {
        return id;
    }
    
}
