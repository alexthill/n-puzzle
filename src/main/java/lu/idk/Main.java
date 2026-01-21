package lu.idk;

import lu.idk.heuristics.Manhattan;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome to n-puzzle!\n");

        Board board = Board.snail(4);
        System.out.println("Snail " + board.prettyString());
        System.out.println("h(Snail) = " + heuristic(board));

        board = Board.random(4);
        System.out.println("Random " + board.prettyString());
        System.out.println("h(Random) = " + heuristic(board));

        if (args.length > 0) {
            try {
                board = BoardParser.parse(args[0]);
                System.out.println("Parsed " + board.prettyString());
                System.out.println("h(Parsed) = " + heuristic(board));
            } catch (Exception e) {
                System.out.println("Error while reading input file \"" + args[0] + "\": " + e);
            }
        }
    }

    private static int heuristic(Board board) {
        Manhattan heuristic = new Manhattan(Board.snail(board.getN()));
        return heuristic.h(board);
    }
}
