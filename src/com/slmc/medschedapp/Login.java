package com.slmc.medschedapp;

import java.util.ArrayList;
import java.util.List;

import com.slmc.R;
import com.slmc.models.MedSched;
import com.slmc.service.WebServiceMedIntakeHistory;
import com.slmc.service.WebServiceMedScheds;
import com.slmc.sqlite.MedSchedSQLite;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private final Context context = this;
	private Button btnFetch;
	private EditText etPin;
	private String pin;


	public Context getContext() {
		return context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		etPin = (EditText) findViewById(R.id.etPin);
		btnFetch = (Button) findViewById(R.id.btnFetch);

		//check if there is an existing Logged in user
		if (userExist()) {
			Intent intent = new Intent(context, ListOfSchedMed.class);
			startActivity(intent);
			finish();
		}
		
		//use for updating med schedules of currently logged in user
		Intent intent = getIntent();
		pin = intent.getStringExtra("pin");
		
		if (!(pin==null)) {
			logIn(pin);
		}

		btnFetch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pin = etPin.getText().toString();
				logIn(pin);
			}
		});
	}

	//method to check if there is currently logged in user
	public Boolean userExist() {
		MedSchedSQLite medSchedSQLite = new MedSchedSQLite(context);
		List<MedSched> medScheds = new ArrayList<MedSched>();
		medScheds = medSchedSQLite.getAllMedScheds();
		if (medScheds.size() != 0) {
			return true;
		} else {
			return false;
		}

	}

	public void logIn(String pin) {
		WebServiceMedScheds ws = new WebServiceMedScheds();
		ws.setPin(pin);
		ws.setCurrLogIn(this);
		ws.setContext(context);
		ws.execute();

	}
	
	public void saveToLocalDB(MedSched[] medScheds, String msgResponse){
		
		if (medScheds != null) {
			MedSchedSQLite dbconn = new MedSchedSQLite(this);
			dbconn.removeAllMedScheds();//delete existing med schedules to make sure all scheds will be updated
			dbconn.addAllMedSched(medScheds);
			
			if (!(this.pin==null)) {		
				Toast.makeText(context, "Medicine Schedules successfully updated.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(
					getApplicationContext(),
					msgResponse,
					Toast.LENGTH_LONG).show();
		}
	}
	
	public void checkIntakeHistory(String pin){
		WebServiceMedIntakeHistory ws = new WebServiceMedIntakeHistory();
		ws.setPin(pin);
		ws.setCurrentLogin(this);
		ws.setContext(context);
		ws.execute();
	}
	
	public void showListOfMedSched(){		
		Intent intent = new Intent(context, ListOfSchedMed.class);
		intent.putExtra("pin", etPin.getText());
		startActivity(intent);
		this.finish();
	}

}