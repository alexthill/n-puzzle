package lu.idk;

import lu.idk.heuristics.IHeuristic;

import lu.idk.Board.Dir;

public class Node {
    public Board board;
    public int g;
    public int h;
    public Integer f;
    public Node parent;
    public Dir dir;

    public Node(Board board, Node parent, Dir dir) {
        this.board = board;
        this.parent = parent;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.dir = dir;
    }

    public Node(Node node) {
        this.board = node.board.clone();
        this.parent = node.parent;
        this.g = node.g;
        this.h = node.h;
        this.f = node.f;
        this.dir = node.dir;
    }

    public void update(int g, IHeuristic heuristic) {
        this.g += g + 1;
        this.h = heuristic.h(this.board);
        this.f = this.g + this.h;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Node.class) {
            if (((Node) o).board.boardEquals(this.board))
                return true;
        }
        return o == this;
    }
}
