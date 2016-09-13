package org.morgade.tab.exception;

/**
 *
 * @author x4rb
 */
public class TabNotOpenException extends RuntimeException {

    public TabNotOpenException() {
        super("Tab not open");
    }
    
}
