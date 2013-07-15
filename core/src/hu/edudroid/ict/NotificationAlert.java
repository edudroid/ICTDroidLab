package hu.edudroid.ict;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class NotificationAlert extends Activity
{
        
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        try{
            super.onCreate(savedInstanceState);
            String KEY;
            String MESSAGE;

            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    MESSAGE= null;
                    KEY=null;
                } else {
                    MESSAGE= extras.getString("Message");
                    KEY=extras.getString("Key");
                }
            } else {
                MESSAGE= (String) savedInstanceState.getSerializable("Message");
                KEY= (String) savedInstanceState.getSerializable("Key");
            }

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            int icon = R.drawable.ic_launcher;
            CharSequence tickerText = "ICT DroidLab figyelmeztető";
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon, tickerText, when);

            Context context2 = getApplicationContext();
            CharSequence contentTitle = "ICT DroidLab";
            //CharSequence contentText = "Hello World!";
            CharSequence contentText = MESSAGE;

            Intent notificationIntent=null;

            
            notificationIntent = new Intent(this, hu.edudroid.ict.ui.MainActivity.class);

            //notificationIntent.FLAG_ACTIVITY_CLEAR_TASK.
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context2, contentTitle, contentText, contentIntent);
            notification.vibrate = new long[] { 100, 250, 100, 500};
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            //notification.flags = Notification.FLAG_ONGOING_EVENT;
            mNotificationManager.notify(Integer.parseInt(KEY), notification);




            Log.i("getextra**********",MESSAGE);

            finish();

            
        }
        catch(Exception e){
            Log.e("Hiba (NotificationAler:onCreate)",e.toString());
            Toast.makeText(getApplicationContext(), "A futás során hiba történt!", Toast.LENGTH_SHORT).show();
            finish();
        }
		
    }

}
