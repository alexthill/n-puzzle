package lu.idk;

import lu.idk.heuristics.IHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {

    public Node startNode;
    public IHeuristic heuristic;

    public List<Node> open = new ArrayList<>();
    public List<Node> closed = new ArrayList<>();

    public void init(IHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    public void search(Board startBoard, Board finalBoard) {
        startNode = new Node(startBoard, null, null);
        startNode.update(-1, heuristic);
        while (!open.isEmpty()) {
            open.sort(Comparator.comparing(item -> item.f));
            Node process = open.get(0);
            if (process.board == finalBoard)
                return;
            open.remove(process);
            closed.add(process);
            for (Node node : getNextNodes(process)) {
                if (closed.contains(node))
                    continue;
                if (!open.contains(node))
                    open.add(node);
                else {
                    Node actual_node = open.get(open.indexOf(node));
                    if (node.g < actual_node.g) {
                        actual_node.g = node.g;
                        actual_node.f = node.f;
                        actual_node.parent = node.parent;
                    }
                }
            }
        }
    }

    public List<Node> getNextNodes(Node node) {
        List<Node> result = new ArrayList<>();
        for (Board.Dir dir : Board.Dir.values()) {
            if (node.board.isMoveValid(dir)) {
                Board newBoard = node.board.clone();
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
        while (current != null) {
            moves.add(current.dir);
            current = current.parent;
        }
        Collections.reverse(moves);
        return moves;
    }
}
