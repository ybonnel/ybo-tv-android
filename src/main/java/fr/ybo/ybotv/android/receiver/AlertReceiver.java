package fr.ybo.ybotv.android.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import fr.ybo.ybotv.android.activity.ProgrammeActivity;
import fr.ybo.ybotv.android.exception.YboTvException;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.YboTvLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        YboTvLog.debug("AlertReceiver>onReceive");
        Programme programme = intent.getParcelableExtra("programme");
        Channel channel = intent.getParcelableExtra("channel");
        createNotification(context, programme, channel);
    }



    private void createNotification(Context context, Programme programme, Channel channel) {
        NotificationManager mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = Integer.parseInt(programme.getStart().substring(8));


        Intent launchNotifiactionIntent = new Intent(context, ProgrammeActivity.class);
        launchNotifiactionIntent.putExtra("programme", (Parcelable)programme);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Date dateToNotif;
        try {
            dateToNotif = new SimpleDateFormat("yyyyMMddHHmmss").parse(programme.getStart());
        } catch (ParseException e) {
            throw new YboTvException(e);
        }

        Notification notification = new Notification(channel.getNotifIconResource(), programme.getTitle(), dateToNotif.getTime());
        notification.defaults |= android.app.Notification.DEFAULT_ALL;
        notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, programme.getTitle(), programme.getDesc(), pendingIntent);

        mNotification.notify(notificationId, notification);

        programme.setHasAlert(context, false);
    }
}
