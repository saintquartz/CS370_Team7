package CS370_Team7;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class healthyPrototypeTest {
    

    @Test
    public void testHpData() {
        healthyPrototype hp = new healthyPrototype();
        dataset ds = new dataset();
        dataset.setCSVPath("CS_370_group_7_toy_dataset.csv");
        dataset.imputation();

        hp.setPrototype(ds.dataset, ds.integerIndexs);
        
        String[] values = {
            "united states", "85", "male", "13", "33.9", "low",
            "former", "regularly", "yes", "yes", "high", "yes",
            "40", "high", "poor", "average", "high", "unemployed",
            "married", "yes", "low", "low", "high", "urban", "yes"
        };

        assertArrayEquals(values, hp.getPrototype());
    }

}
