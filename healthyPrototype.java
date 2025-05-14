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

    public String[] compareUserData(String[] userData, String[] label){
        // certain columns are not recommendable 
        // ex: age  you cannot tell someone to get yougner
        
        int[] nonStaticIndexs = {4,5,6,7,10,13,14,15,19,21};
        // IMPORTANT INDEXS:
        // 4 = BMI (User can lose/gain weight)
        // 5 = Physical Activity
        // 6 = Smoking Status
        // 7 = Alcohol Consumption
        // 10 = Cholesterol
        // 13 = Depression
        // 14 = sleep
        // 15 = Diet
        // 19 = socialEngagement
        // 21 = Stress

        HashMap<String, Integer> outputRanking = new HashMap<>();
        
        // Physical Activity / Depression / social engagement / stress levels
        outputRanking.put("Low",1);
        outputRanking.put("Medium",2);
        outputRanking.put("High",3);

        //Smoking
        outputRanking.put("Never",1);
        outputRanking.put("Former",2);
        outputRanking.put("Current",3);

        // Alcohol
        // outputRanking.put("Never",1); --> in Smoking: 
        outputRanking.put("Occasionally",2);
        outputRanking.put("Regularly",3);

        // Cholesterol
        outputRanking.put("Normal",2);
        // outputRanking.put("High",3); --> in Physical Activity

        // Sleep Quality
        outputRanking.put("Poor",1);
        outputRanking.put("Average",2);
        outputRanking.put("Good",3);

        // Dietary Habits 
        outputRanking.put("Unhealthy",1);
        // outputRanking.put("Average",2); --> in Sleep Quality 
        outputRanking.put("Healthy",3);


        // What if the user NEVER smokes but the AVERAGE Healthy person does?
        // do we recommend them to smoke? NO
        // if the user has LOWER values in this case we do not recommend them to increase their smoking

        // For example: true = higher is better, false = lower is better
        HashMap<Integer, Boolean> higherIsBetter = new HashMap<>();

        higherIsBetter.put(4, false);  // BMI: lower is better
        higherIsBetter.put(5, true);   // Physical Activity: higher is better
        higherIsBetter.put(6, false);  // Smoking: lower is better
        higherIsBetter.put(7, false);  // Alcohol: lower is better
        higherIsBetter.put(10, false); // Cholesterol: lower is better
        higherIsBetter.put(13, false); // Depression: lower is better
        higherIsBetter.put(14, true);  // Sleep: higher is better
        higherIsBetter.put(15, true);  // Diet: higher is better
        higherIsBetter.put(19, true);  // Social Engagement: higher is better
        higherIsBetter.put(21, false); // Stress: lower is better


        String[] recommendationArray = new String[10];

        for (int i = 0; i < nonStaticIndexs.length; i++) {
            int index = nonStaticIndexs[i];

            String userVal = userData[index];
            String protoVal = this.prototypeRow[index];

            Integer userRank = outputRanking.get(userVal);
            Integer protoRank = outputRanking.get(protoVal);
            Boolean betterIfHigher = higherIsBetter.get(index);

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

            if (userRank != null && protoRank != null && betterIfHigher != null) {
                if ((betterIfHigher && userRank < protoRank) || (!betterIfHigher && userRank > protoRank)) {
                    recommendationArray[i] = "Try to improve your " + label[index] + ".";
                } else if ((betterIfHigher && userRank > protoRank) || (!betterIfHigher && userRank < protoRank)) {
                    recommendationArray[i] = "Your " + label[index] + " is higher than recommended. Consider reducing.";
                } else {
                    recommendationArray[i] = "Your " + label[index] + " is on track!";
                }
            } else {
                recommendationArray[i] = "No recommendation for " + label[index] + ".";
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

            if (value != null && !value.equals("") && !decision.equals("No")) {
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

            if (value != null && !value.equals("") && !decision.equals("No")) {
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
