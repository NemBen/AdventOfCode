package nemben.aoc;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Function;

public class InputReader {
    public static <T> ImmutableList<T> readSimpleInput(String name, Function<Scanner, T> readFn) {
        try (Scanner scn = new Scanner(getInputStream(name))) {
            ImmutableList.Builder<T> builder = new ImmutableList.Builder<>();
            while (scn.hasNext()) {
                builder.add(readFn.apply(scn));
            }
            return builder.build();
        }
    }

    private static InputStream getInputStream(String name) {
        return InputReader.class.getClassLoader().getResourceAsStream(name);
    }
}
