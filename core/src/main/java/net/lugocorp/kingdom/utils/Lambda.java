package net.lugocorp.kingdom.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO you can probably one day replace this with Collections or something

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
     * Converts the given Collection into a List
     */
    public static <A> List<A> toList(Collection<A> c) {
        final List<A> ls = new ArrayList<>();
        ls.addAll(c);
        return ls;
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
     * Returns a subset of the input List where each element passes the criteria
     */
    public static <A> List<A> filter(Function<A, Boolean> criteria, List<A> input) {
        List<A> output = new ArrayList<>();
        for (A a : input) {
            if (criteria.apply(a)) {
                output.add(a);
            }
        }
        return output;
    }

    /**
     * Returns true if some element in the Iterable meets the criteria
     */
    public static <A> boolean some(Function<A, Boolean> criteria, Iterable<A> input) {
        for (A a : input) {
            if (criteria.apply(a)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if some element in the Iterable meets the criteria
     */
    public static <A> Optional<A> find(Function<A, Boolean> criteria, Iterable<A> input) {
        for (A a : input) {
            if (criteria.apply(a)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    /**
     * Folds the values from input using the given modifier function
     */
    public static <A, B> B fold(BiFunction<B, A, B> modifier, B initial, Iterable<A> input) {
        B value = initial;
        for (A a : input) {
            value = modifier.apply(value, a);
        }
        return value;
    }

    /**
     * Sorts the given List by some scoring criteria, from largest to smallest
     */
    public static <A> List<A> sort(Function<A, Integer> score, List<A> input) {
        List<Tuple<A, Integer>> tuples = Lambda.map((A a) -> new Tuple(a, score.apply(a)), input);
        Collections.sort(tuples, (Tuple<A, Integer> a, Tuple<A, Integer> b) -> b.b - a.b);
        return Lambda.map((Tuple<A, Integer> t) -> t.a, tuples);
    }

    /**
     * Returns true if a coin flip lands with the given chance of success
     */
    public static boolean chance(int percentage) {
        return (int) Math.floor(100 * Math.random()) < percentage;
    }

    /**
     * Returns a random value from the given Enum
     */
    public static <T extends Enum<T>> T random(Class<T> e) {
        T[] values = e.getEnumConstants();
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /**
     * Returns a random element from the given List
     */
    public static <T> T random(List<T> ls) {
        return ls.get((int) Math.floor(Math.random() * ls.size()));
    }

    /**
     * Returns a random element from the given Set
     */
    public static <T> T random(Set<T> s) {
        final int index = (int) Math.floor(Math.random() * s.size());
        final Iterator<T> iterator = s.iterator();
        for (int a = 0; a < index; a++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * Returns a random subset of the given Set
     */
    public static <T> Set<T> subset(int n, Set<T> population) {
        final List<T> copy = new ArrayList<>();
        final Set<T> subset = new HashSet<>();
        copy.addAll(population);
        for (int a = 0; a < Math.min(n, population.size()); a++) {
            int i = (int) Math.floor(Math.random() * copy.size());
            subset.add(copy.remove(i));
        }
        return subset;
    }
}
