package lu.idk;

import java.util.List;

public class TheSolution {
    public List<Board.Dir> solution;
    public int timeComplexity;
    public int sizeComplexity;

    public TheSolution(List<Board.Dir> solution, int timeComplexity, int sizeComplexity) {
        this.solution = solution;
        this.timeComplexity = timeComplexity;
        this.sizeComplexity = sizeComplexity;
    }
}
