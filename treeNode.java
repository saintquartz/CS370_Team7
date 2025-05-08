import java.util.*;

public class TreeNode {
    private Dataset dataset;    // Entire imported dataset, already imputated
    private List<Integer> rowIndices;   // Stores the indices of the subset rows
    private boolean isTerminal;       // 0 if not leafnode, 1 if it is
    private TreeNode left;
    private TreeNode right;
    private int splitCol;           // Column index used to split
    private String splitThreshold; // Value used to split
    private String diagnosisResult; // Final diagnosis returned as a string

    //Constructor for our treenode with subset rows, assuming its not terminal
    public TreeNode(Dataset dataset, List<Integer> rowIndices) {
        this.dataset = dataset;
        this.rowIndices = rowIndices;
        this.isTerminal = false;
    }

    // Getter methods for terminal and diagnosis result
    public boolean isTerminal() {
        return isTerminal;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    //Split takes in as input the column we split by and the threshold associated with that attribute
    public void split(int columnIndex, String threshold) {
        this.splitCol = columnIndex;
        this.splitThreshold = threshold;

        //Two lists to store what rows are split left or right
        List<Integer> leftIndices = new ArrayList<>();
        List<Integer> rightIndices = new ArrayList<>();

        // For each row in our subset, split left or right based on attribute threshold
        for (int i : rowIndices) {
            String value = dataset.dataset[i][columnIndex];
            if (isLessThanOrEqual(value, threshold, columnIndex)) {
                leftIndices.add(i);
            } else {
                rightIndices.add(i);
            }
        }

        // If we can't split any more, isTerminal = true, set diagnosisResult based on subset
        if (leftIndices.isEmpty() || rightIndices.isEmpty()) {
            this.isTerminal = true;
            this.diagnosisResult = majorityDiagnosis(rowIndices);
        } else {
            this.left = new TreeNode(dataset, leftIndices);
            this.right = new TreeNode(dataset, rightIndices);
        }
    }

    // IsLessThanOrEqual takes in a string value from the subset, determines if its numerical or categorical
    //If numerical, compare to threshold, if categorical, compare alphabetically
    private boolean isLessThanOrEqual(String val, String threshold, int colIndex) {
        try {
            if (Arrays.stream(dataset.integerIndexs).anyMatch(x -> x == colIndex)) {
                return Integer.parseInt(val) <= Integer.parseInt(threshold);
            } else {
                return val.compareTo(threshold) <= 0;
            }
        } catch (NumberFormatException e) {
            return val.compareTo(threshold) <= 0;
        }
    }

    // majorityDiagnosis is called when we reach a terminal node in order to assign a diagnosis
    // It takes in as input the rows of the subset stored by the treeNode
    private String majorityDiagnosis(List<Integer> indices) {
        Map<String, Integer> countMap = new HashMap<>();
        for (int idx : indices) {   // For each row in the subset, retrieve diagnosis from last column, and increment in the hashmap
            String diag = dataset.dataset[idx][24]; // assuming column 24 is the label
            countMap.put(diag, countMap.getOrDefault(diag, 0) + 1);
        }
        return countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}

public class Main {
    public static void main(String[] args) {

    }
}
