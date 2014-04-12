package org.intrahealth.mnewborncareeng;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
//import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

//import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
//import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

public class Immun_entry extends Activity {
	int slno=0;
	//private static int TAKE_PICTURE = 1;
	private DBAdapter mydb;
	//Typeface face;
	DatePicker dtp;
	Boolean modified=false,send_sms=false;
	//Spinner spnCstat,spnMstat;
	String q_msg,uid;
	int cstat=0;
	Calendar mc;
	TextView tvMname,tvSlno;
	//public static final String hmstr[]={"जनवरी","फरवरी","मार�?च","अप�?रैल","मई","जून","ज�?लाई","अगस�?त","सितम�?बर","अक�?टूबर","नवम�?बर","दिसम�?बर"};
	List<Integer> imm_list = new ArrayList<Integer>(); 
	String longMessage="",sql="";
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.immun_entry);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
      //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));

		//((TextView)findViewById(R.id.tvlblMname)).setTypeface(face);
        //((TextView)findViewById(R.id.tvDob)).setTypeface(face);
        //((TextView)findViewById(R.id.tvCstat)).setTypeface(face);
        //((TextView)findViewById(R.id.tvMstat)).setTypeface(face);
        //((TextView)findViewById(R.id.tvPob)).setTypeface(face);
        //((Button)findViewById(R.id.btnSaveBirth)).setTypeface(face);
        //rbHome.setTypeface(face);
        //rbHosp.setTypeface(face);
        dtp=(DatePicker)findViewById(R.id.dtpDoi);
        dtp.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        


        
        tvMname=(TextView)findViewById(R.id.tvMname);
        tvSlno=(TextView)findViewById(R.id.slno);
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	slno = extras.getInt("id");
        	tvSlno.setText(String.valueOf(slno));
        	tvMname.setText(extras.getString("mname"));
        	//change=extras.getBoolean("change");
        }
        mydb=DBAdapter.getInstance(getApplicationContext());

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
		}*/
	    Cursor tmpc;
	    tmpc=mydb.getPregInfo(slno);
	    String dob=tmpc.getString(tmpc.getColumnIndex("dob"));
	    cstat=tmpc.getInt(tmpc.getColumnIndex("c_stat"));
	    //if (cstat==1) btn
	    String dt_str[]=dob.split("\\-");
	    mc=Calendar.getInstance();
	    mc.set(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]));
	    mc=DBAdapter.removeTime(mc);
	    tmpc = mydb.getImmunList(); 

	    // make an adapter from the cursor
	    String[] from = new String[] {"descr"};
	    int[] to = new int[] {android.R.id.text1};
	    SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, tmpc, from, to);
	    sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	    Spinner spin = (Spinner) this.findViewById(R.id.spnImm);
	    spin.setAdapter(sca);
	    LinearLayout llPimm=(LinearLayout)findViewById(R.id.llPimm);
        addListenerOnButton();
        SharedPreferences prefs=PreferenceManager
				.getDefaultSharedPreferences(this);
		uid=prefs.getString("id", "1");
		send_sms=prefs.getBoolean("send_sms", false);
        modified=true;
        Calendar defCalendar = Calendar.getInstance();
        String s=mydb.getImmun(slno);
        imm_list.clear();
        //Toast.makeText(getBaseContext(), "t:"+s, Toast.LENGTH_LONG).show();
        String imm_arr[]=s.split("\\|");
        Context ctx=getApplicationContext();
        llPimm.removeAllViews();
        for(int i=1;i<imm_arr.length;i++)
		{
			String tarr[]=imm_arr[i].split("\\:");
			imm_list.add(Integer.parseInt(tarr[0]));
			TextView tv=new TextView(ctx);
			tv.setTextSize(15);
			tv.setText(tarr[1]+" "+tarr[2]);
			tv.setTextColor(Color.BLUE);
			llPimm.addView(tv);
		}
        //mc=Calendar.getInstance();
    	
    	dtp.init(defCalendar.get(Calendar.YEAR), defCalendar.get(Calendar.MONTH), defCalendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

		    public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		    	modified=true;
		    	 if(isDateInvalid(view)){
			        	Toast.makeText(getBaseContext(), "Invalid date ", Toast.LENGTH_SHORT).show();
			            Calendar mCalendar = Calendar.getInstance();
			            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
			        }
		    }
		});
    		
    }
	
	private void addListenerOnButton() {
				
		Button btnSave = (Button) findViewById(R.id.btnSaveBirth);
		btnSave.setEnabled(cstat!=1);
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				openPreviewDialog();
				// TODO Auto-generated method stub
				/*
				int mstat=0,cstat=0,pob;
				NumberPicker npWeight=(NumberPicker)findViewById(R.id.npWeight);
				double weight=npWeight.getValue();
				String longMessage;
				Context ctx=arg0.getContext();
				if (rbHosp.isChecked()) pob=1;else pob=0;
				//mstat=(int)spnMstat.getSelectedItemId();
				//cstat=(int)spnCstat.getSelectedItemId();
				if (change)
					if (pob!=p_pob) modified=true;
					//if ((pob!=p_pob) || (mstat!=p_mstat) || (cstat!=p_cstat)) modified=true;
				//EditText edtName=(EditText)findViewById(R.id.edtName);
				if (modified)
					{
					String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
					mydb.regChild(slno, pdte, pob, (float) weight, cstat,mstat);
					String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
					String phoneNumber=getString(R.string.smsno);
					if (change)
						longMessage=String.format("%s:CM:%d:%d:%d:%d:%s:",pktHeader,slno,pob,mstat,cstat,pdte);						
					else	
						longMessage=String.format("%s:CA:%d:%d:%d:%d:%s:",pktHeader,slno,pob,mstat,cstat,pdte);
					if (send_sms) sendSMS(phoneNumber, longMessage);
					Toast.makeText(ctx,"जन�?म की सूचना बदल/सेव हो गयी", Toast.LENGTH_SHORT).show();
					}
				finish();*/
			}
		});
	}

	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage("Think again! Do you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}	
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
    	
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
        if(tempCalendar.after(mCalendar)|| tempCalendar.before(mc))
            return true;
        else 
            return false;
        
    }	
	

void openPreviewDialog(){
	
	Context ctx=getApplicationContext();
	//if (rbHosp.isChecked()) pob="अस�?पताल";else pob="घर";
	//if (rbBoy.isChecked()) sex="लड़का";else sex="लड़की";
	//final String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
    AlertDialog.Builder customDialog 
     = new AlertDialog.Builder(Immun_entry.this);
    customDialog.setTitle("Attention!");
   // Context ctx=getApplicationContext();
    LayoutInflater layoutInflater 
 = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 View view=layoutInflater.inflate(R.layout.preg_infodlg,null);
 LinearLayout llContent=(LinearLayout)view.findViewById(R.id.llContent);
 llContent.removeAllViews();
	TextView tvH=new TextView(ctx);
	tvH.setText("Name: "+tvMname.getText());
	tvH.setTextSize(18);
	tvH.setTextColor(Color.BLUE);
	TextView tvImmdt=new TextView(ctx);
	tvImmdt.setText(String.format("Date: %d-%s-%d",dtp.getDayOfMonth(),DBAdapter.hmstr[dtp.getMonth()],dtp.getYear()));
	tvImmdt.setTextSize(18);
	tvImmdt.setTextColor(Color.BLUE);
	TextView tvimmsel=new TextView(ctx);
	Spinner spnsel=(Spinner)findViewById(R.id.spnImm);
	int sel=(int) spnsel.getSelectedItemId();
	
	String pdte=String.format(Locale.getDefault(), "%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
	String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
	if (imm_list.contains(sel))
		{ 
		longMessage=String.format("%s:IM:%d:%d:%s",pktHeader,slno,sel,pdte);
		sql=String.format("update imm_det set imdt=\"%s\" where im_id=%d and pid=%d", pdte,sel,slno);
		}
	else {
		longMessage=String.format("%s:IA:%d:%d:%s",pktHeader,slno,sel,pdte);
		sql=String.format("insert into imm_det(im_id,pid,imdt) values(%d,%d,\"%s\")",sel,slno,pdte);
	}
//	String msg=(String) spnsel.getSelectedItem();
	tvimmsel.setText("Vaccine :"+mydb.imm_txt(sel));
	tvimmsel.setTextSize(18);
	tvimmsel.setTextColor(Color.BLUE);
	llContent.addView(tvH);	
	llContent.addView(tvimmsel);
	llContent.addView(tvImmdt);
	
	
	
 customDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

 
  public void onClick(DialogInterface arg0, int arg1) {
   // TODO Auto-generated method stub
	  
	  //String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
	  
		//int mstat=0,cstat=0,pob;
		//String sex;
		//NumberPicker npWeight=(NumberPicker)findViewById(R.id.npWeight);
		//double weight=npWeight.getValue();
		//Context ctx=getApplicationContext();
//		if (rbHosp.isChecked()) pob=1;else pob=0;
//		if (rbBoy.isChecked()) sex="B";else sex="G";
		//mstat=(int)spnMstat.getSelectedItemId();
		//cstat=(int)spnCstat.getSelectedItemId();
		/*
		if (change) //if (pob!=p_pob) modified=true;
			if ((pob!=p_pob) || (p_sex!=sex) || (p_weight!=weight)) modified=true;
		
		//EditText edtName=(EditText)findViewById(R.id.edtName);
		if (modified)
			{
			String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
		//	mydb.regChild(slno, pdte, pob, (float) weight, cstat,mstat,sex);
			String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
			
			if (change)
				longMessage=String.format("%s:CM:%d:%d:%d:%d:%s:%s",pktHeader,slno,pob,mstat,cstat,pdte,sex);						
			else	
				longMessage=String.format("%s:CA:%d:%d:%d:%d:%s:%s",pktHeader,slno,pob,mstat,cstat,pdte,sex);*/
	Context ctx=getApplicationContext();
	  String phoneNumber=getString(R.string.smsno);
			if (send_sms) mydb.sendSMS(phoneNumber, longMessage);
			Toast.makeText(ctx,"Vaccination details saved", Toast.LENGTH_SHORT).show();
		mydb.custom_qry(sql);
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
