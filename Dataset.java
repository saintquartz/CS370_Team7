package CS370_Team7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
//Need to add functionality to upload new CSV
// Maybe we can have a temp CSV file that we can over write so we dont constantly have new CSV files


public class dataset {
    // private String csvPath = "alzheimers_prediction_dataset.csv";
    public String csvPath = "alzheimers_prediction_dataset.csv"; //add path here
    public String[][] dataset;
    public int rows = 74283;
    public int columns = 25;
    public String[] defaultValues;
    public int[] integerIndexs = {1,3,4,12};

    public String getDefaultValue(int colIdx){
        return this.defaultValues[colIdx];
    };
    public void setCSVPath(String newCSV) {
        this.csvPath = newCSV;
    }
    public String getCSVPath() {
        return this.csvPath;
    }

    public String[][] getDataset() {
        return this.dataset;
    }

    public List<Integer> getAllRowIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dataset.length; i++) {
            indices.add(i);
        }
        return indices;
    }

    public String getValue(int row, int col) {
        return dataset[row][col];
    }

    public String[] getRow(int row) {
        return dataset[row];
    }
    public int getRows(){return this.dataset.length;}

    private void setRows(int newRows){this.rows = newRows;}
    public int getCols(){return this.columns;}
    //private void setCols(int newCols){this.columns = newCols;}
    public void setDataset() {
        int rowCount = 0;
        System.out.println("SET DATASET CSV PATH: " + csvPath);
        // First pass: Count the number of lines (excluding header)
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            // Skip header
            br.readLine();

            while (br.readLine() != null) {
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update rows and initialize dataset array
        this.setRows(rowCount);
        this.dataset = new String[rows][columns];

        int lineNumber = 0;

        // Second pass: Read data into dataset
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null && lineNumber < rows) {
                String[] values = line.split(",", columns);
                for (int i = 0; i < values.length; i++) {
                    this.dataset[lineNumber][i] = values[i].trim().toLowerCase();
                    // "current" == "Current"
                    // can have cases where all data is not accurate
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void findMean(int colIdx){
        int count = 0;
        float total = 0f;                 // use float accumulator so fractions aren’t lost
        ArrayList<Integer> emptyIdx = new ArrayList<>();

        for(int currRow = 0; currRow < getRows(); currRow++){
            if(this.dataset[currRow][colIdx] != null && !this.dataset[currRow][colIdx].isEmpty()){
                total += Float.parseFloat(this.dataset[currRow][colIdx]);
                count++;
            } else {
                emptyIdx.add(currRow);
            }
        }

        // prevent divide by 0 answer SHOULD be 0 if all are null
        String replacementValue = "Null Column";
        boolean flag = false;
        String meanStr = replacementValue;

        if(count == 0){
            this.defaultValues[colIdx] = replacementValue;
            flag = true;
        }
        else{
            if(colIdx == 4){
                // col 4 = BMI → keep one decimal
                meanStr = String.format("%.1f", total / count);
            } else {
                // Every other mean idx is an int → truncate
                meanStr = String.valueOf((int)(total / count));
            }
            this.defaultValues[colIdx] = meanStr;
        }

        for(int nullIndex : emptyIdx){
            // col 4 = BMI; other columns use integer mean
            if(flag){
                this.dataset[nullIndex][colIdx] = replacementValue;
            }
            else{
                this.dataset[nullIndex][colIdx] = meanStr;
            }
        }
    }


    public void findMode(int colIdx){
        String modeString = "NULL";
        int bestCount = 0;
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        ArrayList<Integer> emptyIdx= new ArrayList<>();


        for(int currRow=0; currRow<getRows(); currRow++){
            if(this.dataset[currRow][colIdx] != null && !this.dataset[currRow][colIdx].trim().isEmpty()){
                String key = this.dataset[currRow][colIdx];
                frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
                if(bestCount < frequencyMap.get(key)){
                    bestCount = frequencyMap.get(key);
                    modeString = key;
                }
            }
            else{
                emptyIdx.add(currRow);
            }
        }
        //System.out.println(colIdx + " "+  modeString);
        this.defaultValues[colIdx] = modeString;
        for(int nullIndex : emptyIdx){
            this.dataset[nullIndex][colIdx] = modeString;
        }
    }
    public void dataImputation(){
        // integer idxs: 1,3,4,12

        // The array is all strings but we will treat columns 1,3,4,12 as integers
        this.defaultValues = new String[this.dataset[0].length];
        int currIdx = 0;
        for(int i=0; i<getCols();i++){
            if(currIdx < this.integerIndexs.length && i==this.integerIndexs[currIdx]){
                findMean(i);
                currIdx++;
            }
            else{
                findMode(i);
            }
        }
    }
    /*
        public static void main(String[] args) {
        Dataset datasetObj = new Dataset();
        datasetObj.setDataset();  // Initialize the dataset
        datasetObj.dataImputation();  // Perform data imputation
    }
    */
}
