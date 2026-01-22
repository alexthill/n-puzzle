package lu.idk.heuristics;

import lu.idk.Board;
import lu.idk.Board.Dir;
import lu.idk.heuristics.HeuristicFactory.HeuristicFactoryException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class StaticAdditivePattern implements IHeuristic {

    private static final int DATABASE_VERSION = 2;
    private static final int[][][] PATTERNS_3 = new int[][][]{
            {{1, 1}, {1, 2}, {1, 3}, {2, 1}},
            {{3, 1}, {3, 2}, {3, 3}, {2, 3}},
    };
    private static final int[][][] PATTERNS_4 = new int[][][]{
            {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 2}},
            {{2, 1}, {3, 1}, {3, 2}, {4, 1}, {4, 2}},
            {{4, 2}, {3, 3}, {3, 4}, {4, 3}, {4, 4}},
    };

    private final int[][][] patterns;
    private final Board target;
    private ArrayList<HashMap<Integer, Integer>> tables;
    private final ArrayList<PatternData> patternData;

    public StaticAdditivePattern(Board target) throws HeuristicFactoryException {
        this.target = target;
        int n = target.getN();

        switch (n) {
            case 3:
                patterns = PATTERNS_3;
                if (target.getHoleIdx() != target.coordsToIdx(2, 2)) {
                    throw new HeuristicFactoryException("hole not at expected position");
                }
                break;
            case 4:
                patterns = PATTERNS_4;
                if (target.getHoleIdx() != target.coordsToIdx(2, 3)) {
                    throw new HeuristicFactoryException("hole not at expected position");
                }
                break;
            default:
                throw new HeuristicFactoryException("StaticAdditivePattern can only be used with N=3 or N=4");
        }

        patternData = new ArrayList<>(patterns.length);
        for (int[][] pattern : patterns) {
            patternData.add(new PatternData(pattern, target));
        }

        boolean readValidTable = readDatabase();
        if (!readValidTable) {
            System.out.println("computing pattern database (this may take some time)");
            tables = new ArrayList<>(patterns.length);
            long start = System.currentTimeMillis();
            for (int i = 0; i < patterns.length; ++i) {
                tables.add(computeDatabase(patternData.get(i)));
            }
            long end = System.currentTimeMillis();
            System.out.printf("time to compute pattern database %d ms\n", end - start);
            saveDatabase();
        }
    }

    @Override
    public int h(Board board) {
        int dist = 0;

        for (int patternIdx = 0; patternIdx < patterns.length; ++patternIdx) {
            PatternData data = patternData.get(patternIdx);
            int boardIdx = computeBoardIdx(board, data.isNumInPattern, data.numOrder);
            dist += tables.get(patternIdx).get(boardIdx);
        }

        return dist;
    }

    private String getDatabaseName() {
        int n = target.getN();
        return "pattern_database_n" + n + "_v" + DATABASE_VERSION + ".bin";
    }

    private void saveDatabase() {
        String file = getDatabaseName();
        System.out.printf("saving pattern database to %s\n", file);
        try (
                FileOutputStream os = new FileOutputStream(file);
                BufferedOutputStream bs = new BufferedOutputStream(os);
                DataOutputStream out = new DataOutputStream(bs)
        ) {
            out.writeInt(DATABASE_VERSION);
            out.writeInt(target.getN());
            out.writeInt(tables.size());
            for (HashMap<Integer, Integer> table : tables) {
                out.writeInt(table.size());
                for (HashMap.Entry<Integer, Integer> entry : table.entrySet()) {
                    out.writeInt(entry.getKey());
                    out.writeInt(entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("failed to write database: " + e);
        }
    }

    private boolean readDatabase() {
        String file = getDatabaseName();
        System.out.printf("reading pattern database from %s\n", file);
        try (
                FileInputStream is = new FileInputStream(file);
                BufferedInputStream bs = new BufferedInputStream(is);
                DataInputStream in = new DataInputStream(bs)
        ) {
            if (in.readInt() != DATABASE_VERSION) {
                throw new Exception("invalid database: bad version");
            }
            if (in.readInt() != target.getN()) {
                throw new Exception("invalid database: bad n");
            }

            int tableCount = in.readInt();
            if (tableCount != patterns.length) {
                throw new Exception("invalid database: bad tableCount");
            }

            tables = new ArrayList<>(tableCount);
            for (int i = 0; i < tableCount; ++i) {
                int size = in.readInt();
                HashMap<Integer, Integer> table = new HashMap<>(size);
                for (int j = 0; j < size; ++j) {
                    int key = in.readInt();
                    int val = in.readInt();
                    if (table.put(key, val) != null) {
                        throw new Exception("invalid database: duplicate key");
                    }
                }
                tables.add(table);
            }
        } catch (Exception e) {
            System.out.println("failed to read database: " + e.getMessage());
            return false;
        }
        return true;
    }

    private HashMap<Integer, Integer> computeDatabase(PatternData data) {
        final int size = target.getSize();
        final int n = target.getN();

        Board board = target.clone();
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                if (!data.isNumInPattern[board.getAtCoords(x, y)]) {
                    board.setAtCoords(x, y, n * n);
                }
            }
        }

        final Dir[] dirs = new Dir[]{Dir.RIGHT, Dir.DOWN, Dir.LEFT, Dir.UP};

        HashMap<Integer, Integer> table = new HashMap<>();
        HashSet<Long> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(board, 0));

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            int boardIdx = computeBoardIdx(curr.board, data.isNumInPattern, data.numOrder);
            long boardIdxFull = ((long) boardIdx << 8) | curr.board.getHoleIdx();

            if (!visited.add(boardIdxFull)) {
                continue;
            }
            // if (visited.size() % 100000 == 0)
            //     System.out.printf("visited %d, queue size %d\n", visited.size(), queue.size());

            for (Dir dir : dirs) {
                if (curr.board.isMoveValid(dir)) {
                    Board next = curr.board.clone();
                    int num = next.move(dir);
                    if (data.isNumInPattern[num]) {
                        queue.add(new Node(next, curr.cost + 1));
                    } else {
                        queue.add(new Node(next, curr.cost));
                    }
                }
            }

            table.merge(boardIdx, curr.cost, (oldVal, newVal) -> oldVal > newVal ? newVal : oldVal);
        }

        /*int costSum = 0;
        int costMax = 0;
        for (HashMap.Entry<Integer, Integer> entry : table.entrySet()) {
            int cost = entry.getValue();
            costSum += cost;
            costMax = Math.max(costMax, cost);
        }

        System.out.println("table size: " + table.size());
        System.out.println("cost sum " + costSum + ", cost max " + costMax);*/

        return table;
    }

    private static int computeBoardIdx(Board board, boolean[] isNumInPattern, int[] numOrder) {
        int size = board.getSize();
        int idx = 0;

        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = board.getAtCoords(x, y);
                if (isNumInPattern[num]) {
                    idx |= (x | (y << 3)) << (6 * numOrder[num]);
                }
            }
        }

        return idx;
    }

    private static class Node implements Comparable<Node> {
        public Board board;
        public int cost;

        public Node(Board board, int cost) {
            this.board = board;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node o) {
            return this.cost - o.cost;
        }
    }

    private static class PatternData {
        public boolean[] isNumInPattern;
        public int[] numOrder;
        public int[] numsInPattern;

        public PatternData(int[][] pattern, Board target) {
            int n = target.getN();
            int numsInPatternIdx = 0;
            int nextNumOrder = 0;

            isNumInPattern = new boolean[n * n + 1];
            numOrder = new int[n * n + 1];
            numsInPattern = new int[pattern.length];

            for (int[] coords : pattern) {
                int num = target.getAtCoords(coords[0], coords[1]);
                isNumInPattern[num] = true;
                numsInPattern[numsInPatternIdx++] = num;
                numOrder[num] = nextNumOrder++;
            }
        }
    }
}
