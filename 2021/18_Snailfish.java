package nemben.aoc.d18;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;

import java.util.Scanner;

public class Snailfish {
  abstract static class Node {
    Pair parent;

    Node(Pair parent) {
      this.parent = parent;
    }

    abstract long magnitude();

    static Node parse(String str) {
      try (Scanner scn =
          new Scanner(
              str.replaceAll("\\[", " [ ")
                  .replaceAll("\\]", " ] ")
                  .replaceAll(",", " , ")
                  .replaceAll("\\s\\s", " ")
                  .trim())) {
        return parse(scn, null);
      }
    }

    private static Node parse(Scanner scn, Pair parent) {
      if (scn.hasNextInt()) {
        return new Value(parent, scn.nextInt());
      }

      Pair p = new Pair(parent);
      scn.next();
      p.left = parse(scn, p);
      scn.next();
      p.right = parse(scn, p);
      scn.next();
      return p;
    }
  }

  static class Pair extends Node {
    Node left;
    Node right;

    Pair(Pair parent) {
      super(parent);
    }

    Pair(Pair parent, Node left, Node right) {
      super(parent);
      this.left = left;
      this.right = right;
    }

    @Override
    long magnitude() {
      return 3 * left.magnitude() + 2 * right.magnitude();
    }

    @Override
    public String toString() {
      return "[" + left + "," + right + ']';
    }
  }

  static class Value extends Node {
    int value;

    Value(Pair parent, int value) {
      super(parent);
      this.value = value;
    }

    @Override
    long magnitude() {
      return value;
    }

    @Override
    public String toString() {
      return Integer.toString(value);
    }
  }

  private static ImmutableList<Node> readInput(String name) {
    return InputReader.readSimpleInput(name, scn -> Node.parse(scn.nextLine()));
  }

  private static boolean explode(Node root) {
    Pair p = findExplode(root);
    if (p == null) {
      return false;
    }

    Value left = left(p);
    if (left != null) {
      left.value += ((Value) p.left).value;
    }
    Value right = right(p);
    if (right != null) {
      right.value += ((Value) p.right).value;
    }

    if (p.parent.left == p) {
      p.parent.left = new Value(p.parent, 0);
    } else {
      p.parent.right = new Value(p.parent, 0);
    }

    return true;
  }

  private static Pair findExplode(Node root) {
    return findExplode(root, 0);
  }

  private static Pair findExplode(Node n, int level) {
    if (n instanceof Value) {
      return null;
    }

    Pair p = (Pair) n;
    if (level >= 4 /* && p.left instanceof Value && p.right instanceof Value */) {
      return p;
    }

    Pair left = findExplode(p.left, level + 1);
    if (left != null) {
      return left;
    }
    return findExplode(p.right, level + 1);
  }

  private static Value left(Pair p) {
    while (p.parent != null && p.parent.left == p) {
      p = p.parent;
    }
    if (p.parent == null) {
      return null;
    }
    Node left = p.parent.left;
    while (left instanceof Pair) {
      left = ((Pair) left).right;
    }
    return (Value) left;
  }

  private static Value right(Pair p) {
    while (p.parent != null && p.parent.right == p) {
      p = p.parent;
    }
    if (p.parent == null) {
      return null;
    }
    Node right = p.parent.right;
    while (right instanceof Pair) {
      right = ((Pair) right).left;
    }
    return (Value) right;
  }

  private static boolean split(Node root) {
    Value v = findSplit(root);
    if (v == null) {
      return false;
    }

    int left = v.value / 2;
    int right = v.value - left;

    Pair p = new Pair(v.parent);
    p.left = new Value(p, left);
    p.right = new Value(p, right);

    if (v.parent.left == v) {
      v.parent.left = p;
    } else {
      v.parent.right = p;
    }
    return true;
  }

  private static Value findSplit(Node n) {
    if (n instanceof Value) {
      Value v = (Value) n;
      return v.value >= 10 ? v : null;
    }

    Pair p = (Pair) n;
    Value left = findSplit(p.left);
    if (left != null) {
      return left;
    }
    return findSplit(p.right);
  }

  private static void process(Node root) {
    while (true) {
      if (explode(root)) {
        continue;
      }
      if (!split(root)) {
        break;
      }
    }
  }

  private static Node copy(Node root) {
    return copy(root, null);
  }

  private static Node copy(Node n, Pair parent) {
    if (n instanceof Value) {
      Value v = (Value) n;
      return new Value(parent, v.value);
    }

    Pair p = (Pair) n;
    Pair np = new Pair(parent);
    np.left = copy(p.left, np);
    np.right = copy(p.right, np);
    return np;
  }

  private static Node add(Node a, Node b) {
    Pair sum = new Pair(null);
    sum.left = copy(a);
    sum.left.parent = sum;
    sum.right = copy(b);
    sum.right.parent = sum;
    process(sum);

    return sum;
  }

  private static long part1(ImmutableList<Node> values) {
    Node sum = values.get(0);
    for (int i = 1; i < values.size(); ++i) {
      sum = add(sum, values.get(i));
    }
    return sum.magnitude();
  }

  private static long part2(ImmutableList<Node> values) {
    long max = 0;
    for (int i = 0; i < values.size() - 1; ++i) {
      for (int j = i + 1; j < values.size(); ++j) {
        long mF = add(values.get(i), values.get(j)).magnitude();
        if (max < mF) {
          max = mF;
        }

        long mR = add(values.get(j), values.get(i)).magnitude();
        if (max < mR) {
          max = mR;
        }
      }
    }
    return max;
  }

  public static void main(String[] args) {
    ImmutableList<Node> values = readInput(args[0]);
    System.out.println(part1(values));
    System.out.println(part2(values));
  }
}
