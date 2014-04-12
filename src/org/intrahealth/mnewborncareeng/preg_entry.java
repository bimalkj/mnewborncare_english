package org.intrahealth.mnewborncareeng;

import java.io.File;
//import java.io.IOException;
import java.util.Calendar;



import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
//import android.database.SQLException;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
//import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class preg_entry extends Activity {
	int slno=0;
	private static int TAKE_PICTURE = 0;

	private DBAdapter mydb;
	boolean recStarted = false,modified=false;
	//Typeface face;
	//final AudioRecorder recorder = new AudioRecorder("tmp/test.3gp");
	MediaRecorder recorder;
	MediaPlayer mediaPlayer,beep_mp;
	String audioFile="",pname="",plmp;
	ProgressBar prg;
	MediaPlayer aplayer;
	RecordAmplitude recordAmplitude;
	boolean change=false,isRecording = false,send_sms=false;
	TextView tv,tvId;
	ImageButton btnRec,btnPhoto;
	Button btnSave;
	String phoneNumber;
	public static String q_msg="",uid;
	DatePicker dtp;
	Calendar mc;
	File path;
	EditText edtName;
	InputMethodManager imm;
	//public static final String hmstr[]={"जनवरी","फरवरी","मार�?च","अप�?रैल","मई","जून","ज�?लाई","अगस�?त","सितम�?बर","अक�?टूबर","नवम�?बर","दिसम�?बर"};

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.preg_entry);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
	    btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
	    btnRec = (ImageButton) findViewById(R.id.btnRec);
	    btnSave = (Button) findViewById(R.id.btnSave);
	    tvId=(TextView)findViewById(R.id.slno);
        //face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);
        //TextView tvMname=(TextView)findViewById(R.id.tvMname);
        //tvMname.setTypeface(face);
        phoneNumber=getString(R.string.smsno);
        DatePicker dtPicker=(DatePicker)findViewById(R.id.dtpLmp);
        //ViewGroup childpicker;
        //childpicker=(ViewGroup)findViewById(Resources.getSystem().getIdentifier("month", "id", "android"));
        //EditText tv1=(EditText)childpicker.findViewById(Resources.getSystem().getIdentifier("datepicker_input", "id", "android"));
        //tv1.setTextColor(Color.BLUE);;
        SharedPreferences prefs=PreferenceManager
				.getDefaultSharedPreferences(this);
		uid=prefs.getString("id", "1");
		send_sms=prefs.getBoolean("send_sms", false);
        dtPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        mc=Calendar.getInstance();
        //mc.add(Calendar.DAY_OF_MONTH,-285);
        mc.add(Calendar.MONTH, -13);
        Calendar defCalendar = Calendar.getInstance();
        dtPicker.init(defCalendar.get(Calendar.YEAR), defCalendar.get(Calendar.MONTH), defCalendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

		    public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		        if(isDateInvalid(view)){
		        	Toast.makeText(getBaseContext(), "Invalid date", Toast.LENGTH_SHORT).show();
		            Calendar mCalendar = Calendar.getInstance();
		            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
		        }
		    }
		});
        
		path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
    			+ "/mcare/pdata/");
    	if (!path.exists()) path.mkdirs();	

        Bundle extras = getIntent().getExtras();
        mediaPlayer = new MediaPlayer();
        recorder = new MediaRecorder();
        beep_mp=MediaPlayer.create(getApplicationContext(), R.raw.beep7);
        try {
			beep_mp.prepare();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
        
        if (extras!= null) {
        	change=extras.getBoolean("change");
        	slno = extras.getInt("slno");
        }
        tv = (TextView)findViewById(R.id.tvRecMon);
        prg=(ProgressBar)findViewById(R.id.prgLvlVol);
        tv.setText("..");
        tvId.setText(String.valueOf(slno));
        
        //ImageButton v = (ImageButton)findViewById(R.id.btnStart);
        //View v = findViewById(R.id.btnStart);
         //v.setOnClickListener(this);
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
	    edtName=(EditText)findViewById(R.id.edtName);	    
        edtName.setSelectAllOnFocus(true);
        imm = (InputMethodManager)getSystemService(
        	      Context.INPUT_METHOD_SERVICE);
        	
	    if (change)
    	{
    		Cursor c=mydb.getPreg(slno);
    		pname=c.getString(c.getColumnIndex("name"));
    		plmp=c.getString(c.getColumnIndex("lmp"));
    		edtName.setText(pname);
    	    edtName.addTextChangedListener(new TextWatcher(){
    	        public void afterTextChanged(Editable s) {}
    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	        public void onTextChanged(CharSequence s, int start, int before, int count){ modified=true; }
    	    }); 
    		String dt_str[]=plmp.split("\\-");
    		dtPicker.init(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]), new OnDateChangedListener() {
    			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    				modified=true;
    		        if(isDateInvalid(view)){
    		        	Toast.makeText(getBaseContext(), "Invalid date", Toast.LENGTH_SHORT).show();
    		            Calendar mCalendar = Calendar.getInstance();
    		            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
    		        }
    			}
    		});
    	} else
    	{
    	    edtName.setText("* Enter name  *");
    	}


    	
	    addListenerOnButton();
    }
	
	@Override
	public void onStop()
	{
		
		recorder.release();
		mediaPlayer.release();
		//mydb.close();
		super.onStop();
	}
	
	
	@Override
	public void onResume()
	{
		
		if (mediaPlayer==null)
			mediaPlayer = new MediaPlayer();
		if (recorder==null)
			recorder=new MediaRecorder();
		super.onResume();
	}
	
	private void addListenerOnButton() {
		
		
		//btnSave.setTypeface(face);
   	 
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//String longMessage;
				//String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
				//Context ctx=arg0.getContext();
				dtp=(DatePicker)findViewById(R.id.dtpLmp);
				edtName=(EditText)findViewById(R.id.edtName);
				imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
				openPreviewDialog();
				/*
				if (change)
				{
					if (modified)
					{
					mydb.updatePreg(slno,edtName.getText().toString(), pdte);				
					Toast.makeText(ctx,"जानकारी बदल गयी", Toast.LENGTH_SHORT).show();
					//Toast.makeText(ctx,longMessage, Toast.LENGTH_LONG).show();
					longMessage=String.format("%s:PM:%d:%s:%s:",pktHeader,slno,edtName.getText(),pdte);
					if (send_sms) sendSMS(phoneNumber, longMessage);
					} else Toast.makeText(ctx,"No change detected ..", Toast.LENGTH_SHORT).show();
						
				} else {
					longMessage=String.format("%s:PA:%d:%s:%s:",pktHeader,slno,edtName.getText(),pdte);
					mydb.insertPreg(edtName.getText().toString(), pdte);				
					Toast.makeText(ctx,"पंजीकरण हो गया", Toast.LENGTH_SHORT).show();
					if (send_sms) sendSMS(phoneNumber, longMessage);
				}
				*/
				
				
				//SmsManager sms = SmsManager.getDefault();
				//sms.sendTextMessage(phoneNumber, null, longMessage, null, null);
				//ArrayList<String> parts = sms.divideMessage(longMessage);
				//sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
				
			}
		});
		
		
	   	 
		btnPhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Environment.getExternalStorageDirectory()+"/mcare/pdata/",
						String.valueOf(slno)+ ".jpg");
				if (file.exists()) Toast.makeText(arg0.getContext(),"Overwriting photo ..", Toast.LENGTH_SHORT).show();
				Uri outputFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent,TAKE_PICTURE);			
			}
			
			
			
		});
	   	 
		btnRec.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
		    		if (!recStarted) 
		    		{
		    		//---------------------------
		    		beep_mp.start();
		    		if (recorder!=null) recorder.release();
		    		recorder=new MediaRecorder();
		    		recorder.reset();	
		    	    	recorder.setOnErrorListener(new OnErrorListener(){

		    				public void onError(MediaRecorder arg0, int arg1, int arg2) {
		    					// TODO Auto-generated method stub
		    					tv.setText("Finished err");
		    				}
		    	    		
		    	    	});
		    	    	
		    	    	recorder.setOnInfoListener(new OnInfoListener() {
		    	    	    public void onInfo(MediaRecorder mr, int what, int extra) {                     
		    	    	        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
		    	    	        	btnRec.setImageResource(R.drawable.crimson);
		    	    	        	tv.setText("Finished ..");
		    	    	        	isRecording = false;
		    	    	        	recordAmplitude.cancel(true);
		    	    	        	recorder.stop();
		    	    	        	recorder.reset();
		    	    	        	//recorder.release();
		    	    	        }          
		    	    	    }
		    	    	});

		    	    	
		    	    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    	    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    	    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    	    	//File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
		    	    	//		+ "/mcare/pdata/");
		    	    	//if (!path.exists()) path.mkdirs();
		    	    	
		    	    	//audioFile = File.create("recording", ".3gp", path);
		    	    	try {	
		    	    	audioFile=path+"/"+String.valueOf(slno)+".3gp";
		    	    	recorder.setOutputFile(audioFile);
		    	    	recorder.setMaxDuration(5000);
		    	    	recorder.prepare();
		    	    	} catch (IllegalStateException e) {
		    	    	throw new RuntimeException(
		    	    	"IllegalStateException on MediaRecorder.prepare", e);
		    	    	} catch (Exception e) {
		    	    	throw new RuntimeException(
		    	    	"Exception on MediaRecorder.prepare", e);
		    	    	}
		    	    	tv.setText("Recording..");
		    	    	recorder.start();
		    	    	isRecording = true;
		    	    	recordAmplitude = new RecordAmplitude();
		    	    	recordAmplitude.execute();
		    	    	

		    		//---------------------------	
		    			
		    			recStarted=true;
		    			btnRec.setImageResource(R.drawable.green);
		    			Toast.makeText(getApplicationContext(),"Recording started", Toast.LENGTH_SHORT).show();
		    		}
		    		else 
		    			{
		    			btnRec.setImageResource(R.drawable.crimson);
		    			recStarted=false;
		    			isRecording = false;
		    	    	if (isRecording) {
		    	    		tv.setText("Finished ..");
		    	    			try {
		    	    				recorder.stop();
		    	    				recordAmplitude.cancel(true);	
		    	    				//recorder.stop();
		    	    				recorder.reset();
		    	    				//recorder.release();
		    	    				} catch (Exception e) {
		    	    					e.printStackTrace();
		    	    				}
		    	    			Toast.makeText(getApplicationContext(),"Recording finished", Toast.LENGTH_SHORT).show();
		    				}
		    			}
			}
		});

	
		
		ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
	   	 
		btnPlay.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				audioFile=path+"/"+String.valueOf(slno)+".3gp";	
		    		if (new File(audioFile).exists())
		    		{
		    			try {
		    				if (aplayer==null) aplayer=new MediaPlayer();
		    				aplayer.reset();
			    			aplayer.setDataSource(audioFile);
			    			aplayer.prepare();
			    			aplayer.start();
		    				
		    			//MediaPlayer mediaPlayer = new MediaPlayer();
		    			//mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/tmp/test.3gp");
		    			//if (mediaPlayer.isPlaying())
		    			/*	mediaPlayer.reset();
		    			mediaPlayer.setDataSource(audioFile);
		    			mediaPlayer.prepare();
		    			mediaPlayer.start();*/
		    				} catch (Exception e) {
		    			// TODO Auto-generated catch block
		    					e.printStackTrace();
		    			}
		    		} else Toast.makeText(getApplicationContext(),"Not recorded yet "+audioFile, Toast.LENGTH_SHORT).show();
				}
		});
		
	}


    private class RecordAmplitude extends AsyncTask<Void, Integer, Void> {
    	int ma=0;
    	@Override
    	protected Void doInBackground(Void... params) {
    	while (isRecording) {
    	try {
        	ma=recorder.getMaxAmplitude();
    		Thread.sleep(500);	
    	} catch (Exception e) {
    	//e.printStackTrace();
    	}
    	publishProgress(ma);
    	}
    	return null;
    }
    	protected void onProgressUpdate(Integer... progress) {
    		
    		float max=30000,fpos=progress[0];
    		if (fpos>max) fpos=max;
    		int pos=(int)((fpos/max)*100);
    		//tv.setText(progress[0].toString()+" "+String.valueOf(pos));
    		prg.setProgress(pos);
    		}
 } 
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage("Think again! Do you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   mydb.close();
	            	   beep_mp.release();
	            	   mediaPlayer.release();
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
		Toast.makeText(this,message, Toast.LENGTH_LONG).show();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
*/	
private boolean isDateInvalid(DatePicker tempView) {
    	
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
        if(tempCalendar.after(mCalendar) || tempCalendar.before(mc))
            return true;
        else 
            	return false;
        
    }	

void openPreviewDialog(){
	
	final String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
    AlertDialog.Builder customDialog 
     = new AlertDialog.Builder(preg_entry.this);
    customDialog.setTitle("Attention!");
    Context ctx=getApplicationContext();
    LayoutInflater layoutInflater 
 = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 View view=layoutInflater.inflate(R.layout.preg_infodlg,null);
 LinearLayout llContent=(LinearLayout)view.findViewById(R.id.llContent);
 llContent.removeAllViews();
	TextView tvH=new TextView(ctx);
	tvH.setText("Name: "+edtName.getText());
	tvH.setTextSize(18);
	tvH.setTextColor(Color.BLUE);
	TextView tvEdd=new TextView(ctx);
	tvEdd.setText(String.format("LMP: %d-%s-%d",dtp.getDayOfMonth(),DBAdapter.hmstr[dtp.getMonth()],dtp.getYear()));
	tvEdd.setTextSize(18);
	tvEdd.setTextColor(Color.BLUE);
	llContent.addView(tvH);
	llContent.addView(tvEdd);
  
 customDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

 
  public void onClick(DialogInterface arg0, int arg1) {
   // TODO Auto-generated method stub
	  
	  String pdte=String.format("%04d-%02d-%02d",dtp.getYear(),dtp.getMonth()+1,dtp.getDayOfMonth());
	  String longMessage;
		if (change)
		{
			if (modified)
			{
			mydb.updatePreg(slno,edtName.getText().toString(), pdte);				
			Toast.makeText(getApplicationContext(),"Details modified", Toast.LENGTH_SHORT).show();
			//Toast.makeText(ctx,longMessage, Toast.LENGTH_LONG).show();
			longMessage=String.format("%s:PM:%d:%s:%s",pktHeader,slno,edtName.getText(),pdte);
			if (send_sms) mydb.sendSMS(phoneNumber, longMessage);
			} else Toast.makeText(getApplicationContext(),"No change detected ..", Toast.LENGTH_SHORT).show();
				
		} else {
			longMessage=String.format("%s:PA:%d:%s:%s",pktHeader,slno,edtName.getText(),pdte);
			mydb.insertPreg(edtName.getText().toString(), pdte);				
			Toast.makeText(getApplicationContext(),"Registration completed", Toast.LENGTH_SHORT).show();
			if (send_sms) mydb.sendSMS(phoneNumber, longMessage);
		}
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
