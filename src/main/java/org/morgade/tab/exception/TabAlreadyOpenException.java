package org.morgade.tab.exception;

/**
 *
 * @author x4rb
 */
public class TabAlreadyOpenException extends RuntimeException {

    public TabAlreadyOpenException() {
        super("A tab is already open");
    }
    
}
