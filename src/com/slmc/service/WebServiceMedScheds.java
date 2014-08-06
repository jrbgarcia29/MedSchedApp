package com.slmc.service;

import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.slmc.medschedapp.Login;
import com.slmc.models.MedSched;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class WebServiceMedScheds extends AsyncTask<Void, Void, Void>{

	private String msgResponse;
	private String pin;
	private MedSched[] medScheds;
	private Context context;
	private Login currLogIn;

	
	private ProgressDialog mProgressDialog;


	private final static String NAMESPACE = "http://ws.slmc.com";
	private final static String URL = "http://192.168.52.183:8080/MedSechedWS/services/MedTransactionWS?wsdl";
	private final static String METHOD_GET_ALL_MED_SCHEDS = "getAllMedSchedByPIN";
	private final String SOAP_GET_ALLMED_SCHEDS = "http://ws.slmc.com/getAllMedSchedByPIN/";
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();		
		mProgressDialog = ProgressDialog.show(getContext(), "Wait", "Fetching");
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		medScheds = getAllMedicalSchedByPIN(getPin());
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		getCurrLogIn().saveToLocalDB(medScheds, msgResponse);
		if (!(medScheds==null)) {
			getCurrLogIn().checkIntakeHistory(getPin());
			getCurrLogIn().showListOfMedSched();
			getCurrLogIn().finish();
			Log.d("Successful Login", "Finishing Login Activity");
		}
		
		
		try {
			if (mProgressDialog != null) {
	            mProgressDialog.dismiss();
	        }
		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
//		
//		if (!(medScheds==null)) {
//			
//		}

	}
	
	
	public MedSched[] getAllMedicalSchedByPIN(String pin) {

		final SoapObject request = new SoapObject(NAMESPACE,
				METHOD_GET_ALL_MED_SCHEDS);

		PropertyInfo medSchedIdProp = new PropertyInfo();
		medSchedIdProp.setName("pin");
		medSchedIdProp.setValue(pin);
		medSchedIdProp.setType(String.class);
		request.addProperty(medSchedIdProp);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		MedSched[] medScheds = null;
		try {

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			androidHttpTransport.call(SOAP_GET_ALLMED_SCHEDS, envelope);

			@SuppressWarnings("unchecked")
			java.util.Vector<SoapObject> response = (java.util.Vector<SoapObject>) envelope
					.getResponse();

			medScheds = getMultiSched(response);

		} catch (ClassCastException e) {
			e.printStackTrace();
			setMsgResponse("PIN does not EXIST or no Scheduled Medication for that PIN!");
			SoapObject response = null;

			try {
				response = (SoapObject) envelope.getResponse();
				if (response != null) {
					medScheds = getSingleSched(response);
				}

			} catch (NullPointerException e1) {
				setMsgResponse("PIN does not EXIST or NO Scheduled Medication for that PIN!");
			} catch (Exception e1) {
				setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
			}

		} catch (NullPointerException e) {
			setMsgResponse("PIN does not EXIST or NO Scheduled Medication for that PIN!");
		} catch (Exception e) {
			setMsgResponse("Failed to connect to the server! Please check your connection and try again.");
		}

		return medScheds;
	}

	public MedSched[] getMultiSched(Vector<SoapObject> response) {
		MedSched[] medScheds = new MedSched[((Vector<SoapObject>) response)
				.size()];
		for (int i = 0; i < response.size(); i++) {
			MedSched medSched = new MedSched();
			medSched.setMedSchedId(Integer.parseInt(response.get(i)
					.getProperty("medSchedId").toString()));
			medSched.setPin(response.get(i).getProperty("pin").toString());
			medSched.setMedName(response.get(i).getProperty("medName")
					.toString());
			medSched.setFreq(response.get(i).getProperty("freq").toString());
			medSched.setStartDate(response.get(i).getProperty("startDate")
					.toString());
			medSched.setEndDate(response.get(i).getProperty("endDate")
					.toString());
			medSched.setStartTime(response.get(i).getProperty("startTime")
					.toString());
			medScheds[i] = medSched;
		}
		return medScheds;
	}

	public MedSched[] getSingleSched(SoapObject response) {
		MedSched[] medScheds = new MedSched[1];

		MedSched medSched = new MedSched();
		medSched.setMedSchedId(Integer.parseInt(response.getProperty(
				"medSchedId").toString()));
		medSched.setPin(response.getProperty("pin").toString());
		medSched.setMedName(response.getProperty("medName").toString());
		medSched.setFreq(response.getProperty("freq").toString());
		medSched.setStartDate(response.getProperty("startDate").toString());
		medSched.setEndDate(response.getProperty("endDate").toString());
		medSched.setStartTime(response.getProperty("startTime").toString());
		medScheds[0] = medSched;

		return medScheds;
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

	public Login getCurrLogIn() {
		return currLogIn;
	}

	public void setCurrLogIn(Login currLogIn) {
		this.currLogIn = currLogIn;
	}

}
