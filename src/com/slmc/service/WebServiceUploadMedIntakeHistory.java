package com.slmc.service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.slmc.medschedapp.MedIntakeHistory;
import com.slmc.models.MedIntakeRecord;
import com.slmc.sqlite.MedSchedSQLite;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class WebServiceUploadMedIntakeHistory extends AsyncTask<Void, Void, Void> {

	private String msgResponse;
	private String pin;
	private Context context;

	
//	private ListOfSchedMed currentLOSM;
	
//	private Login currentLogin;
	
	private MedIntakeRecord[] medIRecs;
//
	private MedIntakeHistory MedInHist;

	private ProgressDialog mProgressDialog;

	private final static String NAMESPACE = "http://ws.slmc.com";
	private final static String URL = "http://192.168.52.183:8080/MedSechedWS/services/MedTransactionWS?wsdl";
	private final static String METHOD_SAVE_INTAKE_RECS = "saveIntakeHistory";
	private final String SOAP_SAVE_INTAKE_RECS = "http://ws.slmc.com/saveIntakeHistory/";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		mProgressDialog = ProgressDialog.show(getContext(), "Wait",
//				"Uploading your Intake History");
	}

	@Override
	protected Void doInBackground(Void... params) {
		uploadIntakeHistory(getMedIRecs());
		
		
//		MedSchedSQLite MSSQLite = new MedSchedSQLite(context);
//		MedIntakeRecord[] medIntakeHistory = getMedIntakeHistory(pin);
//		if (!(medIntakeHistory==null)) {
//			for (int i = 0; i < medIntakeHistory.length; i++) {
//				MedIntakeRecord medIRec = medIntakeHistory[i];
//				medIRec.setUploaded("true");
//				MSSQLite.addMedIntakeRecord(medIRec);
//			}
//		}
		
		
		return null;
	}

	
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		
//		getCurrentLogin().showListOfMedSched();
//		getCurrentLogin().finish();
		
//		getMedInHist().displayIntakeHistory(getPin(), null);
		
		getMedInHist().toastUploadResult(getMsgResponse());
		try {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}			
		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		
	}
//	
//	public MedIntakeRecord[] getMedIntakeHistory(String pin){
//		
//		
//		final SoapObject request = new SoapObject(NAMESPACE,
//				METHOD_GET_INTAKE_RECS);
//
//		PropertyInfo medSchedIdProp = new PropertyInfo();
//		medSchedIdProp.setName("pin");
//		medSchedIdProp.setValue(pin);
//		medSchedIdProp.setType(String.class);
//		request.addProperty(medSchedIdProp);
//
//		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//				SoapEnvelope.VER11);
//		envelope.setOutputSoapObject(request);
//		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//
//		MedIntakeRecord[] medIRs = null;
//		try {
//
//			if (android.os.Build.VERSION.SDK_INT > 9) {
//				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//						.permitAll().build();
//				StrictMode.setThreadPolicy(policy);
//			}
//
//			androidHttpTransport.call(SOAP_GET_INTAKE_RECS, envelope);
//			
//			@SuppressWarnings("unchecked")
//			java.util.Vector<SoapObject> response = (java.util.Vector<SoapObject>) envelope
//					.getResponse();
//
//
//			Log.d("ung response", response.toString());
//			medIRs = getMultiHistory(response);
//			
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
//			SoapObject response = null;
//
//			try {
//				
//				
//				response = (SoapObject) envelope.getResponse();
//				if (response != null) {
//					medIRs = getSingleHistory(response);
//				}
//
////			} catch (NullPointerException e1) {
////				setMsgResponse("PIN does not EXIST or NO Scheduled Medication for that PIN!");
//			} catch (Exception e1) {
//				setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
//			}
//
////		} catch (NullPointerException e) {
////			setMsgResponse("PIN does not EXIST or NO Scheduled Medication for that PIN!");
//		} catch (Exception e) {
//			setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
//		}
//
//		return medIRs;
//
//	}
//	
////	
//	public MedIntakeRecord[] getMultiHistory(Vector<SoapObject> response) {
//		MedIntakeRecord[] medIRecs = new MedIntakeRecord[((Vector<SoapObject>) response)
//				.size()];
//		for (int i = 0; i < response.size(); i++) {
//			MedIntakeRecord medIRec = new MedIntakeRecord();
//			medIRec.setPin(response.get(i).getProperty("pin").toString());
//			medIRec.setMedName(response.get(i).getProperty("medName")
//					.toString());
//			medIRec.setActualDateTime(response.get(i).getProperty("time").toString());
//			medIRecs[i] = medIRec;
//		}
//		return medIRecs;
//	}
//
//	public MedIntakeRecord[] getSingleHistory(SoapObject response) {
//		MedIntakeRecord[] medIRecs = new MedIntakeRecord[1];
//
//		MedIntakeRecord medIRec = new MedIntakeRecord();
//		medIRec.setPin(response.getProperty("pin").toString());
//		medIRec.setMedName(response.getProperty("medName")
//				.toString());
//		medIRec.setActualDateTime(response.getProperty("time").toString());
//		medIRecs[0] = medIRec;
//
//		return medIRecs;
//	}
//	
//	

	public void uploadIntakeHistory(MedIntakeRecord[] medIntakeRecs) {

		for (int i = 0; i < medIntakeRecs.length; i++) {
			MedIntakeRecord medIntakeRec = medIntakeRecs[i];
			uploadIntakeRecord(medIntakeRec);
		}

	}

	public void uploadIntakeRecord(MedIntakeRecord medIntakeRec) {
		
//		String pin = null, medName = null, dateTime = null;
		
		String pin = medIntakeRec.getPin();
		String medName = medIntakeRec.getMedName();
		String dateTime = medIntakeRec.getActualDateTime();
		
		final SoapObject request = new SoapObject(NAMESPACE,
				METHOD_SAVE_INTAKE_RECS);
		PropertyInfo pinProp = new PropertyInfo();
		pinProp.setName("pin");
		pinProp.setValue(pin);
		pinProp.setType(String.class);
		request.addProperty(pinProp);

		PropertyInfo medNameProp = new PropertyInfo();
		medNameProp.setName("medName");
		medNameProp.setValue(medName);
		medNameProp.setType(String.class);
		request.addProperty(medNameProp);

		PropertyInfo dateTimeProp = new PropertyInfo();
		dateTimeProp.setName("dateTime");
		dateTimeProp.setValue(dateTime);
		dateTimeProp.setType(String.class);
		request.addProperty(dateTimeProp);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			androidHttpTransport.call(SOAP_SAVE_INTAKE_RECS, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			setMsgResponse(response.toString());

			
			//update local record status
			MedSchedSQLite MSSQLite = new MedSchedSQLite(context);

			if (medIntakeRec.isUploaded().equalsIgnoreCase("false")) {
				MSSQLite.updateLocalMedIntakeRecord(medIntakeRec);
			}

		} catch (Exception e) {
//			setMsgResponse("No Internet Connection. We'll try to upload your intakes history later.");
			Log.d("Exception sa pagsend sa WS", e.toString());
		}
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


	public MedIntakeRecord[] getMedIRecs() {
		return medIRecs;
	}

	public void setMedIRecs(MedIntakeRecord[] medIRecs) {
		this.medIRecs = medIRecs;
	}

	public MedIntakeHistory getMedInHist() {
		return MedInHist;
	}

	public void setMedInHist(MedIntakeHistory medInHist) {
		MedInHist = medInHist;
	}

}
