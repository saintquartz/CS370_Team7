package CS370_Team7;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
public class datasetTest {

   

    @Test
    public void testDataImputation() {
        dataset ds = new dataset();
        ds.setCSVPath("../CS_370_group_7_toy_dataset.csv");
        ds.setDataset();
        ds.dataImputation();

        String[] expected = {
            "united states", "65", "male", "15", "26.7",
            "low", "never", "never", "no", "no", "normal", "yes", "67",
            "low", "good", "average", "low", "employed", "married", "no",
            "medium", "high", "low", "rural", "no"
        };

        assertArrayEquals(expected, ds.defaultValues,
            "Default values don't match after imputation.");
    }

    
}
