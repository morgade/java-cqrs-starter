package org.morgade.cqrs.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.morgade.cqrs.Event;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.EventStream;
import org.morgade.cqrs.EventStreamImpl;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, EventStream> eventStreams = new HashMap<>();

    @Override
    public synchronized EventStream loadEventStream(UUID streamId) {
        EventStream eventStream = eventStreams.get(streamId);
        if(eventStream == null) {
            eventStreams.put(
                    streamId, 
                    eventStream = new EventStreamImpl(1, Collections.EMPTY_LIST)
            );
        }
        return eventStream;
    }

    @Override
    public synchronized void store(UUID streamId, long version, List<? extends Event> events) {
        final EventStream eventStream = loadEventStream(streamId);

        if(eventStream.getVersion()!= version) {
            throw new IllegalStateException("Version conflict");
        }

        List<Event> merged = new LinkedList<>();
        eventStream.forEach(merged::add);
        merged.addAll(events);

        eventStreams.put(streamId, new EventStreamImpl(++version, merged));
    }
}
