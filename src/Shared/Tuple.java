package Shared;

import java.io.Serializable;

public class Tuple<T1, T2> implements Serializable{
    private final T1 first;
    private final T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 fst() {
        return first;
    }

    public T2 snd() {
        return second;
    }

    @Override
    public String toString() {
        return "( " + this.first.toString() + ", " + this.second.toString() + ")";
    }
}
