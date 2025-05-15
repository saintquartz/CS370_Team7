package CS370_Team7;

import java.util.HashMap;

/**
 * The healthyPrototype class analyzes user health data against a "healthy prototype"
 * generated from a dataset. It generates personalized recommendations based on deviations
 * from healthy averages or modes.
 */
public class healthyPrototype {
    String[] prototypeRow;
    
    
    /**
     * Compares user health data with the healthy prototype and returns personalized recommendations.
     *
     * @param userData The user's health data as a String array.
     * @param label The array of corresponding labels (column names) for each health parameter.
     * @return An array of recommendations (one for each non-static index).
     */

    public String[] compareUserData(String[] userData) {
    int[] nonStaticIndexs = {4, 5, 6, 7, 10, 13, 14, 15, 20, 22};
    String[] label = {
        "Country", "Age", "Gender", "Education Level", "BMI", "Physical Activity Level", "Smoking Status", "Alcohol Consumption",
        "Diabetes", "Hypertension", "Cholesterol Level", "Family History of Alzheimer’s", "Cognitive Test Score", "Depression Level",
        "Sleep Quality", "Dietary Habits", "Air Pollution Exposure", "Employment Status", "Marital Status", "Genetic Risk Factor (APOE-ε4 allele)",
        "Social Engagement Level", "Income Level", "Stress Levels", "Urban vs Rural Living", "Alzheimer’s Diagnosis"
    };

    HashMap<String, Integer> outputRanking = new HashMap<>();
    // Lowercase values to match .toLowerCase() calls
    outputRanking.put("low", 1);
    outputRanking.put("medium", 2);
    outputRanking.put("high", 3);

    outputRanking.put("never", 1);
    outputRanking.put("former", 2);
    outputRanking.put("current", 3);

    outputRanking.put("occasionally", 2);
    outputRanking.put("regularly", 3);

    outputRanking.put("normal", 2);

    outputRanking.put("poor", 1);
    outputRanking.put("average", 2);
    outputRanking.put("good", 3);

    outputRanking.put("unhealthy", 1);
    outputRanking.put("healthy", 3);

    HashMap<Integer, Boolean> higherIsBetter = new HashMap<>();
    higherIsBetter.put(4, false);  // BMI
    higherIsBetter.put(5, true);   // Physical Activity
    higherIsBetter.put(6, false);  // Smoking
    higherIsBetter.put(7, false);  // Alcohol
    higherIsBetter.put(10, false); // Cholesterol
    higherIsBetter.put(13, false); // Depression
    higherIsBetter.put(14, true);  // Sleep
    higherIsBetter.put(15, true);  // Diet
    higherIsBetter.put(20, true);  // Social Engagement 
    higherIsBetter.put(22, false); // Stress 

    String[] recommendationArray = new String[10];

    for (int i = 0; i < nonStaticIndexs.length; i++) {
        int index = nonStaticIndexs[i];
        String userVal = userData[index].toLowerCase();
        String protoVal = this.prototypeRow[index].toLowerCase();

        // Debug info
        //System.out.println("Comparing " + label[index] + ": user=" + userVal + ", proto=" + protoVal);

        if (index == 4) {  // BMI
            float userBMI = Float.parseFloat(userVal);
            float protoBMI = Float.parseFloat(protoVal);

            if (userBMI < protoBMI - 1.0) {
                recommendationArray[i] = "Your BMI is lower than average. Consider consulting with a doctor if underweight.";
            } else if (userBMI > protoBMI + 1.0) {
                recommendationArray[i] = "Your BMI is higher than average. Consider healthy weight management.";
            } else {
                recommendationArray[i] = "Your BMI is within a healthy range.";
            }

            continue;
        }

        Integer userRank = outputRanking.get(userVal);
        Integer protoRank = outputRanking.get(protoVal);
        Boolean betterIfHigher = higherIsBetter.get(index);

        // debug
        //System.out.println("userRank=" + userRank + ", protoRank=" + protoRank + ", betterIfHigher=" + betterIfHigher);

        if ((betterIfHigher && userRank < protoRank) || (!betterIfHigher && userRank > protoRank)) {
            recommendationArray[i] = "Try to improve your " + label[index] + ".";
        } else if ((betterIfHigher && userRank > protoRank) || (!betterIfHigher && userRank < protoRank)) {
            if (betterIfHigher) {
                recommendationArray[i] = "Great job! Your " + label[index] + " is better than average.";
            } else {
                recommendationArray[i] = "Your " + label[index] + " is higher than recommended. Consider reducing.";
            }
        } else {
            recommendationArray[i] = "Your " + label[index] + " is on track!";
        }

    }

    return recommendationArray;
}

    

    /**
     * Initializes the healthy prototype row using dataset statistics.
     * Numeric fields are averaged (mean), and categorical fields use the most frequent (mode).
     *
     * @param dataset The 2D array representing health data.
     * @param intIndexs Array of column indices representing numeric fields.
     */

    public void setPrototype(String[][] dataset, int[] intIndexs){
        // It will set the HEALTHY MEAN data for NUMERIC data
        // SET MODE data for CATEGORICAL data
        this.prototypeRow = new String[dataset[0].length];
        int currIntIdx = 0;
        String outcome;
        for(int colIdx = 0; colIdx < dataset[0].length; colIdx++ ){
            if(currIntIdx< intIndexs.length && intIndexs[currIntIdx] == colIdx){
                currIntIdx++;
                outcome = findHealthyMean(colIdx, dataset);
            }
            else{
                outcome = findHealthyMode(colIdx, dataset);
            }
            prototypeRow[colIdx] = outcome;
        }
    }

    /**
     * Calculates the mean of a numeric column for users labeled "Yes" (healthy).
     *
     * @param colIdx The index of the numeric column.
     * @param dataset The health dataset.
     * @return The mean as a string rounded to 2 decimal places.
     */
    public String findHealthyMean(int colIdx, String[][] dataset){
        // takes mean of column and returns it
        int count = 0;
        int total = 0;

        for(int currRow = 0; currRow<dataset.length;currRow++){
            String value = dataset[currRow][colIdx];
            String decision = dataset[currRow][dataset[currRow].length - 1];

            if (value != null && !value.equals("") && !decision.equals("no")) {
                total += Float.parseFloat(dataset[currRow][colIdx]);
                count++;
            }
        }
        if(count == 0){
            return "EMPTY";
        }

        float mean = (float) total / count;

        return String.format("%.2f", mean);
    }

    /**
     * Calculates the mode (most common value) of a categorical column for healthy users.
     *
     * @param colIdx The index of the categorical column.
     * @param dataset The health dataset.
     * @return The mode string.
     */
    public String findHealthyMode(int colIdx, String[][] dataset){
        // takes most frequent occurence in column and returns it
        String modeString = "EMPTY";
        int bestCount = 0;
        HashMap<String, Integer> frequencyMap = new HashMap<>();


        for(int currRow=0; currRow<dataset.length; currRow++){
            String value = dataset[currRow][colIdx];
            String decision = dataset[currRow][dataset[currRow].length - 1];

            if (value != null && !value.equals("") && !decision.equals("no")) {
                String key = dataset[currRow][colIdx];
                frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
                if(bestCount < frequencyMap.get(key)){
                    bestCount = frequencyMap.get(key);
                    modeString = key;
                }
            }
        }
        //System.out.println(colIdx + " "+  modeString);

        return modeString;
    }   
    
}
