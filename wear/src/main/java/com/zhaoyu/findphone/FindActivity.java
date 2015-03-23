package com.zhaoyu.findphone;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

public class FindActivity extends Activity {

    private static final int FIND_PHONE_NOTIFICATION_ID = 2;
    private static Notification.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent toggleAlarmOperation = new Intent(this, FindPhoneService.class);
        toggleAlarmOperation.setAction(FindPhoneService.ACTION_TOGGLE_ALARM);
        PendingIntent toggleAlarmIntent = PendingIntent.getService(this, 0, toggleAlarmOperation,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Action alarmAction = new Notification.Action(R.drawable.ic_launcher, "", toggleAlarmIntent);

        Intent cancelAlarmOperation = new Intent(this, FindPhoneService.class);
        cancelAlarmOperation.setAction(FindPhoneService.ACTION_CANCEL_ALARM);
        PendingIntent cancelAlarmIntent = PendingIntent.getService(this, 0, cancelAlarmOperation,
                PendingIntent.FLAG_CANCEL_CURRENT);

        SpannableString title = new SpannableString(getString(R.string.app_name));
        title.setSpan(new RelativeSizeSpan(0.85f), 0, title.length(), Spannable.SPAN_POINT_MARK);
        notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(getString(R.string.turn_alarm_on))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVibrate(new long[] {0, 50})
                .setDeleteIntent(cancelAlarmIntent)
                .extend(new Notification.WearableExtender()
                        .addAction(alarmAction)
                        .setContentAction(0)
                        .setHintHideIcon(true))
                .setLocalOnly(true)
                .setPriority(Notification.PRIORITY_MAX);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(FIND_PHONE_NOTIFICATION_ID, notification.build());

        finish();
    }

    public static void updateNotification(Context context, String notificationText) {
        notification.setContentText(notificationText);
        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE))
                .notify(FIND_PHONE_NOTIFICATION_ID, notification.build());
    }

}
