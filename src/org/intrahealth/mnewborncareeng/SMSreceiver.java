package org.intrahealth.mnewborncareeng;

import java.util.Locale;

import org.intrahealth.mnewborncareeng.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;


public class SMSreceiver extends BroadcastReceiver {
	
	  private static final String SMS_RECEIVED ="android.provider.Telephony.SMS_RECEIVED";

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(SMS_RECEIVED)) {
			String queryString = context.getString(R.string.signature);
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				for (SmsMessage message : messages) 
					if (message.getMessageBody().startsWith(queryString)) 
					{
						//=^ 352:SY:23:25
						String msg=message.getMessageBody().substring(2).trim();
						String msgar[]=msg.split("\\:");
						if (msgar[1].startsWith("SY"))
						{
							DBAdapter mydb=DBAdapter.getInstance(context);
							String filtsql=String.format(Locale.US, "between %s and %s",msgar[2],msgar[3]);
							String sql="select _id,msg from pend_sms where _id "+filtsql;
							Cursor c=mydb.rawQuery(sql);
							if (c.moveToFirst())
								while (!c.isAfterLast())
								{
									int sms_id=c.getInt(c.getColumnIndex("_id")); 
									String phoneNumber=context.getString(R.string.smsno);
									String smsg=c.getString(c.getColumnIndex("msg"));
									SmsManager sms = SmsManager.getDefault();
								        sms.sendTextMessage(phoneNumber, null, smsg+":"+sms_id, null, null);
								        c.moveToNext();
								}
						abortBroadcast();
						}
						
					}
			}
		}
	}

}

