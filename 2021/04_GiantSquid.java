package nemben.aoc.d04;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import nemben.aoc.InputReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class GiantSquid {

  private static class Input {
    final ImmutableList<Integer> numbers;
    final ImmutableList<int[][]> cards;

    private Input(ImmutableList<Integer> numbers, ImmutableList<int[][]> cards) {
      this.numbers = numbers;
      this.cards = cards;
    }
  }

  private static Input readInput(String name) {
    return InputReader.readInput(
        name,
        scn -> {
          ImmutableList<Integer> numbers =
              Arrays.stream(scn.next().split(","))
                  .map(Integer::parseInt)
                  .collect(collectingAndThen(toList(), ImmutableList::copyOf));

          ImmutableList.Builder<int[][]> builder = ImmutableList.builder();
          while (scn.hasNext()) {
            int[][] card = new int[5][5];
            for (int i = 0; i < 5; ++i) {
              for (int j = 0; j < 5; ++j) {
                card[i][j] = scn.nextInt();
              }
            }
            builder.add(card);
          }
          ImmutableList<int[][]> cards = builder.build();

          return new Input(numbers, cards);
        });
  }

  private static class Position {
    final int boardIdx;
    final int x;
    final int y;

    Position(int boardIdx, int x, int y) {
      this.boardIdx = boardIdx;
      this.x = x;
      this.y = y;
    }
  }

  private static ImmutableMultimap<Integer, Position> buildPositions(ImmutableList<int[][]> cards) {
    ArrayListMultimap<Integer, Position> map = ArrayListMultimap.create();
    for (int i = 0; i < cards.size(); ++i) {
      int[][] card = cards.get(i);
      for (int x = 0; x < 5; ++x) {
        for (int y = 0; y < 5; ++y) {
          map.put(card[x][y], new Position(i, x, y));
        }
      }
    }
    return ImmutableMultimap.copyOf(map);
  }

  private static boolean checkRow(boolean[][] cardMask, int x) {
    for (int i = 0; i < 5; ++i) {
      if (!cardMask[x][i]) {
        return false;
      }
    }
    return true;
  }

  private static boolean checkCol(boolean[][] cardMask, int y) {
    for (int i = 0; i < 5; ++i) {
      if (!cardMask[i][y]) {
        return false;
      }
    }
    return true;
  }

  private static int calculateScore(Integer number, int[][] card, boolean[][] mask) {
    int sum = 0;
    for (int x = 0; x < 5; ++x) {
      for (int y = 0; y < 5; ++y) {
        sum += mask[x][y] ? 0 : card[x][y];
      }
    }
    return sum * number;
  }

  private static int part1(ImmutableList<Integer> numbers, ImmutableList<int[][]> cards) {
    Multimap<Integer, Position> map = buildPositions(cards);

    boolean[][][] masks = new boolean[cards.size()][5][5];
    for (int i = 0; i < numbers.size(); ++i) {
      Integer number = numbers.get(i);
      for (Position pos : map.get(number)) {
        int[][] card = cards.get(pos.boardIdx);
        boolean[][] mask = masks[pos.boardIdx];

        mask[pos.x][pos.y] = true;
        if (checkRow(mask, pos.x) || checkCol(mask, pos.y)) {
          return calculateScore(number, card, mask);
        }
      }
    }
    return 0;
  }

  private static int part2(ImmutableList<Integer> numbers, ImmutableList<int[][]> cards) {
    Multimap<Integer, Position> map = buildPositions(cards);

    Set<Integer> won = new HashSet<>();
    int score = 0;

    boolean[][][] masks = new boolean[cards.size()][5][5];
    for (int i = 0; i < numbers.size(); ++i) {
      Integer number = numbers.get(i);
      for (Position pos : map.get(number)) {
        if (won.contains(pos.boardIdx)) {
          continue;
        }

        int[][] card = cards.get(pos.boardIdx);
        boolean[][] mask = masks[pos.boardIdx];

        mask[pos.x][pos.y] = true;
        if (checkRow(mask, pos.x) || checkCol(mask, pos.y)) {
          won.add(pos.boardIdx);
          score = calculateScore(number, card, mask);
        }
      }
    }
    return score;
  }

  public static void main(String[] args) {
    Input input = readInput(args[0]);
    System.out.println(part1(input.numbers, input.cards));
    System.out.println(part2(input.numbers, input.cards));
  }
}
