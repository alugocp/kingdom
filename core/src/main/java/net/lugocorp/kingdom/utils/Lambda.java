package net.lugocorp.kingdom.utils;
import java.util.ArrayList;
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
     * Combines multiple Iterables into one
     */
    public static <A> List<A> flatMap(Iterable<List<A>> sources) {
        List<A> total = new ArrayList<>();
        for (List<A> src : sources) {
            total.addAll(src);
        }
        return total;
    }
}
