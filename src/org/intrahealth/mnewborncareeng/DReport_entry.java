package org.intrahealth.mnewborncareeng;


import java.util.Calendar;
import java.util.Locale;




import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
import android.content.SharedPreferences;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

//import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;


public class DReport_entry extends Activity {
	int pid=0;
	//private static int TAKE_PICTURE = 1;
	private DBAdapter mydb;
	//Typeface face;
	DatePicker dtp;
	Boolean send_sms=false;
	Spinner spnDstat;
	String q_msg,uid;
	Calendar gcal,mc;
	TextView tvMname;
	String dstr[]={"Child","Mother","Both (Child and Mother)"};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dreport_entry);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
	    //tvHead.setText("");
        //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //  ((TextView)findViewById(R.id.tvlblMname)).setTypeface(face);
        //  tvHead.setTypeface(face);
		spnDstat=(Spinner)findViewById(R.id.spnDstat);
        //((Button)findViewById(R.id.btnSaveDeath)).setTypeface(face);


        
        tvMname=(TextView)findViewById(R.id.tvMname);
        //tvMname.setTypeface(face);
        TextView tvSlno=(TextView)findViewById(R.id.slno);
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	pid = extras.getInt("pid");
        	tvSlno.setText(String.valueOf(pid));
        	tvMname.setText(extras.getString("mname"));       	
        }
        mydb = DBAdapter.getInstance(getApplicationContext());
	    /*
        mydb = new DBAdapter(this);
	    try {
	    	//mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     */
	    Cursor c=mydb.getPregInfo(pid);
	    mc=Calendar.getInstance();
	    mc=DBAdapter.removeTime(mc);
	    String dt_str[];
	     
	    //if (c.getInt(c.getColumnIndex("last_visit"))==0)
	    if (c.isNull(c.getColumnIndex("avd")))
	    	dt_str=c.getString(c.getColumnIndex("dob")).split("\\-");
	    else 
	    {
	    	dt_str=c.getString(c.getColumnIndex("avd")).split("\\-");
	    	spnDstat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){  
	    		  
	    		
                public void onItemSelected(AdapterView<?> adapter, View v, int i, long lng) {  
                    if (i==3) {
                    	Toast.makeText(DReport_entry.this,"Wrong selection !!", Toast.LENGTH_SHORT).show();
                    	spnDstat.setSelection(0);
                    }
                     
                }  
                public void onNothingSelected(AdapterView<?> arg0) {  
                    Toast.makeText(DReport_entry.this,"Nothing selected !!", Toast.LENGTH_LONG).show();  
                    return;  
                }  
                });  
                  
 
	    }
	    mc.set(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]));
	    
        dtp=(DatePicker)findViewById(R.id.dtpDodth);
        dtp.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        Calendar defCalendar = Calendar.getInstance();
        
        dtp.init(defCalendar.get(Calendar.YEAR), defCalendar.get(Calendar.MONTH), defCalendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

		    public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		        if(isDateInvalid(view)){
		        	Toast.makeText(getBaseContext(), "Invalid date ", Toast.LENGTH_SHORT).show();
		            //Calendar mCalendar = Calendar.getInstance();
		            //view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
		        }
		    }
		});
	    
        addListenerOnButton();
        SharedPreferences prefs=PreferenceManager
				.getDefaultSharedPreferences(this);
		uid=prefs.getString("id", "1");
		send_sms=prefs.getBoolean("send_sms", false);
    }

	@Override
	public void onDestroy()
	{		
		mydb.close();
		super.onDestroy();
	}

	
	private void addListenerOnButton() {
		
		

		
		Button btnSave = (Button) findViewById(R.id.btnSaveDeath);
   	 
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 if(!isDateInvalid(dtp)) openPreviewDialog();
				 else Toast.makeText(getBaseContext(), "Invalid date", Toast.LENGTH_SHORT).show();
				/*
				final Context ctx=arg0.getContext();
				new AlertDialog.Builder(ctx)
		           .setMessage("Please confirm ?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		   				int dstat;
						String longMessage="";
						

						dstat=(int)spnDstat.getSelectedItemId();
							
							String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
							mydb.regDeath(pid, pdte, dstat);
							String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
							String phoneNumber=getString(R.string.smsno);
								longMessage=String.format("%s:DA:%d:%d:%s:",pktHeader,pid,dstat,pdte);
							if (send_sms) sendSMS(phoneNumber, longMessage);
							Toast.makeText(ctx,"Information added/changed successfully ..", Toast.LENGTH_SHORT).show();
						finish();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();*/
			}
		});
	}

	/*
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage("Are you sure you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}	
	*/
	/*
	private void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        q_msg=message;

 
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
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        mydb.insertSms(q_msg);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        mydb.insertSms(q_msg);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        mydb.insertSms(q_msg);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        mydb.insertSms(q_msg);
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
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
*/
    private boolean isDateInvalid(DatePicker tempView) {
    	
        
	    //Toast.makeText(getBaseContext(),mc.get(java.util.Calendar.DATE) +" "+
	    //		mc.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.SHORT, Locale.US) +" "+
	   // 		mc.get(java.util.Calendar.YEAR) , 
       //         Toast.LENGTH_SHORT).show();
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        mCalendar=DBAdapter.removeTime(mCalendar);
        tempCalendar = DBAdapter.removeTime(tempCalendar);
        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth());
        //Log.d("info", mc.toString());
        //Log.d("info",tempCalendar.toString());
        if (tempCalendar.equals(mCalendar) || tempCalendar.equals(mc)) return false;
        else 
        if(tempCalendar.after(mCalendar) || tempCalendar.before(mc))
            return true;
        else 
            	return false;
        
    }	

    void openPreviewDialog(){
    	

    	Context ctx=getApplicationContext();

    	//final String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
        AlertDialog.Builder customDialog 
         = new AlertDialog.Builder(DReport_entry.this);
        customDialog.setTitle("Attention!");
       // Context ctx=getApplicationContext();
        LayoutInflater layoutInflater 
     = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     View view=layoutInflater.inflate(R.layout.preg_infodlg,null);
     LinearLayout llContent=(LinearLayout)view.findViewById(R.id.llContent);
     llContent.removeAllViews();
    	TextView tvH=new TextView(ctx);
    	tvH.setText("Mother's Name: "+tvMname.getText());
    	tvH.setTextSize(18);
    	tvH.setTextColor(Color.BLUE);
    	TextView tvEdd=new TextView(ctx);
    	tvEdd.setText(String.format("Date of death: %d-%s-%d",dtp.getDayOfMonth(),DBAdapter.hmstr[dtp.getMonth()],dtp.getYear()));
    	tvEdd.setTextSize(18);
    	tvEdd.setTextColor(Color.BLUE);  
    	TextView tvDth=new TextView(ctx);
    	tvDth.setText(String.format("Death of: "+dstr[(int)spnDstat.getSelectedItemId()]));
    	tvDth.setTextSize(18);
    	tvDth.setTextColor(Color.BLUE);  
    	llContent.addView(tvH);
    	llContent.addView(tvEdd);
    	llContent.addView(tvDth);
     customDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

     
      public void onClick(DialogInterface arg0, int arg1) {
       // TODO Auto-generated method stub
    	  
    	  //String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
    	  int dstat;
			String longMessage="";
			

			dstat=(int)spnDstat.getSelectedItemId();
				
				String pdte=String.format(Locale.getDefault(),"%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
				mydb.regDeath(pid, pdte, dstat);
				String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
				String phoneNumber=getString(R.string.smsno);
					longMessage=String.format(Locale.getDefault(),"%s:DA:%d:%d:%s",pktHeader,pid,dstat,pdte);
    			if (send_sms) mydb.sendSMS(phoneNumber, longMessage);
    			Toast.makeText(getApplicationContext(),"Death information saved", Toast.LENGTH_SHORT).show();
    		finish();
      }});
      
     customDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){

     
      public void onClick(DialogInterface arg0, int arg1) {
       // TODO Auto-generated method stub
        
      }});

           customDialog.setView(view);
           customDialog.show();
       }

}
