package fr.ybo.ybotv.android.adapter;

import android.database.Cursor;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.util.YboTvLog;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class CeSoirViewFlowAdapterTest {

    @Before
    public void setup() {
        YboTvLog.TO_SYSOUT = true;
    }

    @Test
    public void testGetProgrammesPrimeTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        final String dateSelect = simpleDateFormat.format(calendar.getTime()) + "210000";

        testGetProgrammes(calendar, dateSelect, 0);
    }

    @Test
    public void testGetProgrammesSecondPart() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        final String dateSelect = simpleDateFormat.format(calendar.getTime()) + "230000";

        testGetProgrammes(calendar, dateSelect, 1);
    }

    @Test
    public void testGetProgrammesEndPartTwomorrow() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        Calendar calendarTwomorrow = (Calendar) calendar.clone();
        calendarTwomorrow.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        final String dateSelect = simpleDateFormat.format(calendarTwomorrow.getTime()) + "010000";

        testGetProgrammes(calendar, dateSelect, 2);
    }

    @Test
    public void testGetProgrammesEndPartToday() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        final String dateSelect = simpleDateFormat.format(calendar.getTime()) + "010000";

        testGetProgrammes(calendar, dateSelect, 2);
    }

    private void testGetProgrammes(Calendar calendar, final String dateSelect, int position) {
        YboTvApplication application = Mockito.mock(YboTvApplication.class);
        YboTvDatabase database = Mockito.mock(YboTvDatabase.class);
        Mockito.when(application.getDatabase()).thenReturn(database);

        Cursor cursor = Mockito.mock(Cursor.class);

        Mockito.when(database.executeSelectQuery(Mockito.anyString(), Mockito.argThat(new BaseMatcher<List<String>>() {
            @Override
            public boolean matches(Object item) {
                System.out.println(item);
                List<String> args = (List<String>) item;
                return args.size() == 1 && args.contains(dateSelect);
            }

            @Override
            public void describeTo(Description description) {
            }
        }))).thenReturn(cursor);

        Mockito.when(cursor.getCount()).thenReturn(1);

        Mockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(false);

        Mockito.when(cursor.getColumnIndex("channelId")).thenReturn(0);
        Mockito.when(cursor.getColumnIndex("channelDisplayName")).thenReturn(1);
        Mockito.when(cursor.getColumnIndex("channelIcon")).thenReturn(2);
        Mockito.when(cursor.getColumnIndex("programmeId")).thenReturn(3);
        Mockito.when(cursor.getColumnIndex("programmeStart")).thenReturn(4);
        Mockito.when(cursor.getColumnIndex("programmeStop")).thenReturn(5);
        Mockito.when(cursor.getColumnIndex("programmeIcon")).thenReturn(6);
        Mockito.when(cursor.getColumnIndex("programmeTitle")).thenReturn(7);
        Mockito.when(cursor.getColumnIndex("programmeDesc")).thenReturn(8);
        Mockito.when(cursor.getColumnIndex("programmeStarRating")).thenReturn(9);
        Mockito.when(cursor.getColumnIndex("programmeCsaRating")).thenReturn(10);
        Mockito.when(cursor.getColumnIndex("programmeDirectors")).thenReturn(11);
        Mockito.when(cursor.getColumnIndex("programmeActors")).thenReturn(12);
        Mockito.when(cursor.getColumnIndex("programmeWriters")).thenReturn(13);
        Mockito.when(cursor.getColumnIndex("programmePresenters")).thenReturn(14);
        Mockito.when(cursor.getColumnIndex("programmeDate")).thenReturn(15);
        Mockito.when(cursor.getColumnIndex("programmeCategories")).thenReturn(16);

        Mockito.when(cursor.getString(0)).thenReturn("channelId");
        Mockito.when(cursor.getString(1)).thenReturn("channelDisplayName");
        Mockito.when(cursor.getString(2)).thenReturn("channelIcon");
        Mockito.when(cursor.getString(3)).thenReturn("programmeId");
        Mockito.when(cursor.getString(4)).thenReturn("programmeStart");
        Mockito.when(cursor.getString(5)).thenReturn("programmeStop");
        Mockito.when(cursor.getString(6)).thenReturn("programmeIcon");
        Mockito.when(cursor.getString(7)).thenReturn("programmeTitle");
        Mockito.when(cursor.getString(8)).thenReturn("programmeDesc");
        Mockito.when(cursor.getString(9)).thenReturn("programmeStarRating");
        Mockito.when(cursor.getString(10)).thenReturn("programmeCsaRating");
        Mockito.when(cursor.getString(11)).thenReturn("programmeDirectors");
        Mockito.when(cursor.getString(12)).thenReturn("programmeActors");
        Mockito.when(cursor.getString(13)).thenReturn("programmeWriters");
        Mockito.when(cursor.getString(14)).thenReturn("programmePresenters");
        Mockito.when(cursor.getString(15)).thenReturn("programmeDate");
        Mockito.when(cursor.getString(16)).thenReturn("programmeCategories");

        CeSoirViewFlowAdapter.MyGetProgramme myGetProgramme = new CeSoirViewFlowAdapter.MyGetProgramme(position, application, calendar);
        List<ChannelWithProgramme> programmes = myGetProgramme.getProgrammes();

        assertEquals(1, programmes.size());

        ChannelWithProgramme channelWithProgramme = programmes.iterator().next();

        assertEquals("channelId", channelWithProgramme.getChannel().getId());
        assertEquals("channelDisplayName", channelWithProgramme.getChannel().getDisplayName());
        assertEquals("channelIcon", channelWithProgramme.getChannel().getIcon());
        assertEquals("programmeId", channelWithProgramme.getProgramme().getId());
        assertEquals("programmeStart", channelWithProgramme.getProgramme().getStart());
        assertEquals("programmeStop", channelWithProgramme.getProgramme().getStop());
        assertEquals("programmeIcon", channelWithProgramme.getProgramme().getIcon());
        assertEquals("programmeTitle", channelWithProgramme.getProgramme().getTitle());
        assertEquals("programmeDesc", channelWithProgramme.getProgramme().getDesc());
        assertEquals("programmeStarRating", channelWithProgramme.getProgramme().getStarRating());
        assertEquals("programmeCsaRating", channelWithProgramme.getProgramme().getCsaRating());
        assertEquals("channelId", channelWithProgramme.getProgramme().getChannel());
        assertEquals(1, channelWithProgramme.getProgramme().getDirectors().size());
        assertEquals("programmeDirectors", channelWithProgramme.getProgramme().getDirectors().iterator().next());
        assertEquals(1, channelWithProgramme.getProgramme().getActors().size());
        assertEquals("programmeActors", channelWithProgramme.getProgramme().getActors().iterator().next());
        assertEquals(1, channelWithProgramme.getProgramme().getWriters().size());
        assertEquals("programmeWriters", channelWithProgramme.getProgramme().getWriters().iterator().next());
        assertEquals(1, channelWithProgramme.getProgramme().getPresenters().size());
        assertEquals("programmePresenters", channelWithProgramme.getProgramme().getPresenters().iterator().next());
        assertEquals("programmeDate", channelWithProgramme.getProgramme().getDate());
        assertEquals(1, channelWithProgramme.getProgramme().getCategories().size());
        assertEquals("programmeCategories", channelWithProgramme.getProgramme().getCategories().iterator().next());
    }
}
