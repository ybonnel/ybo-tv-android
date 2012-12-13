package fr.ybo.ybotv.android.activity;


import fr.ybo.ybotv.android.R;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgrammeActivityTest {

    @Test
    public void testFormatterMots() {
        String toFormat = "test tutu tata";
        assertEquals("Test tutu tata", ProgrammeActivity.formatterMots(toFormat));
    }

    @Test
    public void testMapOfCsaRatings() {
        assertEquals((int)ProgrammeActivity.mapOfCsaRatings.get("-18"), R.drawable.moins18);
        assertEquals((int)ProgrammeActivity.mapOfCsaRatings.get("-16"), R.drawable.moins16);
        assertEquals((int)ProgrammeActivity.mapOfCsaRatings.get("-12"), R.drawable.moins12);
        assertEquals((int)ProgrammeActivity.mapOfCsaRatings.get("-10"), R.drawable.moins10);
    }

}
