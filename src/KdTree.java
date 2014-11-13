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
        System.out.println(root);
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

    public Point2D[] range(RectHV rect) {
        return new Point2D[0];
    }

    public Point2D nearest(Point2D query) {
        return findNearest(root, query);
    }

    private Point2D findNearest(Node node, Point2D p) {
        if (pointSmallerThanNode(p, node)) {
            if (node.lb != null) {
                return findNearest(node.lb, p);
            } else {
                return node.p;
            }
        } else {
            if (node.rt != null) {
                return findNearest(node.rt, p);
            } else {
                return node.p;
            }
        }
    }

    private String size() {
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

        System.out.println(kdTree + " contains " + p3 + " :" + kdTree.contains(p3));
        Point2D missingPoint = new Point2D(.1, .1);
        System.out.println(kdTree + " contains " + missingPoint + " :" + kdTree.contains(missingPoint));

        Point2D missingPoint2 = new Point2D(.99, .9999999999);
        System.out.println(kdTree + " contains " + missingPoint2 + " :" + kdTree.contains(missingPoint2));

        System.out.println(kdTree + " nearest " + missingPoint + " :" + kdTree.nearest(missingPoint));
        System.out.println(kdTree + " nearest " + missingPoint2 + " :" + kdTree.nearest(missingPoint2));


//        Point2D p = new Point2D(.3, .3);
//        Point2D p2 = new Point2D(.6, .6);
//        kdTree.insert(p);
//        kdTree.insert(p2);
//
//        RectHV rectHV = new RectHV(.1, .1, .4, .4);
//
//        System.out.println("point " + p + " between: " + kdTree.pointBetweenBoundaries(rectHV, p));
//        System.out.println("point " + p2 + " between: " + kdTree.pointBetweenBoundaries(rectHV, p2));
//
//        Point2D p3 = new Point2D(.5, .5);
//        System.out.println("point " + p3 + " nearest: " + kdTree.nearest(p3));
//        Point2D p4 = new Point2D(.1, .4);
//        System.out.println("point " + p4 + " nearest: " + kdTree.nearest(p4));
//
//
//        kdTree.insert(p4);
//        System.out.println("point in rectangle: " + kdTree.range(rectHV));
//        System.out.println("points in set: " + kdTree.size());
//
//        System.out.println("point in rectangle: " + (new KdTree()).range(rectHV));
//
        kdTree.draw();
    }
}
