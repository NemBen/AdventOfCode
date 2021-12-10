package nemben.aoc.d10;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import nemben.aoc.InputReader;

import java.util.ArrayList;
import java.util.Stack;

public class SyntaxScoring {
  private static final ImmutableSet<Character> OPEN = ImmutableSet.of('(', '[', '{', '<');
  private static final ImmutableSet<Character> CLOSE = ImmutableSet.of(')', ']', '}', '>');

  private static final ImmutableMap<Character, Character> PAIRS =
      ImmutableMap.<Character, Character>builder()
          .put(')', '(')
          .put(']', '[')
          .put('}', '{')
          .put('>', '<')
          .build();

  public static ImmutableList<String> readInput(String name) {
    return InputReader.readSimpleInput(name, scn -> scn.nextLine());
  }

  private static long part1(ImmutableList<String> input) {
    long sum = 0;
    for (String line : input) {
      char illegal = ' ';
      Stack<Character> s = new Stack<>();
      for (char c : line.toCharArray()) {
        if (OPEN.contains(c)) {
          s.add(c);
        } else if (CLOSE.contains(c)) {
          if (s.pop() != PAIRS.get(c)) {
            illegal = c;
            break;
          }
        }
      }
      sum += PART1_VALUES.getOrDefault(illegal, 0);
    }
    return sum;
  }

  private static final ImmutableMap<Character, Integer> PART1_VALUES =
      ImmutableMap.<Character, Integer>builder()
          .put(')', 3)
          .put(']', 57)
          .put('}', 1197)
          .put('>', 25137)
          .build();

  private static long part2(ImmutableList<String> input) {
    ArrayList<Long> values = new ArrayList<>();
    lines:
    for (String line : input) {
      Stack<Character> s = new Stack<>();
      for (char c : line.toCharArray()) {
        if (OPEN.contains(c)) {
          s.add(c);
        } else if (CLOSE.contains(c)) {
          if (s.pop() != PAIRS.get(c)) {
            continue lines;
          }
        }
      }
      values.add(calculateValue(s));
    }
    return values.get(values.size() / 2);
  }

  private static long calculateValue(Stack<Character> s) {
    long v = 0;
    while (!s.isEmpty()) {
      v *= 5;
      v += PART2_VALUES.get(s.pop());
    }
    return v;
  }

  private static final ImmutableMap<Character, Integer> PART2_VALUES =
      ImmutableMap.<Character, Integer>builder()
          .put('(', 1)
          .put('[', 2)
          .put('{', 3)
          .put('<', 4)
          .build();

  public static void main(String[] args) {
    ImmutableList<String> input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
    return;
  }
}
