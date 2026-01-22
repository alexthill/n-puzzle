package lu.idk;

import lu.idk.heuristics.IHeuristic;

import java.util.Stack;

public class IDAStar {

    Stack<Node> path = new Stack<>();
    IHeuristic heuristic;
    boolean greedy;
    Board finalBoard;
    int timeComplexity = 0;
    int sizeComplexity = 0;

    public IDAStar(IHeuristic heuristic, Board finalBoard, boolean greedy) {
        this.heuristic = heuristic;
        this.finalBoard = finalBoard;
        this.greedy = greedy;
    }

    TheSolution idaStar(Board startBoard) {
        int bound = this.heuristic.h(startBoard);
        Node startNode = new Node(startBoard, null, null);
        path.push(startNode);
        timeComplexity++;

        while (true) {
            Solution solution = search(0, bound);
            if (solution.state == Solution.SolutionState.FOUND) {
                return new TheSolution(solution.solution, timeComplexity, sizeComplexity);
            }
            if (solution.state == Solution.SolutionState.NOT_FOUND) {
                System.out.println("There is no solution for this board.");
                return null;
            }
            bound = solution.t;
        }
    }

    Solution search(int g, int bound) {
        if (greedy) {
            g = 0;
        }
        if (path.size() > sizeComplexity) {
            sizeComplexity = path.size();
        }

        Node node = path.lastElement();
        int f = g + heuristic.h(node.board);
        if (f > bound) {
            return new Solution(f);
        }
        if (node.board.boardEquals(finalBoard)) {
            return new Solution(f, Solution.SolutionState.FOUND, node.getPath());
        }
        int min = Integer.MAX_VALUE;
        for (Node child : node.getNextNodes(heuristic)) {
            if (!path.contains(child)) {
                path.push(child);
                timeComplexity++;
                Solution solution = search(g + 1, bound);
                if (solution.state == Solution.SolutionState.FOUND) {
                    return solution;
                }
                if (solution.t < min) {
                    min = solution.t;
                }
                path.pop();
            }
        }
        return new Solution(min);
    }

}
