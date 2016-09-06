package org.morgade.cqrs;

import java.util.UUID;

/**
 *
 * @author x4rb
 */
public abstract class Event {
    public final UUID id;

    public Event(UUID id) {
        this.id = id;
    }
}
