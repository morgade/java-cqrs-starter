package org.morgade.cqrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EventStreamImpl implements EventStream {
    private final long version;
    private final List<Event> events;

    public EventStreamImpl(long version, List<Event> events) {
        this.version = version;
        this.events = Collections.unmodifiableList(new ArrayList<Event>(events));
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }
}
