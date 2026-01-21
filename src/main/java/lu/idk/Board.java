package lu.idk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
    public enum Dir {LEFT, RIGHT, UP, DOWN}

    public static Board snail(int n) {
        Board board = new Board(n);
        board.snailPatter();
        return board;
    }

    public static Board random(int n) {
        Board board = new Board(n);
        board.randomPattern();
        return board;
    }

    private int n;
    private int size;
    private int sizeSqr;
    private int holeIdx;
    private int[] grid;

    private Board(int n) {
        this.n = n;
        size = n + 2;
        sizeSqr = size * size;
        grid = new int[sizeSqr];
    }

    public Board(int n, int[] array) throws Exception {
        if (array.length != n * n) {
            throw new Exception("array.length != n^2");
        }

        this.n = n;
        size = n + 2;
        sizeSqr = size * size;
        grid = new int[sizeSqr];

        int i = 0;
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = array[i++];
                if (num == 0) {
                    holeIdx = coordsToIdx(x, y);
                    grid[x + y * size] = n * n;
                } else {
                    grid[x + y * size] = num;
                }
            }
        }

        // Check if array contains numbers from 0 to n^2 after we have copied it to grid.
        // This allows us to modifiy it without making a copy.
        Arrays.sort(array);
        for (i = 0; i < n * n; ++i) {
            if (array[i] != i) {
                throw new Exception("array does not contain exactly the numbers from 0 to N^2");
            }
        }
    }

    public int getN() {
        return n;
    }

    public int dirToValue(Dir dir) {
        switch (dir) {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            case UP:
                return -size;
            case DOWN:
                return size;
        }
        return 0;
    }

    public boolean isMoveValid(Dir dir) {
        return !isBorder(holeIdx + dirToValue(dir));
    }

    public boolean move(Dir dir) {
        int diff = dirToValue(dir);
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
        return new int[]{(idx % size) - 1, idx / size - 1};
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
                        sb.append("─".repeat(cellW));
                    }
                } else if (num == n * n) {
                    for (int i = 1; i < cellW; ++i) {
                        sb.append(" ");
                    }
                    sb.append("_");
                } else {
                    sb.append(String.format("%" + cellW + "d", num));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public Board clone() {
        Board board = new Board(n);
        board.holeIdx = holeIdx;
        for (int i = 0; i < sizeSqr; ++i) {
            board.grid[i] = grid[i];
        }
        return board;
    }

    private void snailPatter() {
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                grid[x + y * size] = n * n;
            }
        }

        final Dir[] dirs = new Dir[]{Dir.RIGHT, Dir.DOWN, Dir.LEFT, Dir.UP};
        int dirIdx = 0;
        int pos = size + 1;

        for (int i = 1; i < n * n; ++i) {
            int[] coords = indexToCoords(pos);
            grid[pos] = i;
            int next = pos + dirToValue(dirs[dirIdx]);
            if (grid[next] != n * n) {
                dirIdx = (dirIdx + 1) % dirs.length;
                next = pos + dirToValue(dirs[dirIdx]);
            }
            pos = next;
        }

        holeIdx = pos;
    }

    private void randomPattern() {
        List<Integer> list = new ArrayList<>(n);
        for (int i = 1; i <= n * n; i++) {
            list.add(i);
        }
        Collections.shuffle(list);

        int i = 0;
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = list.get(i++);
                grid[x + y * size] = num;
                if (num == n * n) {
                    holeIdx = coordsToIdx(x, y);
                }
            }
        }
    }
}
