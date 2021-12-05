package nemben.aoc;

import com.google.auto.value.AutoValue;

import java.util.Iterator;

@AutoValue
public abstract class Line {
  public abstract Point start();

  public abstract Point end();

  public boolean isVertical() {
    return start().x() == end().x();
  }

  public boolean isHorizontal() {
    return start().y() == end().y();
  }

  public boolean isDiagonal() {
    return Math.abs(start().x() - end().x()) == Math.abs(start().y() - end().y());
  }

  public Iterator<Point> points() {
    if (isVertical()) {
      return new Iterator<Point>() {
        private int x = start().x();
        private int y = Math.min(start().y(), end().y());
        private int maxY = Math.max(start().y(), end().y());

        @Override
        public boolean hasNext() {
          return y <= maxY;
        }

        @Override
        public Point next() {
          Point p = Point.of(x, y);
          y += 1;
          return p;
        }
      };
    } else if (isHorizontal()) {
      return new Iterator<Point>() {
        private int x = Math.min(start().x(), end().x());
        private int y = start().y();
        private int maxX = Math.max(start().x(), end().x());

        @Override
        public boolean hasNext() {
          return x <= maxX;
        }

        @Override
        public Point next() {
          Point p = Point.of(x, y);
          x += 1;
          return p;
        }
      };
    } else if (isDiagonal()) {
      return new Iterator<Point>() {
        private int x = start().x() < end().x() ? start().x() : end().x();
        private int y = start().x() < end().x() ? start().y() : end().y();
        private int maxX = Math.max(start().x(), end().x());
        private int slopeY = (start().x() - end().x()) / (start().y() - end().y());

        @Override
        public boolean hasNext() {
          return x <= maxX;
        }

        @Override
        public Point next() {
          Point p = Point.of(x, y);
          x += 1;
          y += slopeY;
          return p;
        }
      };
    }
    throw new AssertionError();
  }

  public static Line of(Point p1, Point p2) {
    return new AutoValue_Line(p1, p2);
  }
}
