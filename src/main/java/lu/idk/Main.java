package lu.idk;

import lu.idk.heuristics.HeuristicFactory;
import lu.idk.heuristics.HeuristicFactory.HeuristicFactoryException;
import lu.idk.heuristics.IHeuristic;

import java.lang.NumberFormatException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: n-puzzle [input file] [heuristic]");
            System.err.println("or:    n-puzzle [random board size] [heuristic]");
            System.err.println("\nSupported heuristics:");
            System.err.println("  - const (always returns 1)");
            System.err.println("  - manhattan (sums of manhattan distances of misplaced tiles)");
            System.err.println("  - pattern (generates and uses a pattern database)");
            System.exit(1);
        }

        Board board;
        try {
            int n = Integer.parseInt(args[0]);
            if (n < 2 || n > 42) {
                System.err.printf("invalid board size %d given, must in range [2, 42]\n", n);
                System.exit(1);
            }
            System.out.printf("Generating random board of size %d\n", n);
            board = Board.random(n);
        } catch (NumberFormatException e) {
            board = parseBoard(args[0]);
        }

        IHeuristic heuristic = null;
        try {
            Board snail = Board.snail(board.getN());
            heuristic = HeuristicFactory.newHeuristic(snail, args[1]);
        } catch (HeuristicFactoryException e) {
            System.err.printf("Failed create heurisitc: %s\n", e.getMessage());
            System.exit(1);
        }

        if (board != null && heuristic != null) {
            System.out.printf("Solving board\n%s\n", board.prettyString());
//            System.out.println("Is reachable: " + board.isReachable(Board.snail(board.getN())));
            if (board.isSolvable()) {
                ctrlz(board, heuristic);
            } else {
                System.out.println("Board is not solvable!");
            }
        } else {
            System.err.println("unexpected null");
        }
    }

    private static Board parseBoard(String arg) {
        System.out.printf("Reading board from file %s\n", arg);
        try {
            return BoardParser.parse(arg);
        } catch (Exception e) {
            System.err.printf("Failed to parse board: %s\n", e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static void ctrlz(Board board, IHeuristic heuristic) {
        Board snail = Board.snail(board.getN());
        System.out.println("Snail " + snail.prettyString());
//        AStar aStar = new AStar();
//        Const heuristic = new Const();
//        aStar.init(heuristic);
        IDAStar algo = new IDAStar(heuristic, snail);
        long before = System.currentTimeMillis();
//        List<Board.Dir> moves = aStar.search(board, snail);
        TheSolution solution = algo.idaStar(board);
        long after = System.currentTimeMillis();
        if (solution != null) {
            System.out.println("States: ");
            printStates(solution.solution, board);
            System.out.println("Solution: " + board.isSolutionValid(solution.solution));
            System.out.println("Number of moves: " + solution.solution.size());
            System.out.println("Time complexity: " + solution.timeComplexity);
            System.out.println("Size complexity: " + solution.sizeComplexity);
        }
        System.out.printf("Time taken: %d ms\n", after - before);
    }

    private static void printStates(List<Board.Dir> moves, Board initialBoard) {
        Board board = initialBoard.clone();
        System.out.println(board.prettyString());
        for (Board.Dir move : moves) {
            board.move(move);
            System.out.println(move);
            System.out.println(board.prettyString());
        }
    }
}
