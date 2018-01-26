package hr.math.ivsenki.kolokvij2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationView extends AppCompatActivity {

    int notificationID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        //dohvat Notification Managera
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //cancel metoda za gasenje notificationa na kraju
        nm.cancel(getIntent().getExtras().getInt("notificationID"));

        displayNotification();
    }

    protected void displayNotification()
    {
        //---PendingIntent to launch activity if the user selects
        // this notification---
        Intent i = new Intent(this, Notification2.class);

        i.putExtra("notificationID", notificationID);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i, 0);


//Notification Channel - novo od Android O

        String NOTIFICATION_CHANNEL_ID = "my_channel_02";
        CharSequence channelName = "hr.math.karga.MYNOTIF";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);

//za sve verzije
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

// za Notification Chanel

        nm.createNotificationChannel(notificationChannel);




//ovako je i u starim verzijama, jedino dodano .setChannelId (za stare verzije to brisemo)

        Notification notif = new Notification.Builder(this)
                .setTicker("Druga notifikacije")
                .setContentTitle("Druga notifikacije")
                .setContentText("Druga notifikacije")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();
        //najnovije, od API level 26.1.0., .setWhen ide po defautlu ovdje na currentTimeMillis

/*        final NotificationCompat.Builder notif = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)

                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(vibrate)
                .setSound(null)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Meeting with customer at 3pm...")
                .setContentText("this is the second row")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker("Reminder: meeting starts in 5 minutes")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false); */

// za sve verzije

        nm.notify(notificationID, notif);
    }
}
