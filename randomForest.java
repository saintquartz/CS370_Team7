
import java.util.*;

public class randomForest {
    private treeNode[] treeArr = new treeNode[100];
    int numTrees = 100;
    int bootStrapSize = 100;
    Dataset dataContainer;
    Set<Integer> testIdx= new HashSet<>();


    /* 
    boolean predict(String[] userInput){
        // take a poll from each decision tree
        // yesCount = hasAlzheimers?
        // noCount = noAlz?
        // return the popular vote
        // if tie return hasAlz
        int yesCount = 0;
        int noCount = 0;
        boolean outcome;
        // not sure if outcome is a boolean can change later
        for(treeNode currTreeNode : this.treeArr){
            outcome = currTreeNode.predict(userInput);

            if(outcome == true){
                yesCount++;
            }
            else{
                noCount++;
            }
        }
        return yesCount >= noCount;
    }

    float getAccuracy(){
        //take test dataset indexs 
        // compare outcome with expected
        // return (outcome == expected) frequency / size

        int numTotal = 0;
        int numCorrect = 0;
        //for(int idx: this.testIdx.keys()){ --> look up syntax 
            // outcome = predict(this.dataset.getRowValues(idx));
            // if outcome == getRowValues(idx)[-1] numCorrect++
        //}
        if(numTotal == 0){ return 0; }
        return (float) numCorrect / numTotal;
    }

    */
    void splitDataSet(){
        //We will take 20% of our rows and store there index in a set
        // when boot strapping we will ensure each index we bootstrap is NOT in our test Set
        double splitRate = .2;
        int rowTotal = this.dataContainer.getRows();
        int testSize = (int)(rowTotal * splitRate);

        int randomRowIndex;
        Random randomObject = new Random();


        for(int idx=0; idx<testSize; idx++){
            randomRowIndex = randomObject.nextInt(this.dataContainer.getRows());
            while(true){
                if(!this.testIdx.contains(randomRowIndex)){
                    testIdx.add(randomRowIndex);
                    break;
                }
            }
        }
    }
    void setDataset(Dataset container){
        this.dataContainer = container;
    }
    void train(){
        

        for(int i=0; i< this.numTrees; i++){
            Integer[] bootStrapArray = this.bootStrap();
            List<Integer> bootStrapList = Arrays.asList(bootStrapArray);
            this.treeArr[i] = new treeNode(this.dataContainer,bootStrapList);

        }
    }
    Integer[] bootStrap(){
        Integer[] subSet = new Integer[this.bootStrapSize];
        Random randomObject = new Random();
        int randomRowIndex;
        Set<Integer> indexSet = new HashSet<>();


        for(int i=0; i< bootStrapSize; i++){
            while(true){
                randomRowIndex = randomObject.nextInt(this.dataContainer.getRows());

                if(!indexSet.contains(randomRowIndex) && !this.testIdx.contains(randomRowIndex)){
                    subSet[i] = randomRowIndex;
                    indexSet.add(randomRowIndex);
                    break;
                }
            }

        }
        return subSet;
    }
    
}
