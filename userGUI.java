package CS370_Team7;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class userGUI {

    String[] userInput = new String[24];

    String csvPath = "alzheimers_prediction_dataset.csv";
    String currCSVPath = "alzheimers_prediction_dataset.csv";
    dataset dataset = new dataset();
    randomForest rf;
    healthyPrototype hp;
    boolean pipelineCalledOnce = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new userGUI().createAndShowGUI());
    }
    void fillMissingUserData(){
        for(int i=0; i<this.userInput.length;i++){
            if(userInput[i].isEmpty()){
                userInput[i] = this.dataset.getDefaultValue(i);
            }
        }
    }
    private void pipelineData() throws IOException {
        long pipelineStart = System.nanoTime();
        System.out.println("START PIPELINE");
        System.out.println(dataset.getCSVPath());
        dataset.setDataset();
        System.out.println("Done: setDataset");

        dataset.dataImputation();
        System.out.println("Done: dataImputation");

        rf = new randomForest(dataset);
        System.out.println("Done: Random Forest - Accuracy: " + rf.getAccuracy());

        hp = new healthyPrototype();
        hp.setPrototype(dataset.dataset, dataset.integerIndexs);
        System.out.println("Done: HP Setup");

        long pipelineEnd = System.nanoTime();
        System.out.println("END PIPELINE: Elapsed = " + (pipelineEnd - pipelineStart) / 1_000_000 + " ms");
    }

    private void runPipelineInBackground(JFrame frame, String path, Runnable onPipelineDone) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                currCSVPath = path;
                dataset = new dataset();
                dataset.setCSVPath(path);
                pipelineData();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Throws if there was an exception
                    if (onPipelineDone != null) {
                        onPipelineDone.run();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to load CSV file from:\n" + path + "\n" + ex.getMessage(),
                            "CSV Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }


    private void createAndShowGUI() {
        JFrame frame = new JFrame("User Input Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 900);

        String[] variableNames = {
                "Country", "Age", "Gender (Male/Female)", "EducationLevel (0-19)", "BMI (18.5 - 35)",
                "Physical Activity Level (Low/Medium/High)", "Smoking Status (Never/Former/Current)",
                "Alcohol Consumption (Never/Occasionally/Regularly)", "Diabetes (Yes/No)", "Hypertension (Yes/No)",
                "Cholesterol Level (Low/Normal/High)", "Family History of Alzheimer's (Yes/No)", "Cognitive Test Score (30 - 99)",
                "Depression Level (Low/Medium/High)", "Sleep Quality (Poor/Good/Average)",
                "Dietary Habits (Unhealthy/Average/Healthy)", "Daily Air Pollution Exposure (Low/Medium/High)",
                "Employment Status (Unemployed/Employed/Retired)", "Marital Status (Single/Married/Widowed)",
                "Genetic Risk Factor (Yes/No)", "Social Engagement Level (Low/Medium/High)",
                "Income Level (Low/Medium/High)", "Stress Levels (Low/Medium/High)", "Urban Living vs Rural (Urban/Rural)"
        };

        JPanel formPanel = new JPanel(new GridLayout(variableNames.length + 3, 2, 10, 10));
        JTextField[] fields = new JTextField[variableNames.length];

        formPanel.add(new JLabel("CSV File Path (Optional):"));
        JTextField csvField = new JTextField();
        formPanel.add(csvField);

        for (int i = 0; i < variableNames.length; i++) {
            formPanel.add(new JLabel(variableNames[i] + ":"));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        JButton submitButton = new JButton("Submit");
        formPanel.add(new JLabel());
        formPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            for (int i = 0; i < fields.length; i++) {
                userInput[i] = fields[i].getText().trim();    
            }
            String inputCSVPath = csvField.getText().trim();

            for (int i = 0; i < userInput.length; i++) {
                System.out.println(variableNames[i] + ": " + userInput[i]);
            }
            System.out.println("CSV File Path: " + inputCSVPath);

            if (!currCSVPath.equals(inputCSVPath) && inputCSVPath != null && !inputCSVPath.isEmpty()) {
                // Run pipeline and only after done, run prediction and display output
                runPipelineInBackground(frame, inputCSVPath, () -> {
                    fillMissingUserData();
                    String newPredictionFromRF = rf.predict(userInput);
                    System.out.println("Prediction for custom new sample (Random Forest): " + newPredictionFromRF);

                    System.out.println("Comparison with Healthy Prototype:");
                    String[] output = hp.compareUserData(userInput);
                    for (String recommendation : output) {
                        System.out.println(recommendation);
                    }
                    

                    JOptionPane.showMessageDialog(frame, "New CSV loaded, model updated, inputs processed!");
                });
            } else {
                fillMissingUserData();
                // Pipeline not updated, just use current model
                String newPredictionFromRF = rf.predict(userInput);
                System.out.println("Prediction for custom new sample (Random Forest): " + newPredictionFromRF);

                System.out.println("Comparison with Healthy Prototype:");
                String[] output = hp.compareUserData(userInput);
                for (String recommendation : output) {
                    System.out.println(recommendation);
                }

                
                

                JOptionPane.showMessageDialog(frame, "Inputs submitted with existing model!");
            }
        });


        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        paddedPanel.add(formPanel, BorderLayout.CENTER);

        frame.getContentPane().add(new JScrollPane(paddedPanel));
        frame.setVisible(true);

        if (!pipelineCalledOnce) {
            pipelineCalledOnce = true;
            runPipelineInBackground(frame, currCSVPath, null);
        }
    }
}
