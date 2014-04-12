package org.intrahealth.mnewborncareeng;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;


public class SendBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		DBAdapter mydb=DBAdapter.getInstance(context);
    	int sms_id=intent.getIntExtra("sms_id", 0);
    	String msginfo="";
		 switch (getResultCode())
        {
            case Activity.RESULT_OK:
                	mydb.deleteSms(sms_id);
                	msginfo="SMS sent "+sms_id;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            	msginfo="Generic failure "+sms_id;                
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            	msginfo="No service "+sms_id;                
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
            	msginfo="Null PDU "+sms_id;
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            	msginfo="Radio off "+sms_id;
            	break;
        }
        if (!msginfo.equals(""))
        {
		 Toast.makeText(context, msginfo, 
                    Toast.LENGTH_SHORT).show();
		 mydb.logAct(msginfo);
        }
	}
}
