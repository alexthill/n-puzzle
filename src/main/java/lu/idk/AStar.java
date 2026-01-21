package lu.idk;

import lu.idk.heuristics.IHeuristic;

import java.util.*;

import lu.idk.Board.Dir;

public class AStar {

    public Node startNode;
    public IHeuristic heuristic;

    public PriorityQueue<Node> opened = new PriorityQueue<>(200, Comparator.comparingInt(a -> a.f));
    public List<Node> closed = new ArrayList<>();

    private int timeComplexity = 0;
    private int sizeComplexity = 0;

    public void init(IHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    public List<Dir> search(Board startBoard, Board finalBoard) {
        startNode = new Node(startBoard, null, null);
        startNode.update(-1, heuristic);
        opened.add(startNode);
        timeComplexity++;
        while (!opened.isEmpty()) {
            int tmp = opened.size() + closed.size();
            if (tmp > sizeComplexity)
                sizeComplexity = tmp;
//            opened.sort(Comparator.comparing(item -> item.f));
            Node process = opened.poll();
            if (process.board.boardEquals(finalBoard)) {
                System.out.println("Found");
                List<Dir> moves = getPath(process);
//                Board test = startBoard.clone();
                for (Dir dir : moves) {
//                    test.move(dir);
                    System.out.println(dir);
                }
                System.out.printf("Time complexity: %d\n", timeComplexity);
                System.out.printf("Size complexity: %d\n", sizeComplexity);
//                System.out.println(test.prettyString());
                return moves;
            }
//            opened.remove(process);
            closed.add(process);
            for (Node node : getNextNodes(process)) {
                if (closed.contains(node))
                    continue;
                if (!opened.contains(node)) {
                    opened.add(node);
                    timeComplexity++;
                } else {
                    opened.remove(node);
                    Node actual_node = new Node(node);
                    opened.add(actual_node);
/*
                    if (node.g < actual_node.g) {
                        actual_node.g = node.g;
                        actual_node.f = node.f;
                        actual_node.parent = node.parent;
                    }
*/
                }
            }
        }
        System.out.println("No solution founded");
        return null;
    }

    public List<Node> getNextNodes(Node node) {
        List<Node> result = new ArrayList<>();
        for (Dir dir : Dir.values()) {
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

    public List<Dir> getPath(Node node) {
        List<Dir> moves = new ArrayList<>();
        Node current = node;
        while (current.dir != null) {
            moves.add(current.dir);
            current = current.parent;
        }
        Collections.reverse(moves);
        return moves;
    }
}
