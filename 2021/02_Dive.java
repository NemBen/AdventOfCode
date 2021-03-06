package nemben.aoc.d02;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import nemben.aoc.InputReader;

public class Dive {
  private enum Direction {
    FORWARD,
    DOWN,
    UP
  }

  private static ImmutableMap<String, Direction> DIRECTIONS =
      ImmutableMap.<String, Direction>builder()
          .put("forward", Direction.FORWARD)
          .put("down", Direction.DOWN)
          .put("up", Direction.UP)
          .build();

  private static class Instruction {
    final Direction direction;
    final long units;

    Instruction(Direction direction, long units) {
      this.direction = direction;
      this.units = units;
    }
  }

  private static ImmutableList<Instruction> readInstructions(String name) {
    return InputReader.readSimpleInput(
        name,
        scn -> {
          Direction direction = DIRECTIONS.get(scn.next(DIRECTION_PATTERN));
          long units = scn.nextLong();
          return new Instruction(direction, units);
        });
  }

  private static final String DIRECTION_PATTERN = "[a-z]+";

  private static long playSimpleInstructions(ImmutableList<Instruction> instructions) {
    long x = 0, y = 0;
    for (Instruction instruction : instructions) {
      switch (instruction.direction) {
        case FORWARD:
          x += instruction.units;
          break;
        case DOWN:
          y += instruction.units;
          break;
        case UP:
          y -= instruction.units;
          break;
        default:
          throw new AssertionError();
      }
    }
    return x * y;
  }

  private static long playAdvancedInstructions(ImmutableList<Instruction> instructions) {
    long x = 0, y = 0, aim = 0;
    for (Instruction instruction : instructions) {
      switch (instruction.direction) {
        case FORWARD:
          x += instruction.units;
          y += aim * instruction.units;
          break;
        case DOWN:
          aim += instruction.units;
          break;
        case UP:
          aim -= instruction.units;
          break;
        default:
          throw new AssertionError();
      }
    }
    return x * y;
  }

  public static void main(String[] args) {
    ImmutableList<Instruction> instructions = readInstructions(args[0]);
    System.out.println(playSimpleInstructions(instructions));
    System.out.println(playAdvancedInstructions(instructions));
    return;
  }
}
