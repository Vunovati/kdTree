import java.util.Set;
import java.util.TreeSet;

/**
 * Created by evlaada on 11/12/14.
 */
public class KdTree {

    private Node root;
    private int size;

    public KdTree() {
    }

    public void insert(Point2D p) {
        if (root == null) {
            createRootNode(p);
        } else {
            traverseTowardsPointPosition(p, root);
        }
    }

    private void createRootNode(Point2D p) {
        root = new Node();
        root.vertical = true;
        root.p = p;
        root.rect = new RectHV(0, 0, 1, 1);
        size++;
    }

    private void traverseTowardsPointPosition(Point2D p, Node node) {
        if (pointSmallerThanNode(p, node)) {
            if (node.lb != null) {
                traverseTowardsPointPosition(p, node.lb);
            } else {
                insertAsChildOfParent(createNewNode(p, node), node);
            }
        } else {
            if (node.rt != null) {
                traverseTowardsPointPosition(p, node.rt);
            } else {
                insertAsChildOfParent(createNewNode(p, node), node);
            }
        }
    }

    private boolean pointSmallerThanNode(Point2D p, Node node) {
        boolean b;
        if (node.vertical) b = p.x() < node.p.x();
        else b = p.y() < node.p.y();
        return b;
    }

    private Node createNewNode(Point2D p, Node node) {
        Node newNode = new Node();
        newNode.p = p;
        newNode.vertical = !node.vertical;
        newNode.rect = calculateNewRect(p, node);
        return newNode;
    }

    private RectHV calculateNewRect(Point2D p, Node node) {
        RectHV rect;
        if (node.vertical) {
            if (p.x() < node.p.x()) {
                rect = getLeftPartOfRect(node.p, node.rect);
            } else {
                rect = getRightPartOfRect(node.p, node.rect);
            }
        } else {
            if (p.y() < node.p.y()) {
                rect = getLowerPartOfRect(node.p, node.rect);
            } else {
                rect = getUpperPartOfRect(node.p, node.rect);
            }
        }
        return rect;
    }

    private void insertAsChildOfParent(Node newNode, Node parent) {
        Point2D p = newNode.p;
        if (p.x() != parent.p.x() || p.y() != parent.p.y()) {
            if (parent.vertical) {
                if (p.x() < parent.p.x()) {
                    parent.lb = newNode;
                } else {
                    parent.rt = newNode;
                }
            } else {
                if (p.y() < parent.p.y()) {
                    parent.lb = newNode;
                } else {
                    parent.rt = newNode;
                }
            }
            size++;
        }
    }

    private RectHV getLeftPartOfRect(Point2D p, RectHV rect) {
        return new RectHV(rect.xmin(), rect.ymin(), p.x(), rect.ymax());
    }

    private RectHV getRightPartOfRect(Point2D p, RectHV rect) {
        return new RectHV(p.x(), rect.ymin(), rect.xmax(), rect.ymax());
    }

    private RectHV getUpperPartOfRect(Point2D p, RectHV rect) {
        return new RectHV(rect.xmin(), p.y(), rect.xmax(), rect.ymax());
    }

    private RectHV getLowerPartOfRect(Point2D p, RectHV rect) {
        return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), p.y());
    }

    public void draw() {
        drawNode(root);
    }

    private void drawNode(Node node) {
        if (node != null) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.01);
            node.p.draw();

            if (node.vertical) {
                StdDraw.setPenRadius(.003);
                StdDraw.setPenColor(StdDraw.RED);

                StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            } else {
                StdDraw.setPenRadius(.003);
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
            }


            drawNode(node.rt);
            drawNode(node.lb);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        return nodeContainsPoint(root, p);
    }

    private boolean nodeContainsPoint(Node node, Point2D p) {
        if (p.x() == node.p.x() && p.y() == node.p.y()) {
            return true;
        } else {
            if (pointSmallerThanNode(p, node)) {
                if (node.lb != null) {
                    return nodeContainsPoint(node.lb, p);
                } else {
                    return false;
                }
            } else {
                if (node.rt != null) {
                    return nodeContainsPoint(node.rt, p);
                } else {
                    return false;
                }
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        Set<Point2D> pointsInRange = new TreeSet<Point2D>();
        searchAndAddPointsInRange(pointsInRange, rect, root);

        return pointsInRange;
    }

    private void searchAndAddPointsInRange(Set<Point2D> points, RectHV rect, Node node) {
        if (node != null) {
            if (nodeWithinRect(node, rect)) {
                points.add(node.p);
            }

            if (node.vertical) {
                if (rect.xmax() > node.p.x()) {
                    searchAndAddPointsInRange(points, rect, node.rt);
                }

                if (rect.xmin() < node.p.x()) {
                    searchAndAddPointsInRange(points, rect, node.lb);
                }
            } else {
                if (rect.ymax() > node.p.y()) {
                    searchAndAddPointsInRange(points, rect, node.rt);
                }

                if (rect.ymin() < node.p.y()) {
                    searchAndAddPointsInRange(points, rect, node.lb);
                }
            }
        }
    }

    private boolean nodeWithinRect(Node node, RectHV rect) {
        return node.p.x() < rect.xmax() && node.p.x() > rect.xmin() && node.p.y() < rect.ymax() && node.p.y() > rect.ymin();
    }

    public Point2D nearest(Point2D query) {
        return findNearest(root, query, root.p);
    }


    private Point2D findNearest(Node node, Point2D target, Point2D nearestSoFar) {
        if (node == null || !couldContainCloserPoint(node, target, nearestSoFar)) return nearestSoFar;

        if (node.p.distanceSquaredTo(target) < nearestSoFar.distanceSquaredTo(target)) {
            nearestSoFar = node.p;
        }

        if (isLeaf(node)) {
                return nearestSoFar;
        }

        if (node.lb != null && node.lb.rect.contains(target)) {
            Point2D nearestLeft  = findNearest(node.lb, target, nearestSoFar);
            return findNearest(node.rt, target, nearestLeft );
        } else {
            Point2D nearestRight = findNearest(node.rt, target, nearestSoFar);
            return findNearest(node.lb, target, nearestRight);
        }

    }

    private boolean isLeaf(Node node) {
        return node.lb == null && node.rt == null;
    }

    private boolean couldContainCloserPoint(Node node, Point2D p, Point2D nearestSoFar) {
        return node != null && node.rect.distanceSquaredTo(p) < p.distanceSquaredTo(nearestSoFar);
    }

    public String size() {
        return String.valueOf(size);
    }

    private String pointBetweenBoundaries(RectHV rectHV, Point2D p) {
        return null;
    }

    private static class Node {
        boolean vertical;
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree

        private Node rt;        // the right/top subtree

        @Override
        public String toString() {
            return "Node{" +
                    "vertical=" + vertical +
                    ", p=" + p +
                    ", rect=" + rect +
                    ", lb=" + lb +
                    ", rt=" + rt +
                    '}';
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();

        Point2D p1 = new Point2D(.7, .2);
        Point2D p2 = new Point2D(.5, .4);
        Point2D p3 = new Point2D(.2, .3);
        Point2D p4 = new Point2D(.4, .7);
        Point2D p5 = new Point2D(.9, .6);

        System.out.println(kdTree + " size is: " + kdTree.size());
        kdTree.insert(p1);
        System.out.println(kdTree + " size is: " + kdTree.size());
        kdTree.insert(p2);
        System.out.println(kdTree + " size is: " + kdTree.size());
        kdTree.insert(p3);
        System.out.println(kdTree + " size is: " + kdTree.size());
        kdTree.insert(p4);
        System.out.println(kdTree + " size is: " + kdTree.size());
        kdTree.insert(p5);
        System.out.println(kdTree + " size is: " + kdTree.size());
//
//        System.out.println(kdTree + " contains " + p3 + " :" + kdTree.contains(p3));
        Point2D missingPoint = new Point2D(.1, .1);
//        System.out.println(kdTree + " contains " + missingPoint + " :" + kdTree.contains(missingPoint));
//
        Point2D missingPoint2 = new Point2D(.99, .9999999999);
//        System.out.println(kdTree + " contains " + missingPoint2 + " :" + kdTree.contains(missingPoint2));
//
        System.out.println(kdTree + " nearest " + missingPoint + " :" + kdTree.nearest(missingPoint));
        System.out.println(kdTree + " nearest " + missingPoint2 + " :" + kdTree.nearest(missingPoint2));
//

//        kdTree.draw();


//        String filename = args[0];
//        In in = new In(filename);
//
//
//        // initialize the two data structures with point from standard input
//        KdTree kdtree2 = new KdTree();
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree2.insert(p);
//        }
//
//        kdtree2.draw();
    }
}
