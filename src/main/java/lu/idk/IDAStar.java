package lu.idk;

import lu.idk.heuristics.IHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class IDAStar {

    Stack<Node> path = new Stack<>();
    IHeuristic heuristic;
    Board finalBoard;

    public IDAStar(IHeuristic heuristic, Board finalBoard) {
        this.heuristic = heuristic;
        this.finalBoard = finalBoard;
    }

    void idaStar(Board startBoard) {
        int bound = this.heuristic.h(startBoard);
        Node startNode = new Node(startBoard, null, null);
        path.add(startNode);

        while (true) {
            System.out.println("looping");
            int t = search(0, bound);
            if (t == Integer.MAX_VALUE) {
                System.out.println("Found");
                return;
            }
            if (t == Integer.MAX_VALUE - 1) {
                System.out.println("No solution");
                return;
            }
            bound = t;
        }
    }

    int search(int g, int bound) {
        Node node = path.lastElement();
        int f = g + heuristic.h(node.board);
        if (f > bound) {
            return f;
        }
        if (node.board.boardEquals(finalBoard)) {
//            for (Board.Dir dir : getPath(node)) {
//                System.out.println(dir);
//            }
            return Integer.MAX_VALUE;
        }
        int min = Integer.MAX_VALUE - 1;
        for (Node child : getNextNodes(node)) {
            if (!path.contains(child)) {
                path.push(child);
                int t = search(g + 1, bound);
                if (t == Integer.MAX_VALUE) {
                    return t;
                }
                if (t < min) {
                    min = t;
                }
                path.pop();
            }
        }
        return min;
    }

    public List<Node> getNextNodes(Node node) {
        List<Node> result = new ArrayList<>();
        for (Board.Dir dir : Board.Dir.values()) {
            if (node.board.isMoveValid(dir)) {
                Board newBoard = node.board.clone();
                newBoard.move(dir);
                Node newNode = new Node(newBoard, node, dir);
                newNode.update(node.g, heuristic);
                result.add(newNode);
            }
        }
        return result;
    }

    public List<Board.Dir> getPath(Node node) {
        List<Board.Dir> moves = new ArrayList<>();
        Node current = node;
        while (current.dir != null) {
            moves.add(current.dir);
            current = current.parent;
        }
        Collections.reverse(moves);
        return moves;
    }
}
