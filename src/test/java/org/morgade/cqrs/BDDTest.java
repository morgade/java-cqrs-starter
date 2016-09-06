package org.morgade.cqrs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;

/**
 * Provides infrastructure for a set of tests on a given aggregate.
 *
 * @author x4rb
 * @param <A>
 */
public abstract class BDDTest<A extends Aggregate> {

    private final Class<A> sutClass;
    private A sut;

    public BDDTest(Class<A> sutClass) {
        this.sutClass = sutClass;
    }

    @Before
    public void bddSetup() {
        try {
            sut = sutClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected void test(List<? extends Event> given, Function<A, Object> whenFunction, Consumer thenConsumer) {
        thenConsumer.accept(whenFunction.apply(applyEvents(sut, given)));
    }

    protected Consumer<Object> then(Object... expectedEvents) {
        return (got) -> {
            if (got instanceof List) {
                List expectedEventsList = Arrays.asList(expectedEvents);
                assertStreamEquals(String.format("Excpected events mismatch. Got: %s Expected: %s",  got, expectedEventsList),
                        ((List)got).stream(), 
                        expectedEventsList.stream());
            } else if (got instanceof Throwable) {
                ((Throwable)got).printStackTrace(System.out);
                Assert.fail(got.toString());
            } else {
                Assert.fail("Expected iterable of events, but got exception " + got.getClass().getName());
            }
        };
    }

    private static void assertStreamEquals(String msg, Stream<?> s1, Stream<?> s2) {
        Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            Assert.assertTrue(msg, EqualsBuilder.reflectionEquals(iter1.next(), iter2.next()));
        }
        
        Assert.assertTrue(msg, !iter1.hasNext() && !iter2.hasNext() );
    }

    protected Consumer thenFailWith(Class exceptionClass) {
        return (got) -> {
            Assert.assertTrue(
                    String.format("Expected exception %s, but got %s",
                            exceptionClass.getName(),
                            got.getClass().getName()),
                    got.getClass().equals(exceptionClass)
            );
        };
    }

    protected <C extends Command, A extends Aggregate> Function<A, Object> when(C command) {
        return (agg) -> {
            try {
                return agg.dispatch(command);
            } catch (Exception e) {
                return e;
            }
        };
    }

    protected List<Event> given(Event ... events) {
        return Arrays.asList(events);
    }

    private A applyEvents(A agg, List<? extends Event> events) {
        agg.dispatch(events);
        return agg;
    }

}
