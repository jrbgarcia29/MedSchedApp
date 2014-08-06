package com.slmc.service;

import com.slmc.R;
import com.slmc.medschedapp.AddIntakeHistory;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service {

	private String pin, medName, nextDateTimeIntake;
	int id;
	
	@Override
	public void onCreate() {

	}

	@Override
	public IBinder onBind(Intent intent) {

		Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG)
				.show();
		return null;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG)
				.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		
		Bundle bundle = intent.getExtras();
		pin = bundle.getString("pin");
		medName = bundle.getString("medName");
		nextDateTimeIntake = bundle.getString("nextDateTimeIntake");
		id = bundle.getInt("id",0);


		showNotification();

	}

	@Override
	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

	public void showNotification() {

		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM);

		Intent intent = new Intent(AlarmService.this, AddIntakeHistory.class);
		intent.setData(Uri.parse("notification:"+id));
		
		intent.putExtra("pin", pin);
		intent.putExtra("medName", medName);
		intent.putExtra("nextDateTimeIntake", nextDateTimeIntake);

		
		PendingIntent pIntent = PendingIntent.getActivity(AlarmService.this, id,
				intent, 0);


		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

		.setContentTitle("Time to take your Medicine!")
				.setContentText("Take " + medName + " now!")
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.setSound(soundUri, RingtoneManager.TYPE_ALARM);
		
		Notification notification=notificationBuilder.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notification.flags |= Notification.FLAG_AUTO_CANCEL
//				| Notification.DEFAULT_LIGHTS 
				| Notification.FLAG_ONLY_ALERT_ONCE;

		notificationManager.notify(id, notification);
	}
	
	public void cancelAllNotif(Context context){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

	    Intent updateServiceIntent = new Intent(context, AddIntakeHistory.class);
	    PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateServiceIntent, 0);

	    // Cancel alarms
	    try {
	        alarmManager.cancel(pendingUpdateIntent);
	    } catch (Exception e) {
	        Log.e("Exception sa pagcancel ng alarm ", "AlarmManager update was not canceled. " + e.toString());
	    }
		
	}

}