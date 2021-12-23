package nemben.aoc.d23;

import com.google.auto.value.AutoValue;
import com.google.common.collect.*;
import nemben.aoc.Point;

import java.util.HashMap;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

public class Amphipod {
  @AutoValue
  abstract static class Paths {
    abstract ImmutableTable<Point, Point, ImmutableList<Point>> paths();

    ImmutableList<Point> getPath(Point from, Point to) {
      return paths().get(from, to);
    }

    private static Paths of(ImmutableTable<Point, Point, ImmutableList<Point>> paths) {
      return new AutoValue_Amphipod_Paths(paths);
    }

    static Paths generate(ImmutableList<Point> rooms, ImmutableList<Point> hallway) {
      Table<Point, Point, ImmutableList<Point>> paths =
          HashBasedTable.create(rooms.size() + hallway.size(), rooms.size() + hallway.size());
      for (Point rP : rooms) {
        for (Point rP2 : rooms) {
          if (rP.y() == rP2.y()) {
            continue;
          }
          paths.put(rP, rP2, findPath(rP, rP2));
          paths.put(rP2, rP, findPath(rP2, rP));
        }
        for (Point hP : hallway) {
          paths.put(rP, hP, findPath(rP, hP));
          paths.put(hP, rP, findPath(hP, rP));
        }
      }
      return of(ImmutableTable.copyOf(paths));
    }

    private static ImmutableList<Point> findPath(Point from, Point to) {
      ImmutableList.Builder<Point> path = ImmutableList.builder();
      if (from.x() == 0 && to.x() != 0) { // Hallway to Room
        int dir = from.y() <= to.y() ? 1 : -1;
        for (int y = from.y() + dir; dir * y <= dir * to.y(); y += dir) {
          path.add(Point.of(from.x(), y));
        }
        for (int x = 1; x <= to.x(); ++x) {
          path.add(Point.of(x, to.y()));
        }
      } else if (from.x() != 0 && to.x() == 0) { // Room to Hallway
        for (int x = from.x() - 1; x >= 1; --x) {
          path.add(Point.of(x, from.y()));
        }
        int dir = from.y() <= to.y() ? 1 : -1;
        for (int y = from.y(); dir * y <= dir * to.y(); y += dir) {
          path.add(Point.of(to.x(), y));
        }
      } else if (from.x() != 0 && to.x() != 0 && from.y() != to.y()) { // Room to Room
        for (int x = from.x() - 1; x >= 1; --x) {
          path.add(Point.of(x, from.y()));
        }
        int dir = from.y() <= to.y() ? 1 : -1;
        for (int y = from.y(); dir * y <= dir * to.y(); y += dir) {
          path.add(Point.of(0, y));
        }
        for (int x = 1; x <= to.x(); ++x) {
          path.add(Point.of(x, to.y()));
        }
      } else {
        throw new AssertionError();
      }
      return path.build();
    }
  }

  static class Burrow {
    private final Paths paths;
    private final int roomSize;
    private final ImmutableList<Point> hallway;
    private final HashMap<Point, Character> creatures;

    Burrow(ImmutableList<Point> hallway, ImmutableList<Creature> creatures) {
      this.paths =
          Paths.generate(
              creatures.stream().map(Creature::position).collect(toImmutableList()), hallway);
      this.roomSize = creatures.stream().map(Creature::position).mapToInt(Point::x).max().orElse(0);
      this.hallway = hallway;
      this.creatures =
          new HashMap<>(
              creatures.stream().collect(toImmutableMap(Creature::position, Creature::amphipod)));
    }

    ImmutableList<Point> getHallway() {
      return hallway;
    }

    ImmutableList<Creature> getCreatures() {
      return creatures.entrySet().stream()
          .map(entry -> Creature.of(entry.getKey(), entry.getValue()))
          .collect(toImmutableList());
    }

    Optional<Point> getRoomSpot(Creature c) {
      int room = ROOMS.get(c.amphipod());

      int x;
      for (x = 0; x < roomSize && creatures.get(Point.of(x + 1, room)) == null; ++x) {}
      if (x == 0) {
        return Optional.empty();
      }

      Point spot = Point.of(x, room);
      return verifyRoommates(spot, c.amphipod()) ? Optional.of(spot) : Optional.empty();
    }

    boolean hasFinalPosition(Creature c) {
      if (c.position().x() == 0 || c.position().y() != ROOMS.get(c.amphipod())) {
        return false;
      }

      return verifyRoommates(c.position(), c.amphipod());
    }

    private boolean verifyRoommates(Point position, char amphipod) {
      for (int r = position.x() + 1; r <= roomSize; ++r) {
        if (creatures.get(Point.of(r, position.y())) != amphipod) {
          return false;
        }
      }
      return true;
    }

    Optional<Integer> getDistance(Point from, Point to) {
      ImmutableList<Point> path = paths.getPath(from, to);
      for (Point p : path) {
        if (creatures.get(p) != null) {
          return Optional.empty();
        }
      }
      return Optional.of(path.size());
    }

    void move(Point from, Point to) {
      creatures.put(to, creatures.remove(from));
    }

    private static final ImmutableMap<Character, Integer> ROOMS =
        ImmutableMap.<Character, Integer>builder()
            .put('A', 2)
            .put('B', 4)
            .put('C', 6)
            .put('D', 8)
            .build();
  }

  @AutoValue
  abstract static class Creature {
    abstract Point position();

    abstract char amphipod();

    static Creature of(Point position, char amphipod) {
      return new AutoValue_Amphipod_Creature(position, amphipod);
    }
  }

  private static long solve(ImmutableList<Point> hallway, ImmutableList<Creature> creatures) {
    return move(new Burrow(hallway, creatures), 0);
  }

  private static long move(Burrow burrow, long baseCost) {
    long minCost = Long.MAX_VALUE;
    boolean allDone = true;
    for (Creature creature : burrow.getCreatures()) {
      if (burrow.hasFinalPosition(creature)) {
        continue;
      }
      allDone = false;

      Optional<Point> maybeRoomSpot = burrow.getRoomSpot(creature);
      if (maybeRoomSpot.isPresent()) {
        Optional<Long> maybeCost = tryMove(burrow, baseCost, creature, maybeRoomSpot.get());
        if (maybeCost.isPresent()) {
          long cost = maybeCost.get();
          if (cost < minCost) {
            minCost = cost;
          }

          continue;
        }
      }

      if (creature.position().x() == 0) {
        continue;
      }

      for (Point hallwayPosition : burrow.getHallway()) {
        Optional<Long> maybeCost = tryMove(burrow, baseCost, creature, hallwayPosition);
        if (!maybeCost.isPresent()) {
          continue;
        }

        long cost = maybeCost.get();
        if (cost < minCost) {
          minCost = cost;
        }
      }
    }

    return allDone ? baseCost : minCost;
  }

  private static Optional<Long> tryMove(
      Burrow burrow, long baseCost, Creature creature, Point destination) {
    Optional<Integer> dist = burrow.getDistance(creature.position(), destination);
    if (!dist.isPresent()) {
      return Optional.empty();
    }

    long moveCost = COST_MULTIPLIERS.get(creature.amphipod()) * dist.get();

    burrow.move(creature.position(), destination);
    long cost = move(burrow, baseCost + moveCost);
    burrow.move(destination, creature.position());

    return Optional.of(cost);
  }

  private static final ImmutableMap<Character, Integer> COST_MULTIPLIERS =
      ImmutableMap.<Character, Integer>builder()
          .put('A', 1)
          .put('B', 10)
          .put('C', 100)
          .put('D', 1000)
          .build();

  private static long part1(ImmutableList<Creature> creatures) {
    return solve(HALLWAY_POINTS, creatures);
  }

  private static long part2(ImmutableList<Creature> creatures) {
    return solve(
        HALLWAY_POINTS,
        Streams.concat(
                creatures.stream()
                    .map(
                        c ->
                            c.position().x() == 2
                                ? Creature.of(Point.of(4, c.position().y()), c.amphipod())
                                : c),
                MISSING_CREATURES.stream())
            .collect(toImmutableList()));
  }

  public static void main(String[] args) {
    ImmutableList<Creature> creatures = sampleInput();
    System.out.println(part1(creatures));
    System.out.println(part2(creatures));
  }

  private static ImmutableList<Creature> input() {
    return ImmutableList.of(
        Creature.of(Point.of(1, 2), 'A'),
        Creature.of(Point.of(2, 2), 'B'),
        Creature.of(Point.of(1, 4), 'D'),
        Creature.of(Point.of(2, 4), 'C'),
        Creature.of(Point.of(1, 6), 'B'),
        Creature.of(Point.of(2, 6), 'D'),
        Creature.of(Point.of(1, 8), 'C'),
        Creature.of(Point.of(2, 8), 'A'));
  }

  private static ImmutableList<Creature> sampleInput() {
    return ImmutableList.of(
        Creature.of(Point.of(1, 2), 'B'),
        Creature.of(Point.of(2, 2), 'A'),
        Creature.of(Point.of(1, 4), 'C'),
        Creature.of(Point.of(2, 4), 'D'),
        Creature.of(Point.of(1, 6), 'B'),
        Creature.of(Point.of(2, 6), 'C'),
        Creature.of(Point.of(1, 8), 'D'),
        Creature.of(Point.of(2, 8), 'A'));
  }

  public static final ImmutableList<Point> HALLWAY_POINTS =
      ImmutableList.of(
          Point.of(0, 0),
          Point.of(0, 1),
          Point.of(0, 3),
          Point.of(0, 5),
          Point.of(0, 7),
          Point.of(0, 9),
          Point.of(0, 10));

  public static final ImmutableList<Creature> MISSING_CREATURES =
      ImmutableList.of(
          Creature.of(Point.of(2, 2), 'D'),
          Creature.of(Point.of(3, 2), 'D'),
          Creature.of(Point.of(2, 4), 'C'),
          Creature.of(Point.of(3, 4), 'B'),
          Creature.of(Point.of(2, 6), 'B'),
          Creature.of(Point.of(3, 6), 'A'),
          Creature.of(Point.of(2, 8), 'A'),
          Creature.of(Point.of(3, 8), 'C'));
}
