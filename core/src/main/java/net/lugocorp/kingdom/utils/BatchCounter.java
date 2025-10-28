package net.lugocorp.kingdom.utils;
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
     * Returns true if the next batch is the last one in this iteration of the data
     */
    public boolean isLastBatch() {
        return this.index + this.getBatchSize() == this.data.size();
    }

    /**
     * Returns the size of the next batch
     */
    private int getBatchSize() {
        return Math.min(this.data.size() - this.index, this.max);
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
     * Registers that an element at the given index was removed (decrements the
     * index if necessary)
     */
    public void processRemoval(int i) {
        if (i < this.index) {
            this.index--;
        } else if (this.index == this.data.size()) {
            this.index = 0;
        }
    }

    /**
     * Calls into processRemoval()
     */
    public void processRemoval(T element) {
        this.processRemoval(this.data.indexOf(element));
    }

    /**
     * Returns a subset of the underlying list (no larger than the specified max
     * length)
     */
    public Iterable<T> getBatch() {
        final int n = this.getBatchSize();
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
