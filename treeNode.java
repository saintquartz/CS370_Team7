package CS370_Team7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class treeNode {
    private dataset dataset;
    private List<Integer> rowIndices;
    private boolean isTerminal;
    private treeNode left;
    private treeNode right;
    private int splitCol;
    private String splitThreshold;
    private String diagnosisResult;

    public treeNode(dataset dataset, List<Integer> rowIndices) {
        this.dataset = dataset;
        this.rowIndices = rowIndices;
        this.isTerminal = false;
    }


    public int getSplitCol() {
        return splitCol;
    }

    public String getSplitThreshold() {
        return splitThreshold;
    }

    public treeNode getLeft() {
        return this.left;
    }
    
    public treeNode getRight() {
        return this.right;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void debugPrint() {
        System.out.println("Is terminal: " + isTerminal);
        System.out.println("Split column: " + splitCol);
        System.out.println("Split threshold: " + splitThreshold);
        System.out.println("Diagnosis (if terminal): " + diagnosisResult);
        System.out.println("Left child exists: " + (left != null));
        System.out.println("Right child exists: " + (right != null));
    }



    public void split() {
        if (rowIndices.size() < 10) {
            this.isTerminal = true;
            this.diagnosisResult = majorityDiagnosis(rowIndices);
            return;
        }
    
        Map.Entry<Integer, String> bestSplit = computeThreshold();
        if (bestSplit == null || bestSplit.getKey() == -1 || bestSplit.getValue() == null) {
            this.isTerminal = true;
            this.diagnosisResult = majorityDiagnosis(rowIndices);
            //System.out.println("No valid split found for current node. Marking as terminal.");
            return;
        }
    
        this.splitCol = bestSplit.getKey();
        this.splitThreshold = bestSplit.getValue();
    
        List<Integer> leftIndices = new ArrayList<>();
        List<Integer> rightIndices = new ArrayList<>();
    
        for (int i : rowIndices) {
            String value = dataset.getValue(i, splitCol);
            if (isLessThanOrEqual(value, splitThreshold, splitCol)) {
                leftIndices.add(i);
            } else {
                rightIndices.add(i);
            }
        }
    
        if (leftIndices.isEmpty() || rightIndices.isEmpty()) {
            this.isTerminal = true;
            this.diagnosisResult = majorityDiagnosis(rowIndices);
        } else {
            this.left = new treeNode(dataset, leftIndices);
            this.right = new treeNode(dataset, rightIndices);
            left.split();
            right.split();
        }
    
        //System.out.println("Splitting on col[" + splitCol + "] <= " + splitThreshold);
        //System.out.println("Left count: " + leftIndices.size() + ", Right count: " + rightIndices.size());
    }

    private Map.Entry<Integer, String> computeThreshold() {
        double bestGain = -1.0;
        int bestCol = -1;
        String bestThreshold = null;
        double minGainThreshold = 0.005;
        double minSplitProportion = .05;

        for (int col = 1; col < 24; col++) {
            Set<String> uniqueValues = new HashSet<>();
            for (int idx : rowIndices) {
                uniqueValues.add(dataset.getValue(idx, col));
            }

            if (uniqueValues.size() <= 1 || uniqueValues.size() > 15) continue;
            List<String> thresholdCandidates = new ArrayList<>(uniqueValues);

            for (String threshold : thresholdCandidates) {
                List<Integer> left = new ArrayList<>();
                List<Integer> right = new ArrayList<>();
                for (int idx : rowIndices) {
                    String val = dataset.getValue(idx, col);
                    if (isLessThanOrEqual(val, threshold, col)) {
                        left.add(idx);
                    } else {
                        right.add(idx);
                    }
                }

                // Skip highly imbalanced splits
                double leftRatio = (double) left.size() / rowIndices.size();
                double rightRatio = (double) right.size() / rowIndices.size();
                if (leftRatio < minSplitProportion || rightRatio < minSplitProportion) {
                    continue;
                }

                double gain = informationGain(left, right);
                if (gain < minGainThreshold) continue;
                //System.out.printf("Testing col[%d] <= %s: Gain = %.4f\n", col, threshold, gain);
                if (gain > bestGain) {
                    bestGain = gain;
                    bestCol = col;
                    bestThreshold = threshold;
                }
            }
        }

        if(bestCol == -1 || bestThreshold == null){
            return null;
        }

        return Map.entry(bestCol, bestThreshold);
    }

    private double entropy(List<Integer> indices) {
        Map<String, Integer> labelCounts = new HashMap<>();
        for (int idx : indices) {
            String label = dataset.getValue(idx,24);
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
        }

        double entropy = 0.0;
        for (int count : labelCounts.values()) {
            double p = (double) count / indices.size();
            entropy -= p * Math.log(p) / Math.log(2);
        }

        return entropy;
    }

    private double informationGain(List<Integer> left, List<Integer> right) {
        double totalSize = left.size() + right.size();
        double parentEntropy = entropy(rowIndices);
        double weightedEntropy = (left.size() / totalSize) * entropy(left)
                + (right.size() / totalSize) * entropy(right);
        return parentEntropy - weightedEntropy;
    }

    private boolean isLessThanOrEqual(String val, String threshold, int colIndex) {
        try {
            if (Arrays.stream(dataset.integerIndexs).anyMatch(x -> x == colIndex)) {
                return Float.parseFloat(val) <= Float.parseFloat(threshold);
            }

            if (colIndex == 2) {
                return val.equalsIgnoreCase("female");
            }

            switch (colIndex) {
                // Ordinal categorical: Low/Medium/High
                case 5: case 13: case 16: case 20: case 21: case 22:
                    return convertOrdinal(val) <= convertOrdinal(threshold);

                // Sleep Quality: Poor < Average < Good
                case 14:
                    return convertSleep(val) <= convertSleep(threshold);

                // Dietary Habits: Unhealthy < Average < Healthy
                case 15:
                    return convertDiet(val) <= convertDiet(threshold);

                // Yes/No binary columns
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
            default -> 0; // fallback
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

    private String majorityDiagnosis(List<Integer> indices) {
        Map<String, Integer> countMap = new HashMap<>();
        for (int idx : indices) {
            String diag = dataset.getValue(idx, 24);
            countMap.put(diag, countMap.getOrDefault(diag, 0) + 1);
        }
        return countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    public void printTree(String prefix) {
        if (isTerminal) {
            System.out.println(prefix + "Diagnosis: " + diagnosisResult);
        } else {
            System.out.println(prefix + "If col[" + splitCol + "] <= " + splitThreshold);
            left.printTree(prefix + "    ");
            System.out.println(prefix + "Else:");
            right.printTree(prefix + "    ");
        }
    }
}

