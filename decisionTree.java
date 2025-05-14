package CS370_Team7;
import java.util.*;

public class decisionTree {
    private treeNode root;
    private dataset dataset;

    // Constructor to accept dataset and a list of indices for training
    public decisionTree(dataset dataset, List<Integer> rowIndices) {
        this.dataset = dataset;
        this.root = new treeNode(dataset, rowIndices);  // Initialize the root node with bootstrap indices
    }

    public void train() {
        // Train the decision tree starting from the root node
        if (root != null) {
            root.split();
        }
    }

    public String predict(String[] instance) {
        treeNode current = root;
        while (!current.isTerminal()) {
            int col = current.getSplitCol();
            String threshold = current.getSplitThreshold();
            String value = instance[col];

            if (isLessThanOrEqual(value, threshold, col)) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        return current.getDiagnosisResult();
    }

    private boolean isLessThanOrEqual(String val, String threshold, int colIndex) {
        // Reuse the logic from treeNode (you may consider refactoring this into a shared static method)
        try {
            if (Arrays.stream(dataset.integerIndexs).anyMatch(x -> x == colIndex)) {
                return Float.parseFloat(val) <= Float.parseFloat(threshold);
            }

            if (colIndex == 2) {
                return val.equalsIgnoreCase("female");
            }

            switch (colIndex) {
                case 5: case 13: case 16: case 20: case 21: case 22:
                    return convertOrdinal(val) <= convertOrdinal(threshold);
                case 14:
                    return convertSleep(val) <= convertSleep(threshold);
                case 15:
                    return convertDiet(val) <= convertDiet(threshold);
                case 8: case 9: case 11: case 19: case 24:
                    return convertBinary(val) <= convertBinary(threshold);
                default:
                    return val.compareTo(threshold) <= 0;
            }
        } catch (NumberFormatException e) {
            return val.compareTo(threshold) <= 0;
        }
    }

    private int convertOrdinal(String val) {
        return switch (val.toLowerCase()) {
            case "low" -> 1;
            case "medium" -> 2;
            case "high" -> 3;
            default -> 0;
        };
    }

    private int convertSleep(String val) {
        return switch (val.toLowerCase()) {
            case "poor" -> 1;
            case "average" -> 2;
            case "good" -> 3;
            default -> 0;
        };
    }

    private int convertDiet(String val) {
        return switch (val.toLowerCase()) {
            case "unhealthy" -> 1;
            case "average" -> 2;
            case "healthy" -> 3;
            default -> 0;
        };
    }

    private int convertBinary(String val) {
        return val.equalsIgnoreCase("yes") ? 1 : 0;
    }

    public void printTree() {
        if (root != null) {
            root.printTree("");
        } else {
            System.out.println("Tree not trained.");
        }
    }
}
