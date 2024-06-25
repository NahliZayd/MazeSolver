package zn.mazesolver.world;

public class Node {
    int row, col, g, h;
    Node parent;

    Node(int row, int col, Node parent, int g, int h) {
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.g = g;
        this.h = h;
    }

    int f() {
        return g + h;
    }
}
