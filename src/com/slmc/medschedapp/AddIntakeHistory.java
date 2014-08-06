package com.slmc.medschedapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.slmc.R;
import com.slmc.models.MedIntakeRecord;
import com.slmc.sqlite.MedSchedSQLite;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddIntakeHistory extends Activity {
	Context context = this;
	TextView tvMedName, tvDate, tvTime;
	Button btnSave, btnEdit;
	
	private String pin;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_intake_history);
		
		
		tvMedName = (TextView)findViewById(R.id.tvMIRMedName);
		tvDate = (TextView)findViewById(R.id.tvMIRDate);
		tvTime = (TextView)findViewById(R.id.tvMIRTime);
		
		btnSave = (Button)findViewById(R.id.btnMIRSave);
		btnEdit = (Button)findViewById(R.id.btnMIREdit);
		
		final String medName;
		String dateIntake = null, timeIntake = null;
		final String dateTimeIntake;
		final String pin;
		Intent intent = getIntent();
		pin = intent.getStringExtra("pin");
		medName = intent.getStringExtra("medName");
		dateTimeIntake = intent.getStringExtra("nextDateTimeIntake");
		
		SimpleDateFormat sdfDateTime = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
		SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);
		
		Date currentDate = new Date();

		
		dateIntake = sdfDate.format(currentDate);
		timeIntake = sdfTime.format(currentDate);

		final String actualDateTime = sdfDateTime.format(currentDate);
		setPin(pin);
		
		tvMedName.setText(medName);
		tvDate.setText(dateIntake);
		tvTime.setText(timeIntake);
		
		btnEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editIntakeRecordDialog(pin, medName, dateTimeIntake);
			}
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final MedIntakeRecord mir = new MedIntakeRecord(pin, medName,
						 actualDateTime);
				addToIntakeHistory(mir);
				Intent intent = new Intent(context, MedIntakeHistory.class);
				intent.putExtra("pin", getPin());
				startActivity(intent);
				AddIntakeHistory.this.finish();
			}
		});
		
	}
	
	public void editIntakeRecordDialog(final String pin, final String medName,
			String dateTimeTaken) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.add_to_history);
		dialog.setTitle("Medicine Intake Record");

		TextView tvMedName = (TextView) dialog.findViewById(R.id.tvATHMedName);
		final EditText etDate = (EditText) dialog.findViewById(R.id.etATHDate);
		final TimePicker tpTimeIntake = (TimePicker) dialog
				.findViewById(R.id.tpTimeTaken);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelATH);
		Button btnSave = (Button) dialog.findViewById(R.id.btnAddTH);

		SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy",
				Locale.US);
		final SimpleDateFormat sdfDateTimeInput = new SimpleDateFormat(
				"MMM dd, yyyy hh:mm a", Locale.US);
		final SimpleDateFormat sdfDateTime = new SimpleDateFormat(
				"MMM dd, yyyy HH:mm", Locale.US);

		final String currDate = sdfDate.format(new Date());

		if (dateTimeTaken.contains("Today")) {
			dateTimeTaken = dateTimeTaken.replace("Today", currDate);
		}

		String dateIntake = null;
		try {
			dateIntake = sdfDate.format(sdfDateTimeInput.parse(dateTimeTaken));
		} catch (ParseException e2) {
		}

		final String dateOfIntake = dateIntake;
		final String[] timeData = dateTimeTaken.substring(13)
				.replaceAll("[A,M,P]", "").trim().split(":");

		Date time = null;
		try {
			time = sdfTime.parse(dateTimeTaken.substring(13));
		} catch (ParseException e1) {

		}
		@SuppressWarnings("deprecation")
		final String strtime = String.valueOf(time.getHours());

		tvMedName.setText(medName);
		etDate.setText(dateOfIntake);
		tpTimeIntake.setCurrentHour(Integer.parseInt(strtime));
		tpTimeIntake.setCurrentMinute(Integer.parseInt(timeData[1]));

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				String strTime, 
				String strActualTime;

//				strTime = strtime + ":" + timeData[1];
				strActualTime = tpTimeIntake.getCurrentHour().toString() + ":"
						+ tpTimeIntake.getCurrentMinute().toString();

//				String dateTime = null;
				String actualDateTime = null;
				try {
//					dateTime = sdfDateTimeInput.format(sdfDateTime
//							.parse(dateOfIntake + " " + strTime));
					actualDateTime = sdfDateTimeInput.format(sdfDateTime
							.parse(etDate.getText().toString() + " "
									+ strActualTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				final MedIntakeRecord mir = new MedIntakeRecord(pin, medName,
						 actualDateTime);
				addToIntakeHistory(mir);
				dialog.dismiss();
				Intent intent = new Intent(context, MedIntakeHistory.class);
				intent.putExtra("pin", getPin());
				startActivity(intent);
				AddIntakeHistory.this.finish();
			}
		});

		dialog.show();
	}

	public void addToIntakeHistory(MedIntakeRecord MIR) {
		MedSchedSQLite MIHSQLite = new MedSchedSQLite(context);
		MIHSQLite.addMedIntakeRecord(MIR);
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}



}
