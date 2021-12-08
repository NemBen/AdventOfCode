package nemben.aoc.d08;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import nemben.aoc.InputReader;

import java.util.Arrays;
import java.util.HashSet;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class SevenSegmentSearch {
  @AutoValue
  public abstract static class Input {
    public abstract ImmutableList<ImmutableSet<Character>> patterns();

    public abstract ImmutableList<ImmutableSet<Character>> numbers();

    public static Input of(
        ImmutableList<ImmutableSet<Character>> patterns,
        ImmutableList<ImmutableSet<Character>> numbers) {
      return new AutoValue_SevenSegmentSearch_Input(patterns, numbers);
    }
  }

  public static ImmutableList<Input> readInput(String name) {
    return InputReader.readSimpleInput(
        name,
        scn -> {
          String[] sides = scn.nextLine().split("\\s\\|\\s");
          return Input.of(
              Arrays.stream(sides[0].split("\\s"))
                  .map(SevenSegmentSearch::toSet)
                  .collect(toImmutableList()),
              Arrays.stream(sides[1].split("\\s"))
                  .map(SevenSegmentSearch::toSet)
                  .collect(toImmutableList()));
        });
  }

  private static ImmutableSet<Character> toSet(String pattern) {
    HashSet<Character> chars = new HashSet<>();
    for (char c : pattern.toCharArray()) {
      chars.add(c);
    }
    return ImmutableSet.copyOf(chars);
  }

  private static int part1(ImmutableList<Input> inputs) {
    int cnt = 0;
    for (Input input : inputs) {
      ImmutableSet<Character> v1 = null, v4 = null, v7 = null, v8 = null;
      for (ImmutableSet<Character> pattern : input.patterns()) {
        switch (pattern.size()) {
          case 2:
            v1 = pattern;
            break;
          case 4:
            v4 = pattern;
            break;
          case 3:
            v7 = pattern;
            break;
          case 7:
            v8 = pattern;
            break;
          default:
        }
      }

      for (ImmutableSet<Character> number : input.numbers()) {
        if (number.equals(v1)) {
          cnt++;
        }
        if (number.equals(v4)) {
          cnt++;
        }
        if (number.equals(v7)) {
          cnt++;
        }
        if (number.equals(v8)) {
          cnt++;
        }
      }
    }
    return cnt;
  }

  private static int part2(ImmutableList<Input> inputs) {
    int sum = 0;
    for (Input input : inputs) {
      ImmutableSet<Character> v1 = null,
          v4 = null,
          v069_1 = null,
          v069_2 = null,
          v069_3 = null,
          v7 = null,
          v8 = null;
      for (ImmutableSet<Character> pattern : input.patterns()) {
        switch (pattern.size()) {
          case 2:
            v1 = pattern;
            break;
          case 4:
            v4 = pattern;
            break;
          case 3:
            v7 = pattern;
            break;
          case 6:
            if (v069_1 == null) {
              v069_1 = pattern;
            } else if (v069_2 == null) {
              v069_2 = pattern;
            } else {
              v069_3 = pattern;
            }
            break;
          case 7:
            v8 = pattern;
            break;
          default:
        }
      }

      char a = Iterables.getOnlyElement(Sets.difference(v7, v1));
      char c;
      char d;
      char e;
      if (v069_1.containsAll(v4)) {
        e = Iterables.getOnlyElement(Sets.difference(v8, v069_1));
        if (v069_2.containsAll(v7)) {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_3));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_2));
        } else {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_2));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_3));
        }
      } else if (v069_2.containsAll(v4)) {
        e = Iterables.getOnlyElement(Sets.difference(v8, v069_2));
        if (v069_1.containsAll(v7)) {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_3));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_1));
        } else {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_1));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_3));
        }
      } else {
        e = Iterables.getOnlyElement(Sets.difference(v8, v069_3));
        if (v069_1.containsAll(v7)) {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_2));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_1));
        } else {
          c = Iterables.getOnlyElement(Sets.difference(v8, v069_1));
          d = Iterables.getOnlyElement(Sets.difference(v8, v069_2));
        }
      }
      char f = Iterables.getOnlyElement(Sets.difference(v1, ImmutableSet.of(c)));
      char b = Iterables.getOnlyElement(Sets.difference(v4, ImmutableSet.of(c, d, f)));
      char g = Iterables.getOnlyElement(Sets.difference(v8, ImmutableSet.of(a, b, c, d, e, f)));

      ImmutableSet<Character>[] values = new ImmutableSet[10];
      values[0] = ImmutableSet.of(a, b, c, e, f, g);
      values[1] = ImmutableSet.of(c, f);
      values[2] = ImmutableSet.of(a, c, d, e, g);
      values[3] = ImmutableSet.of(a, c, d, f, g);
      values[4] = ImmutableSet.of(b, c, d, f);
      values[5] = ImmutableSet.of(a, b, d, f, g);
      values[6] = ImmutableSet.of(a, b, d, e, f, g);
      values[7] = ImmutableSet.of(a, c, f);
      values[8] = ImmutableSet.of(a, b, c, d, e, f, g);
      values[9] = ImmutableSet.of(a, b, c, d, f, g);

      int display = 0;
      for (ImmutableSet<Character> number : input.numbers()) {
        int i;
        for (i = 0; i < 9; ++i) {
          if (values[i].equals(number)) {
            break;
          }
        }
        display *= 10;
        display += i;
      }
      sum += display;
    }
    return sum;
  }

  public static void main(String[] args) {
    ImmutableList<Input> input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
    return;
  }
}
