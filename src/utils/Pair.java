package utils;

public record Pair<X, Y>(X first, Y second) {
    public static <X, Y> Pair<X, Y> empty() {
        return new Pair<>(null, null);
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }
}
