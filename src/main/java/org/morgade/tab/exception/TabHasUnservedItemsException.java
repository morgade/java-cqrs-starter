package org.morgade.tab.exception;

/**
 *
 * @author x4rb
 */
public class TabHasUnservedItemsException extends RuntimeException {

    public TabHasUnservedItemsException() {
        super("Tab has unserved items");
    }
    
}
