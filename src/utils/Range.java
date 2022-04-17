package utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Range(int start, int end) {
    /**
     * @param start inclusive
     * @param end   exclusive
     */
    public Range {
    }

    public static Range from(Collection<?> collection) {
        return new Range(0, collection.size());
    }

    @SafeVarargs
    public static <T> Range from(T... arr) {
        return new Range(0, arr.length);
    }

    public Iterator<Integer> iterator() {
        return stream().iterator();
    }

    public IntStream intStream() {
        return IntStream.range(start, end);
    }

    public Stream<Integer> stream() {
        return intStream().boxed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range range)) return false;
        return start == range.start && end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
