package fr.ybo.ybotv.android.util;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import fr.ybo.ybotv.android.YboTvApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CalendarUtil {

    @SuppressLint("NewApi")
    public static void addToCalendar(Context ctx, String title, String content, int calendarId,
                               long startTime, long endTime) {

        final ContentResolver cr = ctx.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put("calendar_id", calendarId);
        cv.put("title", title);
        cv.put("description", content);
        cv.put("dtstart", startTime);
        cv.put("eventTimezone", TimeZone.getDefault().getID());
        cv.put("dtend", endTime);
        cv.put("hasAlarm", 1);

        Uri newEvent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
        } else {
            newEvent = cr.insert(Uri.parse("content://calendar/events"), cv);
        }

        if (newEvent != null) {
            long id = Long.parseLong(newEvent.getLastPathSegment());
            ContentValues values = new ContentValues();
            values.put("event_id", id);
            values.put("method", 1);
            values.put("minutes", 2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                cr.insert(Uri.parse("content://com.android.calendar/reminders"), values);
            } else {
                cr.insert(Uri.parse("content://calendar/reminders"), values);
            }

        } else {
            Log.e(YboTvApplication.TAG, "Erreur à l'insertion");
        }

    }

    /**
     * Retourner la liste des calendriers
     *
     * @return Liste des calendriers
     */
    @SuppressLint("NewApi")
    public static Map<Integer, String> getCalendars(ContentResolver contentResolver) {
        Map<Integer, String> calendars = new HashMap<Integer, String>();
        String[] projection;
        Uri calendarUri;
        Cursor cursor;
        String accessLevelCol;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            calendarUri = CalendarContract.Calendars.CONTENT_URI;
            projection = new String[] { CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };
            accessLevelCol = CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            calendarUri = Uri.parse("content://com.android.calendar/calendars");
            projection = new String[] { "_id", "displayname" };
            accessLevelCol = "ACCESS_LEVEL";
        } else {
            calendarUri = Uri.parse("content://calendar/calendars");
            projection = new String[] { "_id", "displayname" };
            accessLevelCol = "ACCESS_LEVEL";
        }

        cursor = contentResolver.query(calendarUri, projection, accessLevelCol + "=700", null, null);

        if (cursor != null){
            Log.d(YboTvApplication.TAG, "Nombre de calendrier : " + cursor.getCount());

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    calendars.put(cursor.getInt(0), cursor.getString(1));
                    Log.d(YboTvApplication.TAG, "Calendrier trouvé : " + cursor.getString(1));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }

        return calendars;
    }
}
