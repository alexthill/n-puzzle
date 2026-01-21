package lu.idk;

import lu.idk.heuristics.Manhattan;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*System.out.println("Hello and welcome to n-puzzle!\n");

        Board board = Board.snail(4);
        System.out.println("Snail " + board.prettyString());
        System.out.println("h(Snail) = " + heuristic(board));
        System.out.println("Is solvable = " + board.isSolvable());

        board = Board.random(4);
        System.out.println("Random " + board.prettyString());
        System.out.println("h(Random) = " + heuristic(board));
        System.out.println("Is solvable = " + board.isSolvable());

        if (args.length > 0) {
            try {
                board = BoardParser.parse(args[0]);
                System.out.println("Parsed " + board.prettyString());
                System.out.println("h(Parsed) = " + heuristic(board));

            } catch (Exception e) {
                System.out.println("Error while reading input file \"" + args[0] + "\": " + e);
            }
        }*/

        System.out.println("\n\nSTARTING ALGO");
        ctrlz(args);
    }

    private static int heuristic(Board board) {
        Manhattan heuristic = new Manhattan(Board.snail(board.getN()));
        return heuristic.h(board);
    }

    private static void ctrlz(String[] args) {
        Board snail = Board.snail(4);
        System.out.println("Snail " + snail.prettyString());
        Manhattan heuristic = new Manhattan(snail);
//        AStar aStar = new AStar();
//        Const heuristic = new Const();
//        aStar.init(heuristic);
        IDAStar algo = new IDAStar(heuristic, snail);
        Board board = Board.random(4);
        System.out.println("Is solvable = " + board.isSolvable());
        System.out.println(board.prettyString());
        long before = System.currentTimeMillis();
//        List<Board.Dir> moves = aStar.search(board, snail);
        algo.idaStar(board);
        long after = System.currentTimeMillis();
        System.out.println("After board: " + board.prettyString());
//        System.out.println("Solution: " + board.isSolutionValid(moves));
        System.out.printf("Time taken: %d\n", after - before);
        if (args.length > 0) {
            try {
//                Board board = BoardParser.parse(args[0]);
            } catch (Exception e) {
                System.out.println("Error while reading input file \"" + args[0] + "\": " + e);
            }
        }
    }
}
