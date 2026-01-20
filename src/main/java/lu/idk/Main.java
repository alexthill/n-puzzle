package lu.idk;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome to n-puzzle!");

        Board board = new Board(4);
        board.move(Board.Dir.UP);
        board.move(Board.Dir.LEFT);
        System.out.println(board.prettyString());
    }
}
