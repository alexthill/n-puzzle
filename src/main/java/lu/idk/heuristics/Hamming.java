package lu.idk.heuristics;

import lu.idk.Board;

public class Hamming implements IHeuristic {

    Board target;
    int size;

    public Hamming(Board target) {
        this.target = target;
        size = target.getSize();
    }

    @Override
    public int h(Board board) {
        int result = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int targetValue = target.getAtCoords(x, y);
                int boardValue = board.getAtCoords(x, y);
                if (targetValue != 0 && targetValue != boardValue) {
                    result++;
                }
            }
        }
        return result;
    }
}
