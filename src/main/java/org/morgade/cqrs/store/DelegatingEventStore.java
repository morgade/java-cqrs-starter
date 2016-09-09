package org.morgade.cqrs.store;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Event;
import org.morgade.cqrs.EventReceiver;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.EventStream;

public class DelegatingEventStore implements EventStore {

    private final EventStore eventStore;
    private final List<EventReceiver> listeners;

    public DelegatingEventStore(EventStore eventStore, EventReceiver ... listeners) {
        this.eventStore = eventStore;
        this.listeners = Collections.unmodifiableList(Arrays.asList(listeners));
    }

    @Override
    public EventStream loadEventStream(UUID streamId) {
        return eventStore.loadEventStream(streamId);
    }

    @Override
    public synchronized void store(UUID streamId, long version, List<? extends Event> events) {
        eventStore.store(streamId, version, events);

        listeners.forEach(
            (l) -> events.forEach(l::dispatch)
        );
    }
}
