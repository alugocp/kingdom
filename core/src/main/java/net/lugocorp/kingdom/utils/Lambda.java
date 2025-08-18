package net.lugocorp.kingdom.utils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A collection of functional programming convenience methods
 */
public class Lambda {

    /**
     * This function maps a List of one type into a List of another type
     */
    public static <A, B> List<B> map(Function<A, B> lambda, List<A> input) {
        return input.stream().map((A x) -> lambda.apply(x)).collect(Collectors.toList());
    }

    /**
     * This function maps a Set of one type into a Set of another type
     */
    public static <A, B> Set<B> map(Function<A, B> lambda, Set<A> input) {
        return input.stream().map((A x) -> lambda.apply(x)).collect(Collectors.toSet());
    }

    /**
     * Returns a subset of the input Set where each element passes the criteria
     */
    public static <A> Set<A> filter(Function<A, Boolean> criteria, Set<A> input) {
        Set<A> output = new HashSet<>();
        for (A a : input) {
            if (criteria.apply(a)) {
                output.add(a);
            }
        }
        return output;
    }

    /**
     * Returns a random value from the given Enum
     */
    public static <T extends Enum<T>> T random(Class<T> e) {
        T[] values = e.getEnumConstants();
        return values[(int) Math.floor(Math.random() * values.length)];
    }
}
