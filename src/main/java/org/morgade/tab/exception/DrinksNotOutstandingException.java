package org.morgade.tab.exception;

/**
 *
 * @author x4rb
 */
public class DrinksNotOutstandingException extends RuntimeException {
    public DrinksNotOutstandingException() {
        super("Drinks are not outsanding");
    }
}
