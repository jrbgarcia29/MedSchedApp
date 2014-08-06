package com.slmc.medschedapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.slmc.R;
import com.slmc.service.AlarmService;
import com.slmc.models.MedSched;
import com.slmc.sqlite.MedSchedSQLite;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ListOfSchedMed extends Activity {

	Context context = this;

	private String currentPIN;
	private int alarmId = 0;

	private PendingIntent pendingIntent;

	public String getCurrentPIN() {
		return currentPIN;
	}

	public void setCurrentPIN(String currentPIN) {
		this.currentPIN = currentPIN;
	}

	ListView lvMedSched;

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_sched_med);

		lvMedSched = (ListView) findViewById(R.id.lvMedScheds);

		Intent intent = getIntent();
		setCurrentPIN(intent.getStringExtra("pin"));
		
		displayMedScheds();

		setAlarmForNextIntake();
	}

	// set alarm for each medicine next intakes schedules
	public void setAlarmForNextIntake() {

		MedSchedSQLite sqlite = new MedSchedSQLite(getApplicationContext());
		final List<MedSched> listMedScheds = sqlite.getAllMedScheds();
		String medName, retrievedPDTI, nextMedIntake, startDate = null, startTime = null, endDate = null, freq = null;
		String pin = getCurrentPIN();
		for (int i = 0; i < listMedScheds.size(); i++) {
			MedSched medSched = new MedSched();
			medSched = listMedScheds.get(i);
			medName = medSched.getMedName();
			startDate = medSched.getStartDate();
			startTime = medSched.getStartTime();
			endDate = medSched.getEndDate();
			freq = medSched.getFreq();

			retrievedPDTI = sqlite.getPreviousDateTimeIntake(pin, medName);

			nextMedIntake = getNextMedIntake(startDate, startTime, endDate,
					freq, retrievedPDTI);

//			for (int j = 0; j < 5; j++) {
//				setAlarm(nextMedIntake, medName, endDate, freq);
//				nextMedIntake = getNextMedIntake(startDate, startTime, endDate, freq, nextMedIntake);
//			}
			
			setAlarm(nextMedIntake, medName);

		}

	}

	public void setAlarm(String nextMedIntake, String medName) {

		if (!(nextMedIntake.equalsIgnoreCase("Medication Ended"))) {

			SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
			SimpleDateFormat sdfDateTime = new SimpleDateFormat(
					"MMM dd, yyyy hh:mm a", Locale.US);
			Date date = null;
			String currentDate = sdfDate.format(new Date());

			if (nextMedIntake.contains("Today")) {
				nextMedIntake = nextMedIntake.replace("Today", currentDate);
			}

			try {
				date = sdfDateTime.parse(nextMedIntake);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Bundle bundle = new Bundle();
			bundle.putString("pin", getCurrentPIN());
			bundle.putString("medName", medName);
			bundle.putString("nextDateTimeIntake", nextMedIntake);
			bundle.putInt("id", alarmId);
			
			Intent myIntent = new Intent(ListOfSchedMed.this,
					AlarmService.class);
			myIntent.setData(Uri.parse("alarm:" +(alarmId++)));

			myIntent.putExtras(bundle);
						
			pendingIntent = PendingIntent.getService(ListOfSchedMed.this, 0,
					myIntent, 0);

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			long currentTime = System.currentTimeMillis();
			long notifLimit = calendar.getTimeInMillis();
			
			if(currentTime > notifLimit){
				
			}else {
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
			}
			
		}
	}


	public void displayMedScheds() {
		MedSchedSQLite sqlite = new MedSchedSQLite(getApplicationContext());
		final List<MedSched> listMedScheds = sqlite.getAllMedScheds();

		try {
			setCurrentPIN(listMedScheds.get(0).getPin());
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("Exception sa pagseset ng PIN", e.toString());
		}
		

		MedSchedAdapter<MedSched> medSchedAdapter = new MedSchedAdapter<MedSched>(
				getApplicationContext(), listMedScheds);
		lvMedSched.setAdapter(medSchedAdapter);

		lvMedSched.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				showSelectedMedSched(listMedScheds.get(position));
			}
		});
	}

	public void showSelectedMedSched(final MedSched selectedMedSched) {

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.selected_med_sched);
		dialog.setTitle("Medicine Schedule Info.");

		TextView tvMedName = (TextView) dialog
				.findViewById(R.id.dialogTVMedName);
		final TextView tvNextIntake = (TextView) dialog
				.findViewById(R.id.dialogTVNextIntake);
		TextView tvLastIntake = (TextView) dialog
				.findViewById(R.id.dialogTVLastIntake);
		TextView tvStartDate = (TextView) dialog
				.findViewById(R.id.dialogTVStartDate);
		TextView tvEndDate = (TextView) dialog
				.findViewById(R.id.dialogTVEndDate);
		TextView tvFreq = (TextView) dialog.findViewById(R.id.dialogTVFreq);

		Button btnOk = (Button) dialog.findViewById(R.id.dialogBtnOK);
		Button btnHistory = (Button) dialog.findViewById(R.id.dialogBtnHistory);

		final String medName = selectedMedSched.getMedName();
		String startDate = selectedMedSched.getStartDate();
		String startTime = selectedMedSched.getStartTime();
		String endDate = selectedMedSched.getEndDate();
		String strfreq = selectedMedSched.getFreq();

		MedSchedSQLite MSSQLite = new MedSchedSQLite(context);

		String retrievedPDTI = MSSQLite.getPreviousDateTimeIntake(
				getCurrentPIN(), medName);

		tvMedName.setText(medName);
		tvNextIntake.setText(getNextMedIntake(startDate, startTime, endDate,
				strfreq, retrievedPDTI));
		tvLastIntake.setText(retrievedPDTI);
		tvStartDate.setText(startDate);
		tvEndDate.setText(endDate);
		tvFreq.setText("Every " + selectedMedSched.getFreq() + " hour(s)");

		dialog.show();

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		btnHistory.setEnabled(chechkIfTimeIsAfterSched(tvNextIntake.getText()
				.toString()));

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(context, AddIntakeHistory.class);
				intent.putExtra("pin", getCurrentPIN());
				intent.putExtra("medName", medName);
				intent.putExtra("nextDateTimeIntake", tvNextIntake.getText());
				startActivity(intent);
				ListOfSchedMed.this.finish();
				// editIntakeRecordDialog(selectedMedSched.getPin(), medName,
				// tvNextIntake.getText().toString());
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_of_sched_med, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:
			new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Logout")
					.setMessage("Are you sure you want to Logout?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog2,
										int which) {
									dialog2.dismiss();
									MedSchedSQLite MSSQLite = new MedSchedSQLite(
											context);
									MSSQLite.removeAllMedScheds();
									
									AlarmService alarmSevice = new AlarmService();
									alarmSevice.cancelAllNotif(context);
									
									ListOfSchedMed.this.finish();
									Intent intent = new Intent(
											getApplicationContext(),
											Login.class);
									startActivity(intent);

								}

							}).setNegativeButton("No", null).show();
			return true;

		case R.id.action_intake_history:
			Intent intent = new Intent(context, MedIntakeHistory.class);
			intent.putExtra("pin", getCurrentPIN());
			startActivity(intent);
			return true;

		case R.id.action_update:
			this.finish();
			Intent intentLogIn = new Intent(context, Login.class);
			intentLogIn.putExtra("pin", getCurrentPIN());
			Log.d("PIN sa pag Update", getCurrentPIN());
			startActivity(intentLogIn);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("hiding")
	class MedSchedAdapter<MedSched> extends ArrayAdapter<MedSched> {

		private final Context context;
		private final List<MedSched> medScheds;

		public MedSchedAdapter(Context context, List<MedSched> medScheds) {
			super(context, R.layout.med_sched_table, medScheds);
			this.context = context;
			this.medScheds = medScheds;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.med_sched_table, parent,
					false);
			TextView medName = (TextView) rowView.findViewById(R.id.tvMedName);
			TextView freq = (TextView) rowView.findViewById(R.id.tvFreq);
			TextView nextIntake = (TextView) rowView
					.findViewById(R.id.tvNextIntake);

			medName.setTextColor(Color.parseColor("#0000FF"));
			freq.setTextColor(Color.parseColor("#00AAFF"));
			nextIntake.setTextColor(Color.parseColor("#00AAFF"));

			MedSched currentMedSched = medScheds.get(position);

			String startDate = ((com.slmc.models.MedSched) currentMedSched)
					.getStartDate();
			String startTime = ((com.slmc.models.MedSched) currentMedSched)
					.getStartTime();
			String endDate = ((com.slmc.models.MedSched) currentMedSched)
					.getEndDate();
			String strfreq = ((com.slmc.models.MedSched) currentMedSched)
					.getFreq();

			MedSchedSQLite MSSQLite = new MedSchedSQLite(context);
			String retrievedPDTI = MSSQLite.getPreviousDateTimeIntake(
					getCurrentPIN(),
					((com.slmc.models.MedSched) currentMedSched).getMedName());

			medName.setText(((com.slmc.models.MedSched) currentMedSched)
					.getMedName());
			freq.setText(strfreq);

			String nextTimeIntake = getNextMedIntake(startDate, startTime,
					endDate, strfreq, retrievedPDTI);

			if (chechkIfTimeIsAfterSched(nextTimeIntake)) {
				nextIntake.setTextColor(Color.parseColor("#FF0000"));
			}

			nextIntake.setText(nextTimeIntake);
			return rowView;
		}

	}

	public String getStartDateTime(String startDate, String startTime) {

		String result = null;
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-ddhh:mma",
				Locale.US);
		SimpleDateFormat sdfOutput = new SimpleDateFormat(
				"MMM dd, yyyy hh:mm a", Locale.US);
		try {
			result = sdfOutput.format(sdfInput.parse(startDate + startTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getNextMedIntake(String startDate, String startTime,
			String endDate, String freq, String previousDTI) {

		String result = null;
		if (previousDTI.equalsIgnoreCase("NO INTAKE HISTORY")) {
			result = getStartDateTime(startDate, startTime);
		} else {

			SimpleDateFormat sdfDateTimeInput = new SimpleDateFormat(
					"yyyy-MM-ddhh:mma", Locale.US);
			SimpleDateFormat sdfDateTimeOutput = new SimpleDateFormat(
					"MMM dd, yyyy hh:mm a", Locale.US);
			Date previousDateTime, EndDate;
			try {
				previousDateTime = sdfDateTimeOutput.parse(previousDTI);
				Date currentdate = new Date();
				EndDate = sdfDateTimeInput.parse(endDate + "11:59PM");
				if (currentdate.after(EndDate)) {
					result = "Medication Ended";
				} else if (currentdate.after(previousDateTime)) {

					Calendar instance = GregorianCalendar.getInstance();
					instance.setTime(previousDateTime);

					Integer timeToAdd = Integer.parseInt(freq);

					instance.add(GregorianCalendar.HOUR, timeToAdd);

					result = sdfDateTimeOutput.format(instance.getTime());

				} else {
					result = sdfDateTimeOutput.format(previousDateTime);
				}
			} catch (ParseException e) {
				result = previousDTI;
			}
		}

		SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy",
				Locale.US);
		String strCurrentDate = sdfDate.format(new Date());

		if (result.contains(strCurrentDate)) {
			result = result.replace(strCurrentDate, "Today");
		}

		return result;
	}

	public Boolean chechkIfTimeIsAfterSched(String nextTimeIntake) {

		Date scheduledDateTime = null;
		Date currentDate = new Date();

		SimpleDateFormat sdfDateTimeInput = new SimpleDateFormat(
				"MMM dd, yyyy hh:mm a", Locale.US);
		SimpleDateFormat sdfDateTimeComparator = new SimpleDateFormat(
				"MMM dd, yyyy HH:mm", Locale.US);
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy",
				Locale.US);

		if (nextTimeIntake.contains("Today")) {

			String strDate = sdfDate.format(currentDate);

			try {
				Date convDate = sdfDateTimeInput.parse(nextTimeIntake.replace(
						"Today", strDate));
				scheduledDateTime = sdfDateTimeComparator
						.parse(sdfDateTimeComparator.format(convDate));

			} catch (ParseException e) {

			}

		} else if (nextTimeIntake.equalsIgnoreCase("Medication Ended")) {
			scheduledDateTime = currentDate;
		}

		else {
			try {
				scheduledDateTime = sdfDateTimeComparator.parse(nextTimeIntake);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (currentDate.after(scheduledDateTime)) {
			return true;
		} else {
			return false;
		}

	}

}
