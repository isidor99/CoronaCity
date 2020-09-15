package org.etf.coronacity.helper;

import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Home;

import java.io.Serializable;
import java.util.LinkedList;

/*
    This is helper class
    This class represents shortest path (and contains method for determination that shortest path)
    from one position to another in matrix
 */
public class ShortestPath implements Serializable {

    // inner class that represents path element
    public static class Node implements Serializable {

        int x;
        int y;
        int distance;
        Node prev;

        Node(int x, int y, int distance, Node prev) {
            this.x = x;
            this.y = y;
            this.distance = distance;
            this.prev = prev;
        }

        public int getX() { return x; }

        public int getY() { return y; }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Node) {
                Node other = (Node) obj;
                return other.x == x && other.y == y && other.distance == distance;
            }

            return false;
        }
    }

    /**
     * Find the shortest path from one position to another
     * @param matrix two dimensional array that represents matrix
     * @param start start position
     * @param end end position
     * @return LinkedLint<Node> path
     */
    public static LinkedList<Node> shortestPath(Object[][] matrix, LocationData start, LocationData end) {

        // both persons and ambulances can move across the fields where
        // the checkpoints are located, but not the houses

        Node[][] nodes = new Node[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix.length; j++)
                if ((i == start.getPositionX() && j == start.getPositionY()) ||
                        (i == end.getPositionX() && j == end.getPositionY()) ||
                        !(matrix[i][j] instanceof Home))
                    nodes[i][j] = new Node(i, j, Integer.MAX_VALUE, null);

        LinkedList<Node> queue = new LinkedList<>();
        Node source = nodes[start.getPositionX()][start.getPositionY()];
        source.distance = 0;
        queue.add(source);
        Node destination = null;
        Node p;
        while ((p = queue.poll()) != null) {

            if (p.x == end.getPositionX() && p.y == end.getPositionY()) {
                destination = p;
                break;
            }

            // move in all 8 directions
            // right
            visit(nodes, queue, p.x, p.y + 1, p);

            // right bottom
            visit(nodes, queue, p.x + 1, p.y + 1, p);

            // bottom
            visit(nodes, queue, p.x + 1, p.y, p);

            // left bottom
            visit(nodes, queue, p.x + 1, p.y - 1, p);

            // left
            visit(nodes, queue, p.x, p.y - 1, p);

            // left top
            visit(nodes, queue, p.x - 1, p.y - 1, p);

            // top
            visit(nodes, queue, p.x - 1, p.y, p);

            // right top
            visit(nodes, queue, p.x - 1, p.y + 1, p);
        }

        if (destination == null)
            return null;

        LinkedList<Node> path = new LinkedList<>();
        p = destination;
        do {
            path.addFirst(p);
        } while ((p = p.prev) != null);

        return path;
    }

    //

    /**
     * Visit next node if possible
     * @param nodes all nodes
     * @param queue nodes that needs to be visited
     * @param x position x
     * @param y position y
     * @param parent parent node
     */
    private static void visit(Node[][] nodes, LinkedList<Node> queue, int x, int y, Node parent) {

        if (x < 0 || x > nodes.length - 1 || y < 0 || y > nodes.length - 1 || nodes[x][y] == null)
            return;

        int distance = parent.distance + 1;
        Node p = nodes[x][y];
        if (distance < p.distance) {
            p.distance = distance;
            p.prev = parent;
            queue.add(p);
        }
    }
}
