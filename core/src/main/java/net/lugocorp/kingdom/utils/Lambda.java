package net.lugocorp.kingdom.utils;
import java.util.List;
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
}
