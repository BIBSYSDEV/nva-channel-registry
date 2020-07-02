package no.unit.nva.channel;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {
    public static <T extends Iterable<T>> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
