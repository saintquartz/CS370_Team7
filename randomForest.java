package CS370_Team7;

import java.util.*;


public class randomForest {
    private decisionTree[] trees = new decisionTree[100];  // Array of decision trees
    private int numTrees = 100;
    private int bootStrapSize = 1000;
    private dataset dataContainer;
    private Set<Integer> testIdx = new HashSet<>();
    Random randomObject = new Random();

    
    public randomForest(dataset container) {
        this.dataContainer = container;
        splitDataSet();
        train();
    }

    public int getNumTrees() {
        return this.numTrees;
    }

    public decisionTree[] getDTree() {
        return this.trees;
    }

    public void splitDataSet() {
        // Split 20% of the data for testing
        double splitRate = 0.2;
        int rowTotal = this.dataContainer.getRows();
        int testSize = (int)(rowTotal * splitRate);

        Random randomObject = new Random();

        while (testIdx.size() < testSize) {
            int randomRowIndex = randomObject.nextInt(rowTotal);
            testIdx.add(randomRowIndex);
        }
    }

    public void train() {
        // Train the forest by generating multiple decision trees
        for (int i = 0; i < numTrees; i++) {
            Integer[] bootStrapArray = bootStrap();
            List<Integer> bootStrapList = Arrays.asList(bootStrapArray);
            //long start = System.nanoTime();
            decisionTree tree = new decisionTree(dataContainer, bootStrapList);
            
            //long end = System.nanoTime();
            //long elapsedTime = end-start;
            //System.out.println("DT make time: " + elapsedTime);
            tree.train();  // Train each decision tree
            trees[i] = tree;
        }
    }

    public Integer[] bootStrap() {
        // Bootstrap sampling to generate random training subsets
        //long start = System.nanoTime();

        Integer[] subSet = new Integer[bootStrapSize];
        Set<Integer> indexSet = new HashSet<>();
        int randomRowIndex;

        for (int i = 0; i < bootStrapSize; i++) {
            do {
                randomRowIndex = this.randomObject.nextInt(this.dataContainer.getRows());
            } while (indexSet.contains(randomRowIndex) || testIdx.contains(randomRowIndex));  // Avoid test set rows

            subSet[i] = randomRowIndex;
            indexSet.add(randomRowIndex);
        }
        //long end = System.nanoTime();
        //long duration = end - start;  // duration in nanoseconds

        return subSet;



    }

    // Poll results from each tree and return the majority vote
    public String predict(String[] userInput) {
        Map<String, Integer> votes = new HashMap<>();
        
        // Collect predictions from all trees
        for (decisionTree tree : trees) {
            String prediction = tree.predict(userInput);
            votes.put(prediction, votes.getOrDefault(prediction, 0) + 1);
        }

        // Find the most common prediction
        String mostVoted = null;
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                mostVoted = entry.getKey();
                maxVotes = entry.getValue();
            }
        }
        return mostVoted;  // Return the most common outcome
    }

    // Optional: Evaluate the accuracy of the model using the test set
    public float getAccuracy() {
        int numTotal = 0;
        int numCorrect = 0;

        for (int idx : testIdx) {
            String[] sample = dataContainer.getRow(idx);  // Assuming a method to fetch a row
            String expected = sample[sample.length - 1];  // The last column is the target variable
            String prediction = predict(sample);
            
            if (prediction.equals(expected)) {
                numCorrect++;
            }
            numTotal++;
        }

        return (numTotal == 0) ? 0 : (float) numCorrect / numTotal;
    }
}
