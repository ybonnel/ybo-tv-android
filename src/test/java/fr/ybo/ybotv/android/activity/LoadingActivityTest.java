package fr.ybo.ybotv.android.activity;

import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.util.TimeUnit;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoadingActivityTest {

    @Test
    public void testMustUpdate() {
        assertTrue(LoadingActivity.mustUpdate(null));
        LastUpdate lastUpdate = new LastUpdate();
        lastUpdate.setLastUpdate(new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(2)));
        assertFalse(LoadingActivity.mustUpdate(lastUpdate));
        lastUpdate.setLastUpdate(new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(6)));
        assertTrue(LoadingActivity.mustUpdate(lastUpdate));
    }

}
