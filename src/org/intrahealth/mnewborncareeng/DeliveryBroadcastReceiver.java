package org.intrahealth.mnewborncareeng;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class DeliveryBroadcastReceiver extends BroadcastReceiver {

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
            	msginfo="SMS delivered "+sms_id;
                break;
            case Activity.RESULT_CANCELED:
            	msginfo="SMS cancelled "+sms_id;
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
