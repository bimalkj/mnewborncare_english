package org.intrahealth.mnewborncareeng;

//import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import org.intrahealth.mnewborncareeng.R;

//import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.content.IntentFilter;
import android.database.Cursor;
//import android.database.SQLException;
//import android.os.Binder;
//import android.os.Handler;

import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SmsService extends Service {


	
	int counter = 0;
	int idle =0;
	static final int UPDATE_INTERVAL = 45000;
	private Timer timer;
	private TimerTask task;
	private DBAdapter mydb;
	public String phoneNumber="1234";
	static boolean radio_on=false;
	static boolean serviceRunning=false;
	static boolean busy=false;
	long sms_id=-1;
	private int pending=0;
	TelephonyManager telephonyManager;
	PhoneStateListener phoneStateListener;
    //private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.intrahealth.mnewborncare.smsservice";
    public static final int DURATION = 45000;
    public static final int mxidle=20;
    private IntentFilter mIntentFilter;
    private SMSreceiver mSMSreceiver;
    private Handler mHandler = new Handler();
    //private final Handler handler = new Handler();
    Intent intent;
	int ss=-1;
/*	
	private final IBinder binder = new LocalBinder();
	public class LocalBinder extends Binder {
		SmsService getService() {
			return SmsService.this;
		}
	}
	*/
	
	
	private Runnable periodicTask = new Runnable() {
		public void run() {	
		    mydb = DBAdapter.getInstance(getBaseContext());
			//radio_on=Connectivity.isConnected(getBaseContext());
			radio_on=(ss==android.telephony.ServiceState.STATE_IN_SERVICE);
			if (radio_on)
			{
				
			Cursor pendSms=mydb.getSmsList();
			pending=pendSms.getCount();
			//intent.putExtra("counter", pending);
	    	//sendBroadcast(intent);
			if (pendSms.moveToFirst())
				{
				idle=0;
				int msgid=pendSms.getInt(pendSms.getColumnIndex("_id"));
				int retry=pendSms.getInt(pendSms.getColumnIndex("retry"))+1;
				String msg= pendSms.getString(pendSms.getColumnIndex("msg"));
				
				mydb.logAct("Sending sms "+msg+" try "+retry);
				Log.d("SmsService", msg+" "+String.valueOf(retry));
				sendSMS(phoneNumber, msg,msgid,
						retry);
				//Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
				} else { 
					Log.d("SmsService", "No pending sms "+String.valueOf(idle));
					idle++;if (idle>mxidle) {
							//task.cancel();
							//timer.cancel();
							mydb.myclose();
							mHandler.removeCallbacks(periodicTask);
							stopSelf();
							serviceRunning=false;
						}
					}
			pendSms.close();
			} else Log.d("SmsService", "Radio_off");
			if (serviceRunning) mHandler.postDelayed(periodicTask, DURATION);
		}	
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder<SmsService>(this);
		//return binder;
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
    	intent = new Intent(BROADCAST_ACTION);
	    /*
	    try {
	    	mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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

			//registerReceiver(sent_br, new IntentFilter("SMS_SENT"));
			phoneNumber=getString(R.string.smsno);
			serviceRunning=true;
			  mIntentFilter = new IntentFilter();
			  mSMSreceiver = new SMSreceiver();
		        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
				mIntentFilter.setPriority(100);
		        registerReceiver(mSMSreceiver, mIntentFilter);
			mHandler.postDelayed(periodicTask, DURATION);
	  } 
	/*
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	// We want this service to continue running until it is explicitly
	// stopped, so return sticky.
		
	if (!serviceRunning)
	{
		serviceRunning=true;timer = new Timer();idle=0;				
		Toast.makeText(this, "SMS Service Started "+ss, Toast.LENGTH_SHORT).show();
		Log.d("SmsService", "SMS Service Started "+ss);
		phoneNumber=getString(R.string.smsno);
		//radio_on=!DBAdapter.isAirplaneModeOn(getBaseContext());
		send_sms_queue();
		} else {
			Toast.makeText(this, "SMS Service already started "+ss, Toast.LENGTH_SHORT).show();
			Log.d("SmsService", "SMS Service already started "+ss);
		}
	return START_STICKY;
	}
	
	*/
	
	public void send_sms_queue() {
//		if (pending>0)
		if (!busy)
		timer.scheduleAtFixedRate(task = new TimerTask() {
		public void run() {	
		    mydb = DBAdapter.getInstance(getBaseContext());
			//radio_on=Connectivity.isConnected(getBaseContext());
			radio_on=(ss==android.telephony.ServiceState.STATE_IN_SERVICE);
			if (radio_on)
			{
				
			Cursor pendSms=mydb.getSmsList();
			pending=pendSms.getCount();
			intent.putExtra("counter", pending);
	    	sendBroadcast(intent);
			if (pendSms.moveToFirst())
				{
				idle=0;
				Log.d("SmsService",  pendSms.getString(pendSms.getColumnIndex("msg"))+" "+String.valueOf(pendSms.getInt(pendSms.getColumnIndex("retry"))));
				sendSMS(phoneNumber, pendSms.getString(pendSms.getColumnIndex("msg")),pendSms.getInt(pendSms.getColumnIndex("_id")),
						pendSms.getInt(pendSms.getColumnIndex("retry"))+1);
				//Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
				} else { 
					Log.d("SmsService", "No pending sms "+String.valueOf(idle));
					/*
					idle++;if (idle>20) {
							task.cancel();
							timer.cancel();
							mydb.myclose();
							stopSelf();
							serviceRunning=false;
						}*/
					}
			pendSms.close();
			} else Log.d("SmsService", "Radio_off");
		}
		}, 0, UPDATE_INTERVAL);
	}
	
	@Override
	public void onDestroy() {
		serviceRunning=false;		
		if (mydb!=null) mydb.myclose();
	if (timer != null){
	timer.cancel();
	task.cancel();
	}
	Log.d("SmsService", "SMS Service destroyed ");
	Toast.makeText(this, "SMS Service destroyed", Toast.LENGTH_SHORT).show();
	//unregisterReceiver(sent_br);
	super.onDestroy();
	unregisterReceiver(mSMSreceiver); 
	mHandler.removeCallbacks(periodicTask);
	}

	
	@Override
    public boolean stopService(Intent name) {
        // TODO Auto-generated method stub
		serviceRunning=false;
		if (mydb!=null) mydb.close();
		if (timer != null){
			timer.cancel();
			task.cancel();
			}
        Log.d("SmsService", "SMS Service stopped ");
        Toast.makeText(this, "SMS Service stopped", Toast.LENGTH_SHORT).show();
        return super.stopService(name);

    }

 	
	private synchronized void sendSMS(String phoneNumber, String message,int sms_id,int retry) 
    {        
        String SENT = "SMS_SENT";//+String.valueOf(sms_id);
        String DELIVERED = "SMS_DELIVERED";//+String.valueOf(sms_id);
        //busy=true;
        
        Intent si=new Intent(SENT);si.putExtra("sms_id", sms_id);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, sms_id,
            si, PendingIntent.FLAG_CANCEL_CURRENT);
 
        Intent di=new Intent(DELIVERED);di.putExtra("sms_id", sms_id);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, sms_id,
        		di, PendingIntent.FLAG_UPDATE_CURRENT);
        mydb.updateSms(sms_id,retry);
 
        //---when the SMS has been sent---
        /*
        BroadcastReceiver sent_br=new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	//Bundle extras = arg1.getExtras();
            	int smsid=arg1.getIntExtra("sms_id", 0);
            	busy=false;
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent "+sms_id+String.valueOf(smsid), 
                                Toast.LENGTH_SHORT).show();
                        	mydb.deleteSms(sms_id);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
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
                unregisterReceiver(this);
            }
        };


        registerReceiver(sent_br, new IntentFilter(SENT));	
        //---when the SMS has been delivered---
        BroadcastReceiver del_br=new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	int smsid=arg1.getIntExtra("sms_id", 0);
            	switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered "+sms_id+String.valueOf(smsid), 
                                Toast.LENGTH_SHORT).show();
                        	mydb.deleteSms(sms_id);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered ", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            unregisterReceiver(this);	
            }
        };
        registerReceiver(del_br, new IntentFilter(DELIVERED));        
 */
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message+":"+sms_id, sentPI, deliveredPI);

        
}

}