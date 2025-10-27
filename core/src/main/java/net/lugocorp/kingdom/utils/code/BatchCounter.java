package net.lugocorp.kingdom.utils.code;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class allows us to batch any list into more easily processed chunks
 */
public class BatchCounter<T> {
    private final List<T> data;
    private final int max;
    private int index = 0;

    public BatchCounter(int max, List<T> list) {
        this.data = list;
        this.max = max;
    }

    public BatchCounter(int max) {
        this(max, new ArrayList<>());
    }

    /**
     * Returns the underlying list for this instance
     */
    public List<T> list() {
        return this.data;
    }

    /**
     * Registers that an element(s) was removed from the underlying list
     */
    public void removed(int n) {
        if (this.index > 0) {
            this.index -= n;
        }
    }

    /**
     * Returns a subset of the underlying list (no larger than the specified max
     * length)
     */
    public Iterable<T> getBatch() {
        final int n = Math.min(this.data.size() - this.index, this.max);
        final BatchCounter<T> that = this;
        return new Iterable<T>() {
            /** {@inheritdoc} */
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int counter = 0;

                    /** {@inheritdoc} */
                    @Override
                    public boolean hasNext() {
                        return this.counter < n;
                    }

                    /** {@inheritdoc} */
                    @Override
                    public T next() {
                        counter++;
                        final T element = that.data.get(that.index++);
                        if (that.index == that.data.size()) {
                            that.index = 0;
                        }
                        return element;
                    }
                };
            }
        };
    }
}
