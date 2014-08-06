package com.slmc.medschedapp;

import java.util.ArrayList;
import java.util.List;

import com.slmc.R;
import com.slmc.models.MedIntakeRecord;
import com.slmc.service.WebServiceUploadMedIntakeHistory;
import com.slmc.sqlite.MedSchedSQLite;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MedIntakeHistory extends Activity {

	private Context context = this;

	private ListView lvIntakeHistory;
	private Spinner spinnerFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_med_intake_history);
		lvIntakeHistory = (ListView) findViewById(R.id.lvIntakeHistory);

		spinnerFilter = (Spinner) findViewById(R.id.spinnerFilter);

		Intent intent = getIntent();
		final String pin = intent.getStringExtra("pin");

		MedSchedSQLite MSSQLite = new MedSchedSQLite(context);
		final List<String> medNames = new ArrayList<String>();
		medNames.add("Filter by Medicine Name");
		medNames.addAll(MSSQLite.getMedNames(pin));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, medNames);

		spinnerFilter.setAdapter(dataAdapter);

		spinnerFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String medName = null;

				if (!(position == 0)) {
					medName = medNames.get(position);
				}

				displayIntakeHistory(pin, medName);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		displayIntakeHistory(pin, null);
		
		uploadMedIntakeRecs();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, ListOfSchedMed.class);
		startActivity(intent);
		this.finish();
	}

	public void displayIntakeHistory(String pin, String medName) {

		MedSchedSQLite sqlite = new MedSchedSQLite(getApplicationContext());
		final List<MedIntakeRecord> listMedIntakeHistory = sqlite
				.getAllMedIntakeRecords(pin, medName);

		MedHistoryAdapter<MedIntakeRecord> medSchedAdapter = new MedHistoryAdapter<MedIntakeRecord>(
				getApplicationContext(), listMedIntakeHistory);
		lvIntakeHistory.setAdapter(medSchedAdapter);

		lvIntakeHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				// showSelectedMedSched(listMedScheds.get(position));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.med_intake_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

//		case R.id.action_clear_history:
//			MedSchedSQLite MSSQLite = new MedSchedSQLite(context);
//			MSSQLite.clearAllIntakeHistory();
//			Intent refresh = new Intent(this, MedIntakeHistory.class);
//			this.finish();
//			startActivity(refresh);
//			return true;
//		case R.id.action_upload_recs:
//			uploadMedIntakeRecs();
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	private void uploadMedIntakeRecs(){
		Intent intent = getIntent();
		String pin = intent.getStringExtra("pin");
		MedSchedSQLite MSSQLite2 = new MedSchedSQLite(context);
		List<MedIntakeRecord> medIRList = new ArrayList<MedIntakeRecord>();
		medIRList = MSSQLite2.getAllMedIntakeRecords(pin, null);
		MedIntakeRecord[] medIRecs = (MedIntakeRecord[]) medIRList
				.toArray(new MedIntakeRecord[medIRList.size()]);

		WebServiceUploadMedIntakeHistory ws = new WebServiceUploadMedIntakeHistory();

		ws.setMedIRecs(medIRecs);
		ws.setMedInHist(this);
		ws.setPin(pin);
		ws.setContext(context);
		ws.execute();		
	}
	
	
	public void toastUploadResult(String msgResponse){
		if (!(msgResponse==null)) {
			Toast.makeText(context, msgResponse, Toast.LENGTH_LONG)
			.show();
		}
	}

	@SuppressWarnings("hiding")
	class MedHistoryAdapter<MedIntakeRecord> extends
			ArrayAdapter<MedIntakeRecord> {

		private final Context context;
		private final List<MedIntakeRecord> medHistories;

		public MedHistoryAdapter(Context context,
				List<MedIntakeRecord> medHistories) {
			super(context, R.layout.med_sched_table, medHistories);
			this.context = context;
			this.medHistories = medHistories;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.intake_history_lv, parent,
					false);
			TextView medName = (TextView) rowView
					.findViewById(R.id.tvIHMedName);
			TextView actualDateTimeIntake = (TextView) rowView
					.findViewById(R.id.tvActualDateTime);

			MedIntakeRecord currentMedHistRec = medHistories.get(position);

			medName.setTextColor(Color.parseColor("#000099"));
			actualDateTimeIntake.setTextColor(Color.parseColor("#3399FF"));

			String isUploaded = ((com.slmc.models.MedIntakeRecord) currentMedHistRec)
					.isUploaded();

			if (isUploaded.equalsIgnoreCase("true")) {
				actualDateTimeIntake.setTextColor(Color.parseColor("#000099"));
			}

			medName.setText(((com.slmc.models.MedIntakeRecord) currentMedHistRec)
					.getMedName());

			actualDateTimeIntake
					.setText(((com.slmc.models.MedIntakeRecord) currentMedHistRec)
							.getActualDateTime());
			return rowView;
		}

	}

}
