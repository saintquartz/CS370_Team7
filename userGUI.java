package CS370_Team7;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class userGUI {

    // Array to hold all input strings
    String[] userInput = new String[24];
    String csvPath = "";  // CSV input

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new userGUI().createAndShowGUI());


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
            "Employment Status (Unemployed/Employed/Retired)", 
            "Marital Status (Single/Married/Widowed)", "Genetic Risk Factor (Yes/No)",
            "Social Engagement Level (Low/Medium/High)", "Income Level (Low/Medium/High)", 
            "Stress Levels (Low/Medium/High)", "Urban Living vs Rural (Urban/Rural)"
        };

        // Adjusted row count: 25 variables + CSV + Submit + Spacer
        JPanel formPanel = new JPanel(new GridLayout(variableNames.length + 3, 2, 10, 10));
        JTextField[] fields = new JTextField[variableNames.length];

        // CSV input field at top
        formPanel.add(new JLabel("CSV File Path (Optional):"));
        JTextField csvField = new JTextField();
        formPanel.add(csvField);

        // Variable input fields
        for (int i = 0; i < variableNames.length; i++) {
            formPanel.add(new JLabel(variableNames[i] + ":"));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        // Submit button
        JButton submitButton = new JButton("Submit");
        formPanel.add(new JLabel());
        formPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            for (int i = 0; i < fields.length; i++) {
                userInput[i] = fields[i].getText().trim();
            }
            csvPath = csvField.getText().trim();

            // Print inputs
            /*
            System.out.println("Collected Inputs:");
            for (int i = 0; i < userInput.length; i++) {
                System.out.println(variableNames[i] + ": " + userInput[i]);
            }
            System.out.println("CSV File Path: " + csvPath);
            */

            //run

            for (int i = 0; i < userInput.length; i++) {
                System.out.println(variableNames[i] + ": " + userInput[i]);
            }
            
            dataset dataset = new dataset();
            dataset.setDataset();
            dataset.dataImputation();
            randomForest rf = new randomForest(dataset);
            String newPredictionFromRF = rf.predict(userInput);
            System.out.println("Prediction for custom new sample (Random Forest): " + newPredictionFromRF);

            JOptionPane.showMessageDialog(frame, "Inputs and CSV path submitted!");
        });

        // Wrap in a padded panel
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        paddedPanel.add(formPanel, BorderLayout.CENTER);

        frame.getContentPane().add(new JScrollPane(paddedPanel));
        frame.setVisible(true);


        
    }

}