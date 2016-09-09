package org.morgade.cqrs;

/**
 *
 * @author x4rb
 */
public interface EventStream extends Iterable<Event> {
    long getVersion();
}
