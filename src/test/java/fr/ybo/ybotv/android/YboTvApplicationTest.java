package fr.ybo.ybotv.android;

import fr.ybo.ybotv.android.activity.CeSoirActivity;
import fr.ybo.ybotv.android.activity.NowActivity;
import fr.ybo.ybotv.android.activity.ParChaineActivity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class YboTvApplicationTest {

    @Test
    public void testEnumScreen() {
        assertEquals(YboTvApplication.SCREEN.NOW.getActivity(), NowActivity.class);
        assertEquals(YboTvApplication.SCREEN.CE_SOIR.getActivity(), CeSoirActivity.class);
        assertEquals(YboTvApplication.SCREEN.PAR_CHAINE.getActivity(), ParChaineActivity.class);
        assertEquals(YboTvApplication.SCREEN.formString("NOW"), YboTvApplication.SCREEN.NOW);
        assertEquals(YboTvApplication.SCREEN.formString("CE_SOIR"), YboTvApplication.SCREEN.CE_SOIR);
        assertEquals(YboTvApplication.SCREEN.formString("PAR_CHAINE"), YboTvApplication.SCREEN.PAR_CHAINE);
        assertNull(YboTvApplication.SCREEN.formString("AUTRE"));
    }

}
