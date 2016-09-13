package org.morgade.tab.exception;

/**
 *
 * @author x4rb
 */
public class FoodNotOutstandingException extends RuntimeException {
    public FoodNotOutstandingException() {
        super("Food not outstanding");
    }
}
