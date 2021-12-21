package nemben.aoc.d21;

import nemben.aoc.InputReader;

public class DiracDice {
  private static InputReader.InputPair<Integer, Integer> readInput(String name) {
    return InputReader.readInputPair(
        name,
        scn -> Integer.parseInt(scn.nextLine().substring("Player 1 starting position: ".length())),
        scn -> Integer.parseInt(scn.nextLine().substring("Player 2 starting position: ".length())));
  }

  static class Dice {
    int value = 0;
    int rolls = 0;

    int roll() {
      rolls += 1;
      value += 1;
      if (value > 100) {
        value -= 100;
      }
      return value;
    }

    int rolls() {
      return rolls;
    }
  }

  private static int nextPosition(int current, int roll) {
    int next = current + roll;
    while (next > 10) {
      next -= 10;
    }
    return next;
  }

  private static int part1(int first, int second) {
    int player1 = first, player2 = second;
    int sum1 = 0, sum2 = 0;

    Dice dice = new Dice();
    while (sum1 < 1000 && sum2 < 1000) {
      for (int i = 0; i < 3; ++i) {
        player1 += dice.roll();
      }
      while (player1 > 10) {
        player1 -= 10;
      }
      sum1 += player1;
      if (sum1 >= 1000) {
        break;
      }
      for (int i = 0; i < 3; ++i) {
        player2 += dice.roll();
      }
      while (player2 > 10) {
        player2 -= 10;
      }
      sum2 += player2;
    }
    return Math.min(sum1, sum2) * dice.rolls();
  }

  private static long part2(Integer first, Integer second) {
    long[] dice = new long[3 * 3 + 1];
    for (int i = 1; i <= 3; ++i) {
      for (int j = 1; j <= 3; ++j) {
        for (int k = 1; k <= 3; ++k) {
          dice[i + j + k] += 1;
        }
      }
    }

    long p1Wins = 0;
    long p2Wins = 0;

    long[][][][] currentStates = new long[11][11][31][31];
    currentStates[first][second][0][0] = 1;

    for (int turn = 0; turn <= 8; ++turn) {
      long[][][][] nextStates = new long[11][11][31][31];

      for (int p1 = 1; p1 <= 10; ++p1) {
        for (int p2 = 1; p2 <= 10; ++p2) {
          long[][] currentPos = currentStates[p1][p2];

          for (int roll1 = 3; roll1 <= 9; ++roll1) {
            int p1Next = nextPosition(p1, roll1);
            long[][] p1Pos = nextStates[p1Next][p2];

            for (int score1 = 21 - p1Next; score1 < 21; ++score1) {
              for (int score2 = turn; score2 < 21; ++score2) {
                p1Pos[score1 + p1Next][score2] += currentPos[score1][score2] * dice[roll1];
              }
            }

            for (int roll2 = 3; roll2 <= 9; ++roll2) {
              int p2Next = nextPosition(p2, roll2);
              long[][] p2Pos = nextStates[p1Next][p2Next];

              for (int score1 = turn; score1 < 21 - p1Next; ++score1) {
                for (int score2 = turn; score2 < 21; ++score2) {
                  p2Pos[score1 + p1Next][score2 + p2Next] +=
                      currentPos[score1][score2] * dice[roll1] * dice[roll2];
                }
              }
            }
          }
        }
      }

      for (int p1 = 1; p1 <= 10; ++p1) {
        for (int p2 = 1; p2 <= 10; ++p2) {
          long[][] pos = nextStates[p1][p2];
          for (int score1 = 21; score1 <= 30; ++score1) {
            for (int score2 = 0; score2 <= 30; ++score2) {
              p1Wins += pos[score1][score2];
            }
          }
          for (int score1 = 0; score1 < 21; ++score1) {
            for (int score2 = 21; score2 <= 30; ++score2) {
              p2Wins += pos[score1][score2];
            }
          }
        }
      }

      currentStates = nextStates;
    }
    return Math.max(p1Wins, p2Wins);
  }

  public static void main(String[] args) {
    InputReader.InputPair<Integer, Integer> input = readInput(args[0]);
    System.out.println(input);
    System.out.println(part1(input.first(), input.second()));
    System.out.println(part2(input.first(), input.second()));
  }
}
