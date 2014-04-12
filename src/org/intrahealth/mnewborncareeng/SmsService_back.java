package org.intrahealth.mnewborncareeng;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SmsService_back extends Service {

	int counter = 0;
	static final int UPDATE_INTERVAL = 120000;
	private Timer timer = new Timer();
	private DBAdapter mydb;
	public String phoneNumber="1234";
	static boolean radio_on=false;
	long sms_id=-1;
	private int pending=0;
	TelephonyManager telephonyManager;
	PhoneStateListener phoneStateListener;
	int ss=-1;
	
	private final IBinder binder = new LocalBinder();
	public class LocalBinder extends Binder {
		SmsService_back getService() {
			return SmsService_back.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
		//return null;
	}
	
	public int getPending()
	{
		return pending;
	}
	
	@Override
	  public void onCreate()
	  {
	    super.onCreate();
	    phoneStateListener = new PhoneStateListener() {
			public void onCallForwardingIndicatorChanged(boolean cfi) {}
			public void onCallStateChanged(int state, String incomingNumber) {}
			public void onCellLocationChanged(CellLocation location) {}
			public void onDataActivity(int direction) {}
			public void onDataConnectionStateChanged(int state) {}
			public void onMessageWaitingIndicatorChanged(boolean mwi) {}
			public void onServiceStateChanged(ServiceState serviceState) { ss=serviceState.getState(); }
			public void onSignalStrengthChanged(int asu) {}
			};
			
			telephonyManager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
					PhoneStateListener.LISTEN_CALL_STATE |
					PhoneStateListener.LISTEN_CELL_LOCATION |
					PhoneStateListener.LISTEN_DATA_ACTIVITY |
					PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
					PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
					PhoneStateListener.LISTEN_SERVICE_STATE |
					PhoneStateListener.LISTEN_SIGNAL_STRENGTH);	
	  } 
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	// We want this service to continue running until it is explicitly
	// stopped, so return sticky.
	    mydb = new DBAdapter(this);
	    try {
	    	mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
					
	Toast.makeText(this, "SMS Service Started "+ss, Toast.LENGTH_SHORT).show();
	phoneNumber=getString(R.string.smsno);
	//radio_on=!DBAdapter.isAirplaneModeOn(getBaseContext());

	Cursor pendSms=mydb.getSmsList();
	pending=pendSms.getCount();
	send_sms_queue();
	return START_STICKY;
	}
	
	private void send_sms_queue() {
	timer.scheduleAtFixedRate( new TimerTask() {
		public void run() {
		
			//radio_on=Connectivity.isConnected(getBaseContext());
			radio_on=(ss==android.telephony.ServiceState.STATE_IN_SERVICE);
			if (radio_on)
			{
			Cursor pendSms=mydb.getSmsList();
			pending=pendSms.getCount();
			if (pending>0)
				{
				pendSms.moveToFirst();
				Log.d("SmsService", String.valueOf(pendSms.getInt(pendSms.getColumnIndex("retry"))));
				sendSMS(phoneNumber, pendSms.getString(pendSms.getColumnIndex("msg")),pendSms.getInt(pendSms.getColumnIndex("_id")),
						pendSms.getInt(pendSms.getColumnIndex("retry"))+1);
				Log.d("SmsService", "SMS sent");
				//Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
				}
			} else Log.d("SmsService", "Radio_off");
		}
		}, 0, UPDATE_INTERVAL);
	}
	
	@Override
	public void onDestroy() {
		mydb.close();
	super.onDestroy();
	if (timer != null){
	timer.cancel();
	}
	Toast.makeText(this, "SMS Service stopped", Toast.LENGTH_SHORT).show();
	}

	
	private void sendSMS(String phoneNumber, String message,final long sms_id,final int retry)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        

 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        	mydb.deleteSms(sms_id);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        mydb.updateSms(sms_id,retry);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        //mydb.updateSms(sms_id,retry);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        //mydb.updateSms(sms_id,retry);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        //mydb.updateSms(sms_id,retry);
                        break;
                }
            }
        }, new IntentFilter(SENT));	
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message+":"+sms_id, sentPI, deliveredPI);  
}
	


}