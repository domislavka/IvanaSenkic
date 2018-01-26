package hr.math.ivsenki.kolokvij2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    int notificationID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayNotification();

        DBAdapter db = new DBAdapter(this);

        //---dodaj 5 likova
        db.open();
        long id = db.insertLik("Mickey", "Walt Disney");
        id = db.insertLik("Raito Yagami", "Takeshi Obata");
        id = db.insertLik("Gintoki Sakata", "Hideaki Sorachi");
        id = db.insertLik("Yugi Muto", "Kazuki Takahashi");
        id = db.insertLik("Goku", "Akira Toriyama");
        id = db.insertLik("Kenshin Himura", "Nobuhiro Watsuki");
        id = db.insertLik("San", "Hayao Miyazaki");
        db.close();


        //---dodaj filmove
        String[] filmovi = new String[]{
                "The Mad Doctor",
                "Death Note",
                "White Demon's Birth",
                "Yu-Gi-Oh!",
                "The Strongest Guy",
                "New Kyoto Arc",
                "Mononoke-hime"
        };


        db.open();
        Cursor c1 = db.getAllLikovi();
        int i = 0;
        if(c1.moveToFirst()){
            do{
                int idlika = Integer.parseInt(c1.getString(0));
                long id2 = db.insertFilm(filmovi[i], idlika);
                ++i;
            }while(c1.moveToNext());
        }
        db.close();

        ///---stavljamo sadržaj tablica na ekran
        TableLayout tablica = (TableLayout)findViewById(R.id.tablica);

        //--najprije ocistimo sve retke u tablici
        tablica.removeAllViews();

        String[] tekst;

        db.open();
        Cursor c5 = db.getAllLikovi();
        Cursor c2 = db.getAllFilmovi();
        tekst = new String[c5.getCount()];

        i = 0;
        if(c5.moveToFirst() && c2.moveToFirst()){
            do{
                tekst[i] = c5.getString(0) + " " + c5.getString(1) + " " + c5.getString(2)
                        + " " + c2.getString(0) + " " + c2.getString(1);
                ++i;
            }while(c5.moveToNext() && c2.moveToNext());
        }
        db.close();


        //---stavi tekst u tablicu
        for(i = 0; i < tekst.length; ++i){

            TableRow redak = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            redak.setLayoutParams(lp);

            TextView tekstContainer = new TextView(this);
            tekstContainer.setText(tekst[i]);
            redak.addView(tekstContainer);
            tablica.addView(redak, i);
        }


        //---stavi u EditText autora lika Mickeya
        db.open();
        Cursor c4 = db.getMickeyAuthor("Mickey");
        db.close();

        if(c4 != null) {
            EditText text = (EditText) findViewById(R.id.editTxt);
            text.setText(c4.getString(2));
        }

    }


    ///---FUNCKIJA ZA ISPIS SADRZAJA TABLICA U TABLEVIEW
    public void ispisiUTablicu(View view){

        DBAdapter db = new DBAdapter(view.getContext());
        TableLayout tablica = (TableLayout)findViewById(R.id.tablica);

        //--najprije ocistimo sve retke u tablici
        tablica.removeAllViews();

        String[] tekst;

        db.open();
        Cursor c1 = db.getAllLikovi();
        Cursor c2 = db.getAllFilmovi();
        tekst = new String[c1.getCount()];
        int i = 0;
        if(c1.moveToFirst() && c2.moveToFirst()){
            do{
                tekst[i] = c1.getString(0) + " " + c1.getString(1) + " " + c1.getString(2)
                        + " " + c2.getString(0) + " " + c2.getString(1);
                ++i;
            }while(c1.moveToNext() && c2.moveToNext());
        }
        db.close();

        //---stavi tekst u tablicu
        for(i = 0; i < tekst.length; ++i){

            TableRow redak = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            redak.setLayoutParams(lp);

            TextView tekstContainer = new TextView(this);
            tekstContainer.setText(tekst[i]);
            redak.addView(tekstContainer);
            tablica.addView(redak, i);
        }
    }

    public void onClickDeleteRow(View view) {
        DBAdapter db = new DBAdapter(view.getContext());
        EditText temp = (EditText)findViewById(R.id.deleteRow);
        String id = temp.getText().toString();

        db.open();
        if( db.deleteRedak(Long.valueOf(id)) ){
            Toast.makeText(this, "Delete successful.", Toast.LENGTH_LONG).show();
            ispisiUTablicu(view);
        }
        else
            Toast.makeText(this, "Delete failed.", Toast.LENGTH_LONG).show();
        db.close();

    }


    ///----PRIKAZ NOTIFIKACIJE 1
    protected void displayNotification()
    {
        //---PendingIntent to launch activity if the user selects
        // this notification---
        Intent i = new Intent(this, NotificationView.class);

        i.putExtra("notificationID", notificationID);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i, 0);

//Notification Channel - novo od Android O
            String NOTIFICATION_CHANNEL_ID = "my_channel_01";
            CharSequence channelName = "hr.math.karga.MYNOTIF";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);


//za sve verzije
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

// za Notification Chanel

        nm.createNotificationChannel(notificationChannel);



//ovako je i u starim verzijama, jedino dodano .setChannelId (za stare verzije to brisemo)

        Notification notif = new Notification.Builder(this)
                .setTicker("Reminder: meeting starts in 5 minutes")
                .setContentTitle("Meeting with customer at 3pm...")
                .setContentText("this is the second row")
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
