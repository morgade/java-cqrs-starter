package org.morgade.cqrs;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author x4rb
 */
public interface EventStore {
    EventStream loadEventStream(UUID id);
    void store(UUID id, long version, List<? extends Event> newEvents);
}
