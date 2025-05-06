import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap; 

//Need to add functionality to upload new CSV
// Maybe we can have a temp CSV file that we can over write so we dont constantly have new CSV files 


public class Dataset {
    private String csvPath = "alzheimers_prediction_dataset.csv";
    private String[][] dataset;
    private int rows = 74283;
    private int columns = 25;
    
    public int[] integerIndexs = {1,3,4,12};

    public void setCSVPath(String newCSV) {
        this.csvPath = newCSV;
    }
    private int getRows(){return this.rows;}

    //private void setRows(int newRows){this.rows = newRows;} --> when we have upload CSV
    private int getCols(){return this.columns;}
    //private void setCols(int newCols){this.columns = newCols;}


    public void setDataset() {
        // In order to have a 2D array we are going to turn everything into strings to process
        dataset = new String[rows][columns]; 
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null && lineNumber < rows) {
                
                String[] values = line.split(",", columns);

                for (int i = 0; i < values.length; i++) {
                    dataset[lineNumber][i] = values[i];
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void findMean(int colIdx){
        int count = 0;
        int total = 0;
        ArrayList<Integer> emptyIdx= new ArrayList<>();

        for(int currRow = 0; currRow<getRows();currRow++){
            if(this.dataset[currRow][colIdx] != null && this.dataset[currRow][colIdx]!= ""){
                total += Float.parseFloat(this.dataset[currRow][colIdx]);
                count++;
            }
            else{
                emptyIdx.add(currRow);
            }
        }
        //prevent divide by 0 answer SHOULD be 0 if all are null
        String replacementValue = "Null Column";
        boolean flag = false;
        if(count == 0){
            flag = true;
        }
        for(int nullIndex : emptyIdx){
            //col 12 = Float
            // Every other mean idx is an int
            if(flag == true){
                this.dataset[nullIndex][colIdx] = replacementValue;
            }
            else{
            this.dataset[nullIndex][colIdx] = String.valueOf(colIdx == 12 ? (float) total / count : total / count);
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

        for(int nullIndex : emptyIdx){
            this.dataset[nullIndex][colIdx] = modeString;
        }
    }
    public void dataImputation(){
        // integer idxs: 1,3,4,12

        // The array is all strings but we will treat columns 1,3,4,12 as integers
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
