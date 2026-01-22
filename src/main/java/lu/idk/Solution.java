package lu.idk;

import java.util.List;

public class Solution {

    public enum SolutionState {FOUND, NOT_FOUND, SEARCHING}

    List<Board.Dir> solution;
    int t;
    SolutionState state = SolutionState.SEARCHING;

    public Solution(int t) {
        solution = null;
        this.t = t;
        if (t == Integer.MAX_VALUE) {
            state = SolutionState.NOT_FOUND;
        }

    }

    public Solution(int t, SolutionState state) {
        solution = null;
        this.t = t;
        this.state = state;
    }

    public Solution(int t, SolutionState state, List<Board.Dir> solution) {
        this.solution = solution;
        this.t = t;
        this.state = state;
    }


}
