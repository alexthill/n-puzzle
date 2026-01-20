package lu.idk;

public class Board {
    public enum Dir { LEFT, RIGHT, UP, DOWN }

    private int n;
    private int size;
    private int sizeSqr;
    private int holeIdx;
    private int[] grid;

    public Board(int n) {
        this.n = n;

        size = n + 2;
        sizeSqr = size * size;
        grid = new int[sizeSqr];
        holeIdx = sizeSqr - size - 2;

        int num = 1;
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                grid[x + y * size] = num;
                num++;
            }
        }
    }

    public boolean move(Dir dir) {
        int diff = 0;
        switch (dir) {
            case LEFT: diff = -1; break;
            case RIGHT: diff = 1; break;
            case UP: diff = -size; break;
            case DOWN: diff = size; break;
        }
        if (isBorder(holeIdx + diff)) {
            return false;
        }

        int tmp = grid[holeIdx];
        grid[holeIdx] = grid[holeIdx + diff];
        grid[holeIdx + diff] = tmp;
        holeIdx += diff;

        return true;
    }

    public int[] indexToCoords(int idx) {
        return new int[] { (idx % size) - 1, idx / size - 1 };
    }

    public int coordsToIdx(int x, int y) {
        return x + y * size;
    }

    public boolean isBorder(int idx) {
        return grid[idx] == 0;
    }

    public String prettyString() {
        StringBuilder sb = new StringBuilder("Board ");
        sb.append(n);
        sb.append("x");
        sb.append(n);

        int[] hole = indexToCoords(holeIdx);
        sb.append(" (hole at ");
        sb.append(hole[0]);
        sb.append(",");
        sb.append(hole[1]);
        sb.append(")\n");

        int cellW = 1;
        if (n * n > 10) {
            cellW = 3;
        }

        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                int num = grid[coordsToIdx(x, y)];
                if (num == 0) {
                    if (x == 0 && y == 0) {
                        sb.append("┌");
                    } else if (x == size - 1 && y == 0) {
                        sb.append("┐");
                    } else if (x == 0 && y == size - 1) {
                        sb.append("└");
                    } else if (x == size - 1 && y == size - 1) {
                        sb.append("┘");
                    } else if (x == 0 || x == size - 1) {
                        sb.append("│");
                    } else {
                        for (int i = 0; i < cellW; ++i) {
                            sb.append("─");
                        }
                    }
                } else if (num == n * n) {
                    for (int i = 1; i < cellW; ++i) {
                        sb.append(" ");
                    }
                    sb.append("_");
                } else {
                    sb.append(String.format("%" + cellW + "d", num));
                }
                num++;
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
