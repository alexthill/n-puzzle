package lu.idk.heuristics;

import lu.idk.Board;

public class Manhattan implements IHeuristic {

    private Board target;
    private int hole;
    private int[] targetXs;
    private int[] targetYs;

    public Manhattan(Board target) {
        this.target = target;

        int n = target.getN();
        int size = target.getSize();
        targetXs = new int[n * n + 1];
        targetYs = new int[n * n + 1];
        hole = n * n;

        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = target.getAtCoords(x, y);
                targetXs[num] = x;
                targetYs[num] = y;
            }
        }
    }

    @Override
    public int h(Board board) {
        int dist = 0;
        int size = target.getSize();

        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = board.getAtCoords(x, y);
                if (num == hole) {
                    continue;
                }

                int targetX = targetXs[num];
                int targetY = targetYs[num];
                dist += Math.abs(x - targetX) + Math.abs(y - targetY);
            }
        }

        return dist;
    }
}
