package lu.idk;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome to n-puzzle!\n");

        Board board = Board.snail(4);
        System.out.println("Snail " + board.prettyString());

        board = Board.random(4);
        System.out.println("Random " + board.prettyString());

        board = board.clone();
        System.out.println("Cloned " + board.prettyString());
    }
}
