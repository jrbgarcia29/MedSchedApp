package com.slmc.service;

import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.slmc.medschedapp.Login;
import com.slmc.models.MedIntakeRecord;
import com.slmc.sqlite.MedSchedSQLite;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class WebServiceMedIntakeHistory extends AsyncTask<Void, Void, Void> {

	private String msgResponse;
	private String pin;
	private Context context;
	
	private Login currentLogin;

	private ProgressDialog mProgressDialog;

	private final static String NAMESPACE = "http://ws.slmc.com";
	private final static String URL = "http://192.168.52.183:8080/MedSechedWS/services/MedTransactionWS?wsdl";
	private final static String METHOD_GET_INTAKE_RECS = "getIntakeHistory";
	private final String SOAP_GET_INTAKE_RECS = "http://ws.slmc.com/getIntakeHistory/";

	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(getContext(), "Wait",
				"Checking your Intake History");
	}

	@Override
	protected Void doInBackground(Void... params) {

		MedSchedSQLite MSSQLite = new MedSchedSQLite(context);
		MedIntakeRecord[] medIntakeHistory = getMedIntakeHistory(pin);
		if (!(medIntakeHistory==null)) {
			for (int i = 0; i < medIntakeHistory.length; i++) {
				MedIntakeRecord medIRec = medIntakeHistory[i];
				medIRec.setUploaded("true");
				MSSQLite.addMedIntakeRecord(medIRec);
			}
		}

		return null;
	}

	
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		
		getCurrentLogin().showListOfMedSched();
		getCurrentLogin().finish();
		try {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}			
		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		
	}
	
	public MedIntakeRecord[] getMedIntakeHistory(String pin){
		
		
		final SoapObject request = new SoapObject(NAMESPACE,
				METHOD_GET_INTAKE_RECS);

		PropertyInfo medSchedIdProp = new PropertyInfo();
		medSchedIdProp.setName("pin");
		medSchedIdProp.setValue(pin);
		medSchedIdProp.setType(String.class);
		request.addProperty(medSchedIdProp);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		MedIntakeRecord[] medIRs = null;
		try {

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			androidHttpTransport.call(SOAP_GET_INTAKE_RECS, envelope);
			
			@SuppressWarnings("unchecked")
			java.util.Vector<SoapObject> response = (java.util.Vector<SoapObject>) envelope
					.getResponse();


			Log.d("ung response", response.toString());
			medIRs = getMultiHistory(response);
			
		} catch (ClassCastException e) {
			e.printStackTrace();
			setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
			SoapObject response = null;

			try {
				
				
				response = (SoapObject) envelope.getResponse();
				if (response != null) {
					medIRs = getSingleHistory(response);
				}

			} catch (Exception e1) {
				setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
			}

		} catch (Exception e) {
			setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
		}

		return medIRs;

	}
	
	
	public MedIntakeRecord[] getMultiHistory(Vector<SoapObject> response) {
		MedIntakeRecord[] medIRecs = new MedIntakeRecord[((Vector<SoapObject>) response)
				.size()];
		for (int i = 0; i < response.size(); i++) {
			MedIntakeRecord medIRec = new MedIntakeRecord();
			medIRec.setPin(response.get(i).getProperty("pin").toString());
			medIRec.setMedName(response.get(i).getProperty("medName")
					.toString());
			medIRec.setActualDateTime(response.get(i).getProperty("time").toString());
			medIRecs[i] = medIRec;
		}
		return medIRecs;
	}

	public MedIntakeRecord[] getSingleHistory(SoapObject response) {
		MedIntakeRecord[] medIRecs = new MedIntakeRecord[1];

		MedIntakeRecord medIRec = new MedIntakeRecord();
		medIRec.setPin(response.getProperty("pin").toString());
		medIRec.setMedName(response.getProperty("medName")
				.toString());
		medIRec.setActualDateTime(response.getProperty("time").toString());
		medIRecs[0] = medIRec;

		return medIRecs;
	}
	
	


	public String getMsgResponse() {
		return msgResponse;
	}

	public void setMsgResponse(String msgResponse) {
		this.msgResponse = msgResponse;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}


	public Login getCurrentLogin() {
		return currentLogin;
	}

	public void setCurrentLogin(Login currentLogin) {
		this.currentLogin = currentLogin;
	}

}
