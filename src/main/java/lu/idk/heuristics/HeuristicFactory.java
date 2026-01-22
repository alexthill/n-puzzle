package lu.idk.heuristics;

import lu.idk.Board;

public class HeuristicFactory {

    private static HeuristicFactory instance;

    public static HeuristicFactory getInstance() {
        if (instance == null) {
            instance = new HeuristicFactory();
        }
        return instance;
    }

    private HeuristicFactory() {
    }

    public static IHeuristic newHeuristic(Board target, String name) throws HeuristicFactoryException {
        if (name.equalsIgnoreCase("const")) {
            return new Const();
        }
        if (name.equalsIgnoreCase("hamming")) {
            return new Hamming(target);
        }
        if (name.equalsIgnoreCase("manhattan")) {
            return new Manhattan(target);
        }
        if (name.equalsIgnoreCase("pattern")) {
            return new StaticAdditivePattern(target);
        }

        throw new HeuristicFactoryException("invalid heuristic '" + name + "'");
    }

    public static class HeuristicFactoryException extends Exception {

        public HeuristicFactoryException(String message) {
            super(message);
        }
    }
}
