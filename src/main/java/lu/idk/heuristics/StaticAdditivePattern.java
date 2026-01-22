package lu.idk.heuristics;

import lu.idk.Board;
import lu.idk.Board.Dir;
import lu.idk.heuristics.HeuristicFactory.HeuristicFactoryException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class StaticAdditivePattern implements IHeuristic {

    private static final int[][][] PATTERNS = new int[][][] {
        {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 2}},
        {{2, 1}, {3, 1}, {3, 2}, {4, 1}, {4, 2}},
        {{4, 2}, {3, 3}, {3, 4}, {4, 3}, {4, 4}},
    };

    private Board target;
    private int hole;
    private int[] targetXs;
    private int[] targetYs;

    public StaticAdditivePattern(Board target) throws HeuristicFactoryException {
        this.target = target;
        int n = target.getN();
        hole = n * n;

        if (n != 4) {
            throw new HeuristicFactoryException("StaticAdditivePattern can only be used with N=4");
        }
        if (target.getAtCoords(2, 3) != hole) {
            throw new HeuristicFactoryException("hole not at expected position");
        }

        computeDatabase(PATTERNS[0]);

        int size = target.getSize();
        targetXs = new int[n * n + 1];
        targetYs = new int[n * n + 1];

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

    private void computeDatabase(int[][] pattern) {
        int n = target.getN();
        int size = target.getSize();
        boolean[] isNumInPattern = new boolean[n * n + 1];
        int[] numOrder = new int[n * n + 1];
        int[] numsInPattern = new int[pattern.length];

        int numsInPatternIdx = 0;
        int nextNumOrder = 0;
        for (int[] coords: pattern) {
            int num = target.getAtCoords(coords[0], coords[1]);
            isNumInPattern[num] = true;
            numsInPattern[numsInPatternIdx++] = num;
            numOrder[num] = nextNumOrder++;
            System.out.println("in pattern: " + num + ", order " + numOrder[num]);
        }

        Board board = target.clone();
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                if (!isNumInPattern[board.getAtCoords(x, y)]) {
                    board.setAtCoords(x, y, -1);
                }
            }
        }

        final Dir[] dirs = new Dir[] {Dir.RIGHT, Dir.DOWN, Dir.LEFT, Dir.UP};

        HashMap<Integer, Integer> table = new HashMap<>();
        HashSet<Long> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(board, 0));

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            int boardIdx = tableBoardIdx(curr.board, isNumInPattern, numOrder);
            long boardIdxFull = ((long)boardIdx << 8) | curr.board.getHoleIdx();

            if (!visited.add(boardIdxFull)) {
                continue;
            }
            if (visited.size() % 100000 == 0)
                System.out.println("visited: " + visited.size());

            for (Dir dir: dirs) {
                if (curr.board.isMoveValid(dir)) {
                    Board next = curr.board.clone();
                    int num = next.move(dir);
                    if (num != -1 && isNumInPattern[num]) {
                        queue.add(new Node(next, curr.cost + 1));
                    } else {
                        queue.add(new Node(next, curr.cost));
                    }
                }
            }

            table.merge(boardIdx, curr.cost, (oldVal, newVal) -> {
                return oldVal == null || oldVal > newVal ? newVal : oldVal;
            });
        }

        int costSum = 0;
        int costMax = 0;
        for (HashMap.Entry<Integer, Integer> entry: table.entrySet()) {
            int cost = entry.getValue();
            costSum += cost;
            costMax = Math.max(costMax, cost);
        }

        System.out.println("table size: " + table.size());
        System.out.println("cost sum " + costSum + ", cost max " + costMax);
    }

    private static int tableBoardIdx(Board board, boolean[] isNumInPattern, int[] numOrder) {
        int size = board.getSize();
        int idx = 0;
        for (int y = 1; y < size - 1; ++y) {
            for (int x = 1; x < size - 1; ++x) {
                int num = board.getAtCoords(x, y);
                if (num != -1 && isNumInPattern[num]) {
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
}
