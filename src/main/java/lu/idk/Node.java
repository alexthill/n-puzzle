package lu.idk;

import lu.idk.heuristics.IHeuristic;

import lu.idk.Board.Dir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<Dir> getPath() {
        List<Board.Dir> moves = new ArrayList<>();
        Node current = this;
        while (current.dir != null) {
            moves.add(current.dir);
            current = current.parent;
        }
        Collections.reverse(moves);
        return moves;
    }

    public List<Node> getNextNodes(IHeuristic heuristic) {
        List<Node> result = new ArrayList<>();
        for (Dir dir : Dir.values()) {
            if (this.board.isMoveValid(dir)) {
                Board newBoard = this.board.clone();
                newBoard.move(dir);
                Node newNode = new Node(newBoard, this, dir);
                newNode.update(this.g, heuristic);
                result.add(newNode);
            }
        }
        return result;
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
