import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {
    Set<Point2D> points;

    public PointSET() {
        points = new TreeSet<Point2D>();
    }                               // construct an empty set of points

    public boolean isEmpty() {
        return points.isEmpty();
    }                      // is the set empty?

    public int size() {
        return points.size();
    }                        // number of points in the set

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        for (Point2D point: points) {
            point.draw();
        }
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Set<Point2D> pointsInRectangle = new TreeSet<Point2D>();

        for (Point2D point: points) {
            if (pointBetweenBoundaries(rect, point)) {
                pointsInRectangle.add(point);
            }
        }
        return pointsInRectangle;
    }

    private boolean pointBetweenBoundaries(RectHV rect, Point2D point) {
        return numberBetweenBoundaries(point.x(), rect.xmax(), rect.xmin()) && numberBetweenBoundaries(point.y(), rect.ymax(), rect.ymin());
    }

    private boolean numberBetweenBoundaries(double point, double upper, double lower) {
        return point <= upper && point >= lower;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        Point2D nearestNeighbor = points.iterator().next();
        Comparator<Point2D> comparator = p.DISTANCE_TO_ORDER;

        for (Point2D point: points) {
            if (comparator.compare(point, nearestNeighbor) < 0) {
                nearestNeighbor = point;
            }
        }

        return nearestNeighbor;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        PointSET pointSET = new PointSET();
        Point2D p = new Point2D(3, 3);
        Point2D p2 = new Point2D(6, 6);
        pointSET.insert(p);
        pointSET.insert(p2);

        RectHV rectHV = new RectHV(1,1, 4, 4);

        System.out.println("point " + p + " between: " + pointSET.pointBetweenBoundaries(rectHV, p));
        System.out.println("point " + p2 + " between: " + pointSET.pointBetweenBoundaries(rectHV, p2));

        Point2D p3 = new Point2D(5, 5);
        System.out.println("point " + p3 + " nearest: " + pointSET.nearest(p3));
        Point2D p4 = new Point2D(1, 4);
        System.out.println("point " + p4 + " nearest: " + pointSET.nearest(p4));


        pointSET.insert(p4);
        System.out.println("point in rectangle: " + pointSET.range(rectHV));
        System.out.println("points in set: " + pointSET.size());

        System.out.println("point in rectangle: " + (new PointSET()).range(rectHV));

        pointSET.draw();
    }
}