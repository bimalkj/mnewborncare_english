package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



import org.intrahealth.mnewborncareeng.control.NumberPicker;

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
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
//import android.database.SQLException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
//import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Quest extends Activity {
	int id=0,yind,nind,nqid,qhid,qtype=-1,y,n,grp=0,attch_stat=0,seq=0,pid,pqid=0;
	boolean branch=false,grp_finished=false,fvisit=false,ans_clicked=true;
	//private static int TAKE_PICTURE = 1;
	//private int qind=0;
	private DBAdapter mydb;
	//Typeface face;
	Button btnQuest,btnBack,btnNext;
	RadioButton rbYes,rbNo;
	TextView tvNumInput;
	LinearLayout llNumInput,llspninput,llPans,llStopwatch;
	NumberPicker npNumInput;
	TextView tvHead;
	Spinner spnMchInput;
	ImageButton btnHelp;
	MediaPlayer mp,beep_mp;
	RadioGroup rg;
	List<String> mchlist;
	private ArrayAdapter<String> adapter;
	Cursor c;
	String ansstr="",hv_str="";
	StringBuilder  gsumm=new StringBuilder("------");
	double ansarr[]=new double[90];
	//String grpinfo[]={"f'k'kq ds tkap","ek¡ fd tkap","thok.kq laØe.k tkap","nLrjksx fd tkap",
	//		"Lruiku lEcaf/kr tkap","Vhdkdj.k lEcaf/kr tkap"};
	String grpinfo[]={"Newborn check-up","Mother's check-up","Bacterial infection check-up","Diarrhea check-up","BreastFeeding check-up"};
	//String vst_seq[]={"¼1½ igyh tkap","¼2½ nwljh tkap","¼3½ rhljh tkap","¼4½ pkSFkh tkap","¼5½ ikapoh tkap","¼6½ Ìëh tkap","¼7½ lkroh tkap"};
	String vst_seq[]={"1. First check-up","2. Second check-up","3. Third check-up","4. Fourth check-up","5. Fifth check-up","6. Sixth check-up","7. Seventh check-up"};

	Context ctx;
	int click_cnt=1,dstat=0;	
	boolean srun=false,stp_watch=false,learn=false,cbranch=false,send_sms=false;
    private Button timeView;
    private int hour = 0;
    private int min = 0;
    private int sec = 0;
    String mTimeFormat = "%02d:%02d:%02d",uid;
    char ccatgr = 'G';


	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.quest);
	    tvHead=(TextView)findViewById(R.id.tvHead);
	    npNumInput=(NumberPicker)findViewById(R.id.npNumInput);
	    spnMchInput=(Spinner)findViewById(R.id.spnMchInput);
	    llPans=(LinearLayout)findViewById(R.id.llPans);
	    llStopwatch=(LinearLayout)findViewById(R.id.llStopwatch);
	    
	    LinearLayout llBase=(LinearLayout)findViewById(R.id.llBase);
      //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);
        ctx=this;
		String mch="One$Two$Three";
		//mchlist=new ArrayList(Arrays.asList(mch.split("\\$")));
		mchlist=new ArrayList<String>(Arrays.asList(mch.split("\\$")));
		for(int i=0;i<90;i++) ansarr[i]=-1;
        btnQuest=(Button)findViewById(R.id.btnQuest);
        //btnQuest.setTypeface(face);
        SharedPreferences prefs=PreferenceManager
				.getDefaultSharedPreferences(this);
		uid=prefs.getString("id", "1");
		send_sms=prefs.getBoolean("send_sms", false);        
        
        rg=(RadioGroup)findViewById(R.id.rdgpYesNo);
        rg.setVisibility(android.view.View.GONE);
        //((RadioButton)findViewById(R.id.rbYes)).setTypeface(face);
        //((RadioButton)findViewById(R.id.rbNo)).setTypeface(face);
        //Uri path = Uri.parse("android.resource://" + getPackageName() + "/raw/h.3gp");
        
        beep_mp=MediaPlayer.create(getApplicationContext(), R.raw.beep7);
        try {
			beep_mp.prepare();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
        
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	id = extras.getInt("id");
        	hv_str=extras.getString("hv_str");
        	seq=extras.getInt("seq");
        	pid=extras.getInt("pid");
        	learn=extras.getBoolean("learn");
        	dstat=extras.getInt("dstat");
        	fvisit=extras.getBoolean("fvisit");
        }
        
        switch (seq) {
		case 1:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv1);break;
		case 2:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv2);break;
		case 3:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv3);break;
		case 4:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv4);break;
		case 5:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv5);break;
		case 6:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv6);break;
		case 7:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv7);break;
		default:mp=MediaPlayer.create(getApplicationContext(), R.raw.hv1);break;
		}
        if (!fvisit)  mp.start();
        if (learn) llBase.setBackgroundResource(R.drawable.btn_default_normal_disable_focused);
        else llBase.setBackgroundResource(R.color.clrOffwhite);
        if (!fvisit)
        	btnQuest.setText(vst_seq[seq-1]);
        else btnQuest.setText("Optional visit");
        //TextView txtRowId=(TextView)findViewById(R.id.txtRowId);
        //txtRowId.setText(String.valueOf(slno));
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
        addListenerOnButton();
    }
	
	private void addListenerOnButton() {
		
		btnBack = (Button)findViewById(R.id.btnBack);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnHelp=(ImageButton)findViewById(R.id.btnHelp);
		rbYes=(RadioButton)findViewById(R.id.rbYes);
		rbNo=(RadioButton)findViewById(R.id.rbNo);
		//npNumInput=(NumberPicker)findViewById(R.id.npNumInput);
		llNumInput=(LinearLayout)findViewById(R.id.llnuminput);
		llspninput=(LinearLayout)findViewById(R.id.llspninput);
		tvNumInput=(TextView)findViewById(R.id.tvNumInput);
		llNumInput.setVisibility(android.view.View.INVISIBLE);
		llspninput.setVisibility(android.view.View.INVISIBLE);
		btnBack.setVisibility(android.view.View.INVISIBLE);
		btnNext.setBackgroundResource(R.drawable.custom_button_yellow);
		//tvNumInput.setTypeface(face);
		//btnNext.setTypeface(face);
		//btnBack.setTypeface(face);
		//rbYes.setTypeface(face);
		//rbNo.setTypeface(face);
		nqid=0;
		if (dstat==2) nqid=DBAdapter.qc[0];
		c=mydb.getNextQuest(nqid+1,branch,dstat);		
		yind=c.getColumnIndex("y_qid");
		nind=c.getColumnIndex("n_qid");	
		adapter  = new ArrayAdapter<String>(
	            this,android.R.layout.simple_spinner_item,mchlist);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spnMchInput.setAdapter(adapter);
	    spnMchInput.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				ans_clicked=true;
				return false;
			}
		});
	    /*
	    spnMchInput.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ans_clicked=true;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    */
	    
	    rbYes.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				click_cnt=0;ans_clicked=true;
				btnNext.setBackgroundResource(R.drawable.custom_button_yellow);
				AssetFileDescriptor afd;
				try {
					mp.reset();
					afd = getAssets().openFd("snd/haan.3gp");	
					mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					mp.prepare();
					mp.start();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});

	    rbNo.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				click_cnt=0;ans_clicked=true;
				btnNext.setBackgroundResource(R.drawable.custom_button_yellow);
				AssetFileDescriptor afd;
				try {
					mp.reset();
					afd = getAssets().openFd("snd/nahin.3gp");	
					mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					mp.prepare();
					mp.start();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
	    
	    
		btnNext.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {

				//if (qtype==0) click_cnt++;else click_cnt=2;
				//click_cnt++;
				//Play beep sound
				//ans_clicked=true;
				mp.reset();
				beep_mp.start();
				if ((npNumInput.changed)||(qtype==3)) ans_clicked=true;
				if (ans_clicked)
				{
				if (qtype==3) click_cnt=2; else click_cnt++;
				if (click_cnt==1) {
					btnNext.setBackgroundResource(R.drawable.custom_button_green);
					
				} else if (click_cnt==2) next_quest();
				} else Toast.makeText(getApplicationContext(),"Please select an answer", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnQuest.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {				
				AssetFileDescriptor afd;
				try {
					mp.reset();
					afd = getAssets().openFd("snd/"+String.valueOf(nqid)+"qa.3gp");	
					mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					mp.prepare();
					mp.start();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
			if (llPans.getVisibility()==android.view.View.INVISIBLE)
			{	
				String pans=mydb.getpans(pid, nqid);
				String seq_arr[]=mydb.seq_str.split("\\ ");
				String pans_arr[]=pans.split("\\ ");
				llPans.removeAllViews();
				llPans.setBackgroundResource(R.drawable.custom_button_brown);
				TextView tvH=new TextView(ctx);
				tvH.setText(" Last visit: response");
				//tvH.setTypeface(face);
				tvH.setTextSize(15);
				llPans.addView(tvH);
				for(int i=1;i<pans_arr.length;i++)
				{
					TextView tv=new TextView(ctx);
					tv.setTextSize(15);
					if (qtype==0) { 
						String st=" ";
						if (pans_arr[i].contentEquals("1")) st="Yes";
						else if (pans_arr[i].contentEquals("0")) st="No";
						tv.setText(seq_arr[i]+". "+st); 
						//tv.setTypeface(face);
					} else {
						tv.setText(seq_arr[i]+") "+pans_arr[i]);
					}
					
					llPans.addView(tv);
				}
				llPans.setVisibility(android.view.View.VISIBLE);
			} else llPans.setVisibility(android.view.View.INVISIBLE);
		}
	});

		
		
		btnHelp.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String movieurl = Environment.getExternalStorageDirectory() + "/v3gp/"+String.valueOf(qhid)+"qv.3gp";
				if (new File(movieurl.toString()).exists())
				{
				mp.reset();	
				Intent intentToPlayVideo = new Intent(Intent.ACTION_VIEW);
				intentToPlayVideo.setDataAndType(Uri.parse(movieurl), "video/*");
				startActivity(intentToPlayVideo);
				} else
					Toast.makeText(getApplicationContext(),"Resource not available..", Toast.LENGTH_SHORT).show();
			}
		});
	
	}

	


	public void next_quest()
	{
		int cid=0,tmp_qid;
		char class_str;
		
		String qtext="";
		//qtype 0 Yes/No, 1 NumberInput 2 Spinner 3 Label
		//String att_str[]={"yxko vPNk gS","yxko vPNk ugha gS","yxko fcydqy ugha gS"};
		String att_str[]={"Good attachment","Bad attachment","No attachment"};
		String answrg0="",answrg1="",answrg2="",answrg3="",answrg4="";
		//0=good 1=not good 2=not atall
		click_cnt=0; //reset click_count for next question
		ans_clicked=false; 
		npNumInput.changed=false;
		btnNext.setBackgroundResource(R.drawable.custom_button_yellow);
		llPans.setVisibility(android.view.View.INVISIBLE);
		llStopwatch.setVisibility(android.view.View.INVISIBLE);
		nqid++;tmp_qid=nqid;
		//mp.reset();
		if (qtype==0) {
			if (rg.getCheckedRadioButtonId()==R.id.rbYes) 
				{ansarr[nqid-1]=1;if (y>0) nqid=y;}
				else {ansarr[nqid-1]=0;if (n>0) nqid=n;}
			} else if (qtype==1) {
				ansarr[nqid-1]=npNumInput.getValue();
			} else if (qtype==2) {
				ansarr[nqid-1]=(int)spnMchInput.getSelectedItemId();
			}

		
		//Stop re branching
		if (grp_finished && (tmp_qid<=(DBAdapter.qc[1]+1)) && (nqid>DBAdapter.qc[1])) nqid=tmp_qid;
		//Set branch flag in case it it not set and store the current question id 
		if ((nqid>DBAdapter.qc[1]) && (!branch) && (tmp_qid!=nqid)) { branch=true;pqid=tmp_qid;ccatgr='R'; }
		//Special re-branching for group 1
		//For question 9, if the temp >99.5 , then JUMP to Group 2 ( i.e. question 40)
		//· Q 29: If < 4 goto Q 30 (a label) , else goto Q 31.
		//· Q 31: If > 5 goto 32 (label), else goto Q 33.
		//· Q 33: if >102 goto Q 34 else if == 98.6 goto 36.
		//· Q 38: If YES goto 39 (label), else END
		
		//Q 33 the logic is if temperature is > 102 goto 34, else if temperature is > 98.6 && <101.9 goto 35, elese if temp <=98.6 goto 36
		double tval=npNumInput.getValue();
		/*if (tmp_qid==10)
			if (tval>99.5) nqid=39;else;
		else*/ 
		if (tmp_qid==28) 
			if (tval>=4) nqid=29;else;
		else if (tmp_qid==30)
			if (tval<=5) nqid=31;else;
		else if (tmp_qid==32)
			{ if ((tval>98.6)&&(tval<101.9)) nqid=33;else if (tval<=98.6) nqid=34;}
		else if (tmp_qid==37)
			if (rg.getCheckedRadioButtonId()==R.id.rbNo) nqid=38;else;
		//else if (tmp_qid==66)
		//	if (rg.getCheckedRadioButtonId()==R.id.rbNo) nqid=68;else;	
		
		//End of special re-branching condition
		c=mydb.getNextQuest(nqid,branch,dstat);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//If it is in branched state return back where left off
		/* Commented out 23 nov 2012 (no repetetion of questions)
		if(branch && (!c.moveToFirst())) 
			{
			nqid=pqid;grp_finished=true;
			branch=false;c=mydb.getNextQuest(nqid, branch);			
			};*/
		// If branched out come back to mothers question
		if(branch && (!c.moveToFirst())) 
			{
			nqid=DBAdapter.qc[0]+1;grp_finished=true;
			if (dstat==1) cbranch=true;
			branch=false;c=mydb.getNextQuest(nqid, branch,dstat);			
			};
		
		
		if(c.moveToFirst())
		{
			AssetFileDescriptor afd;
			try {
				afd = getAssets().openFd("snd/"+String.valueOf(nqid)+"qa.3gp");
				
				mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				mp.prepare();
				mp.start();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		//qtext=String.valueOf(nqid)+") "+c.getString(c.getColumnIndex("qtext"));
			qhid=nqid;int tmp_ind=c.getColumnIndex("qvid");
			if (!c.isNull(tmp_ind)) qhid=c.getInt(tmp_ind);
			qtext=c.getString(c.getColumnIndex("qtext"));
			tvHead.setText(grpinfo[c.getInt(c.getColumnIndex("grp"))]);
/*
		if (nqid==(DBAdapter.qc[0]+1)) //End of group 0 Question number 26
		{
			class_str='G';
			if (branch) class_str='R';
			gsumm.setCharAt(0, class_str);
		}
		else if (nqid==(DBAdapter.qc[1]+1)) //End of group 1 Question number 39
		{
			class_str='G';
			if ((ansarr[31]>5)||(ansarr[33]>102)||(ansarr[36]==1)||(ansarr[38]==1)) class_str='R';
			gsumm.setCharAt(1, class_str);
		}
		else*/
		if (nqid==(DBAdapter.qc[2]+1)) //End of group 2 Question number 52
			{
			class_str='G';
			cid=0;
			/*
			for(int i=0;i<53;i++) {
				if (ansarr[i]==0) ansstr=ansstr+"0 ";
				else if (ansarr[i]==1) ansstr=ansstr+"1 ";
				else ansstr=ansstr+new DecimalFormat("#.#").format(ansarr[i])+" ";
						//String.format("%.1f ", ansarr[i]);
				}
				*/
			
			//If, 41=Yes OR 42>=60 OR
			//		43=Yes OR 44=Yes OR 45=Yes OR
			//		47=Yes OR 49=Yes OR 50=Yes OR
			//		51=Yes OR 52=Yes old logic
			/*
			if ((ansarr[41]==1) || (ansarr[42]>=60) || (ansarr[43]==1)||(ansarr[44]==1)||(ansarr[45]==1)
					||(ansarr[47]==1)||(ansarr[49]==1)||(ansarr[50]==1)||(ansarr[51]==1)||(ansarr[52]==1)) { cid=1; class_str='R';}
			//If, 46=Yes OR 48=Yes old logic 
			else if((ansarr[46]==1)||(ansarr[48]==1)) { cid=2;class_str='Y';}
			*/
			//If, 39=Yes OR 40>=60 OR
			//		41=Yes OR 42=Yes OR 43=Yes OR
			//		45=Yes OR 47=Yes OR 48=Yes OR 
			//		49=Yes OR 50=Yes OR 51=Yes OR 52=Yes New logic
			if ((ansarr[39]==1) || (ansarr[40]>=60) || (ansarr[41]==1)||(ansarr[42]==1)||(ansarr[43]==1)
					||(ansarr[45]==1)||(ansarr[47]==1)||(ansarr[48]==1)||(ansarr[49]==1)||(ansarr[50]==1)
					||(ansarr[51]==1)||(ansarr[52]==1)) { cid=1; class_str='R';}
			//If, 44=Yes OR 46=Yes New logic
			else if((ansarr[44]==1)||(ansarr[46]==1)) { cid=2;class_str='Y';}

			//Toast.makeText(ctx,"Classification :"+class_str, Toast.LENGTH_LONG).show();
			gsumm.setCharAt(2, class_str);
			mp.reset();
			Intent intent = new Intent(ctx,Remedy.class);
			intent.putExtra("cid",cid);
			startActivity(intent);
			grp=c.getInt(c.getColumnIndex("grp"));
			} else if (nqid==(DBAdapter.qc[3]+1))  //End of group 3 Question no 60
			{
				class_str='G';cid=5;
				//If, 56=Yes OR 58=Yes OR 59=Yes //3
				//If, 57=Yes OR 58=Yes OR 60       =Yes //4
				
				//If, (56=Yes AND 58=Yes)
				//OR
				//(56=Yes AND 60=Yes)
				//OR
				//(58=Yes AND 60=Yes) clid=3
				
				//If,(57=Yes AND 59=Yes)
				//OR
				//(57=Yes AND 60=Yes)
				//OR
				//(59=Yes AND 60=Yes) clid=4
			
				//if 54=YES, AND 55 = NO, AND
				//		56=NO,AND 57 = NO, AND
				//		58=NO,AND 59 = NO, AND 60=NO clid=5
				
				//if 54=NO clid=10
				//(cat id - 4) - If,(57=Yes AND 59=Yes) OR (57=Yes AND 60=Yes) OR (59=Yes AND 60=Yes) 
				
			if (((ansarr[56]==1)&&(ansarr[58]==1))||((ansarr[56]==1)&&(ansarr[60]==1))||((ansarr[58]==1)&&(ansarr[60]==1))) { cid=3;class_str='R';}
			else if (((ansarr[57]==1)&&(ansarr[59]==1))||((ansarr[57]==1)&&(ansarr[60]==1))||((ansarr[59]==1)&&(ansarr[60]==1))) {cid=4;class_str='Y';}
			else if ((ansarr[54]==1)&&(ansarr[55]==0)&&(ansarr[56]==0)&&(ansarr[57]==0)&&(ansarr[58]==0)&&(ansarr[59]==0)&&(ansarr[60]==0)) {cid=5;class_str='G';}
			else if (ansarr[54]==0) {cid=10;class_str='G';}
			else if (ansarr[55]==1) {cid=6;class_str='R';}
			//Call classification and remedy
			gsumm.setCharAt(3, class_str);
			mp.reset();
			Intent intent = new Intent(ctx,Remedy.class);
			intent.putExtra("cid",cid);
			startActivity(intent);
			//Toast.makeText(ctx,"Classification :"+class_str, Toast.LENGTH_LONG).show();
				} else if (nqid==72) //For deciding attachment status  
				{
				attch_stat=0;
				//If (69 & 70 &r 71 &72)=No
				/*
				if ((ansarr[69]==0)&&(ansarr[70]==0)&&(ansarr[71]==0)&&(ansarr[72]==0)) attch_stat=2;
				else if ((ansarr[69]==0)||(ansarr[70]==0)||(ansarr[71]==0)||(ansarr[72]==0)) attch_stat=1;
				*/
				//"लगाव बिलक�?ल नहीं है = (68=NO AND 69=NO AND 70=NO AND 71=NO) " New logic
				if ((ansarr[68]==0)&&(ansarr[69]==0)&&(ansarr[70]==0)&&(ansarr[71]==0)) attch_stat=2;
				else if ((ansarr[68]==0)||(ansarr[69]==0)||(ansarr[70]==0)||(ansarr[71]==0)) attch_stat=1;				
				qtext=att_str[attch_stat];
				} else if (tmp_qid==(DBAdapter.qc[4]+1)) //End of group 4 Question no 78 
				{
				cid=9;class_str='G';
				//If 74 = "not sucking at all" OR 73="No attachment at all" OR 62=Yes //
				//If 74 = "बिलक�?ल नहीं चूसना" OR 73="लगाव बिलक�?ल नहीं है" OR 65=Yes cid=7
				
				//If Q 74 = "बिलक�?ल नहीं चूसना" OR Q 73="लगाव बिलक�?ल नहीं है" OR Q 65=Yes
				
				//If 74= "not suckling effectively" OR If 73="Not well attached" OR 64 < 8 OR 65=YES OR 75=YES OR 76=Yes
				//If 74= "प�?रभावी ढंग से नहीं चूसना" OR If 73="लगाव अच�?छा नहीं है" OR 67 < 8 OR
				//		62=YES OR 75=YES OR 78=Yes cid=8 प�?रभावी ढंग से चूसना$प�?रभावी ढंग से नहीं चूसना$बिलक�?ल नहीं चूसना old logic
				/*
				if ((ansarr[74]==2) || (attch_stat==2)||(ansarr[65]==1)) { cid=7;class_str='R';}
				else if ((ansarr[74]==1)||(attch_stat==1)||(ansarr[67]<8)||(ansarr[62]==1)||(ansarr[75]==1)||(ansarr[78]==1)) { cid=8;class_str='Y'; }					
				*/
				//If 74 = "not sucking at all" OR 73="No attachment at all" OR 62=Yes //
				//If 74 = "बिलक�?ल नहीं चूसना" OR 73="लगाव बिलक�?ल नहीं है" OR 65=Yes cid=7
				
				//If Q 74 = "बिलक�?ल नहीं चूसना" OR Q 73="लगाव बिलक�?ल नहीं है" OR Q 65=Yes
				
				//If 74= "not suckling effectively" OR If 73="Not well attached" OR 64 < 8 OR 65=YES OR 75=YES OR 76=Yes
				//If 74= "प�?रभावी ढंग से नहीं चूसना" OR If 73="लगाव अच�?छा नहीं है" OR 67 < 8 OR
				//		62=YES OR 75=YES OR 78=Yes cid=8 प�?रभावी ढंग से चूसना$प�?रभावी ढंग से नहीं चूसना$बिलक�?ल नहीं चूसना old logic
				
				//If 73 = "बिलक�?ल नहीं चूसना" OR [72="लगाव बिलक�?ल नहीं है = (68=NO AND 69=NO AND 70=NO AND 71=NO) "] 
				//		OR 64=Yes
				//If 73= "प�?रभावी ढंग से नहीं चूसना" OR If [72=" लगाव अच�?छा नहीं है = (68=NO OR 69=NO OR 70=NO OR 71=NO)"] 
				//		OR 66 < 8 OR 62=YES OR 74=YES OR 75=Yes  OR 76=Yes New logic

				if ((ansarr[73]==2) || (attch_stat==2)||(ansarr[64]==1)) { cid=7;class_str='R';}
				else if ((ansarr[73]==1)||(attch_stat==1)||(ansarr[66]<8)||(ansarr[62]==1)||(ansarr[74]==1)||(ansarr[75]==1)||(ansarr[76]==1)) { cid=8;class_str='Y'; }					

				gsumm.setCharAt(4, class_str);
				mp.reset();
				Intent intent = new Intent(ctx,Remedy.class);
				intent.putExtra("cid",cid);
				startActivity(intent);						
				}
		if (!c.isNull(yind)) y=c.getInt(yind);else y=-1;
		if (!c.isNull(nind)) n=c.getInt(nind);else n=-1;				
		btnQuest.setText(qtext);
		if (c.getInt(c.getColumnIndex("help"))==1)
			btnHelp.setVisibility(android.view.View.VISIBLE);
		else btnHelp.setVisibility(android.view.View.GONE);
		if (c.getInt(c.getColumnIndex("pans"))==1)
			btnBack.setVisibility(android.view.View.VISIBLE);
		else btnBack.setVisibility(android.view.View.INVISIBLE);
		
		qtype=c.getInt(c.getColumnIndex("q_type"));
		//if (qtype!=0) ans_clicked=true;
		double def=c.getDouble(c.getColumnIndex("def"));
		if (def==0) {
				//rbNo.setChecked(true);
				rbNo.setTextColor(Color.parseColor("#669900"));
				rbYes.setTextColor(Color.RED);
			} else 
			{
				//rbYes.setChecked(true);
				rbYes.setTextColor(Color.parseColor("#669900"));
				rbNo.setTextColor(Color.RED);
			}
		if(qtype==0) {
			
			//rbNo.setChecked(false);rbYes.setChecked(false);
			rg.clearCheck();
			btnQuest.setBackgroundResource(R.drawable.custom_button_lblue);
			rg.setVisibility(android.view.View.VISIBLE);
			llNumInput.setVisibility(android.view.View.GONE);
			llspninput.setVisibility(android.view.View.GONE);
		}
		else if (qtype==2) {
			btnQuest.setBackgroundResource(R.drawable.custom_button_lblue);
			llspninput.setVisibility(android.view.View.VISIBLE);
			rg.setVisibility(android.view.View.GONE);
			llNumInput.setVisibility(android.view.View.GONE);
			if (!c.isNull(c.getColumnIndex("oinfo"))) {
				String mstr=c.getString(c.getColumnIndex("oinfo"));
				String mcharr[]=mstr.split("\\$");
				mchlist.clear();
				for(int i=0;i<mcharr.length;i++)
				mchlist.add(mcharr[i]);
				//mchlist.addAll(mstr.split("\\$"));
				//mchlist=new ArrayList(Arrays.asList(mstr.split("\\$")));
				
				adapter.notifyDataSetChanged();
				spnMchInput.setSelection(0);
				//Toast.makeText(getApplicationContext(),"List changed", Toast.LENGTH_SHORT).show();
				}
			
		} else 	if (qtype==1) {
			btnQuest.setBackgroundResource(R.drawable.custom_button_lblue);
			llNumInput.setVisibility(android.view.View.VISIBLE);
			rg.setVisibility(android.view.View.GONE);
			llspninput.setVisibility(android.view.View.GONE);
			tvNumInput.setText(c.getString(c.getColumnIndex("oinfo")));
			npNumInput.setValue(def);
			//if (nqid==8) npNumInput.setValue(0); //2.5
			if ((nqid==9)||(nqid==31)) npNumInput.setIncrVal(0.1);
			else if ((nqid==27)||(nqid==29)) {npNumInput.setValue(0);npNumInput.setIncrVal(1);}
			//else if (nqid==29) {npNumInput.setValue(0);npNumInput.setIncrVal(1);}
			else if (nqid==40) 
				{
				//Set numeric input for breath counting
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				stp_watch=true;
				npNumInput.setValue(0);//50
				npNumInput.setIncrVal(1);
				llStopwatch.removeAllViews();
				timeView=new Button(ctx);
				timeView.setBackgroundResource(R.drawable.custom_button_blue);
				timeView.setText("00:00:00");
				timeView.setTextSize(30);
				
				timeView.setTextColor(Color.WHITE);
				timeView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						srun=!srun;
						if (srun)
						{
							mp.reset();
							beep_mp.start();
							mHandler.postDelayed(mUpdateTime, 1000);
						} else {
							sec=0;
						}
					}
				});
				llStopwatch.addView(timeView);
				llStopwatch.setVisibility(android.view.View.VISIBLE);
				}
			//else if (nqid==63) {npNumInput.setValue(0);npNumInput.setIncrVal(1);}
			//else if (nqid==67) {npNumInput.setValue(0);npNumInput.setIncrVal(1);}
		} else {
			btnQuest.setBackgroundResource(R.drawable.btn_default_normal_disable_focused);
			llNumInput.setVisibility(android.view.View.GONE);
			llspninput.setVisibility(android.view.View.GONE);
			rg.setVisibility(android.view.View.GONE);
			btnBack.setVisibility(android.view.View.INVISIBLE);
			}
		} else {
			//Ending No more questions are left
			//Calculate group summary
			
			gsumm.setCharAt(0, ccatgr);
			class_str='G';
			if ((ansarr[29]>5)||(ansarr[31]>102)||(ansarr[34]==1)||(ansarr[36]==1)) class_str='R';
			gsumm.setCharAt(1, class_str);
			//Display Categerisation for group 3 in case of mother death
			Intent intent = null;
			if (cbranch)  //End of group 3 Question no 60
				{
				class_str='G';cid=5;
				if (((ansarr[56]==1)&&(ansarr[58]==1))||((ansarr[56]==1)&&(ansarr[60]==1))||((ansarr[58]==1)&&(ansarr[60]==1))) { cid=3;class_str='R';}
				else if (((ansarr[57]==1)&&(ansarr[59]==1))||((ansarr[57]==1)&&(ansarr[60]==1))||((ansarr[59]==1)&&(ansarr[60]==1))) {cid=4;class_str='Y';}
				else if ((ansarr[54]==1)&&(ansarr[55]==0)&&(ansarr[56]==0)&&(ansarr[57]==0)&&(ansarr[58]==0)&&(ansarr[59]==0)&&(ansarr[60]==0)) {cid=5;class_str='G';}
				else if (ansarr[54]==0) {cid=10;class_str='G';}
				else if (ansarr[55]==1) {cid=6;class_str='R';}
				gsumm.setCharAt(3, class_str);
				mp.reset();
				intent = new Intent(ctx,Remedy.class);
				intent.putExtra("cid",cid);
				//ctx.startActivity(intent);
				};
			ansstr="";
			for(int i=0;i<(DBAdapter.qc[4]+1);i++) {
				if (ansarr[i]==-1) ansstr=ansstr+"- ";
				else if (ansarr[i]==0) ansstr=ansstr+"0 ";
				else if (ansarr[i]==1) ansstr=ansstr+"1 ";
				else ansstr=ansstr+new DecimalFormat("#.#").format(ansarr[i])+" ";
			if (i==DBAdapter.qc[0]) {answrg0=ansstr;ansstr="";}
			else if (i==DBAdapter.qc[1]) {answrg1=ansstr;ansstr="";}
			else if (i==DBAdapter.qc[2]) {answrg2=ansstr;ansstr="";}
			else if (i==DBAdapter.qc[3]) {answrg3=ansstr;ansstr="";}
			else if (i==DBAdapter.qc[4]) {answrg4=ansstr;ansstr="";}
			//else if (i==DBAdapter.qc[5]) {answrg5=ansstr;ansstr="";}			
			}
			StringBuilder mhv_str=new StringBuilder(hv_str);
			mhv_str.setCharAt(seq-1, '1');
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm",Locale.getDefault());
	        String pdte = df.format(c.getTime());
			String pktHeader=getString(R.string.sms_prefix)+" "+getString(R.string.signature)+" "+uid;
			String longMessage,longMessage1;
			if (!learn)
				{
				if (!fvisit)
					{
					mydb.updatePregVisitStr(pid, mhv_str.toString());
					mydb.updateVisit(id, answrg0, answrg1, answrg2, answrg3, answrg4,gsumm.toString());
					longMessage=String.format(Locale.getDefault(),"%s:H1:%d:%d:%s:%s:%s:%s",pktHeader,pid,seq,pdte,answrg0,answrg1,gsumm);					
					longMessage1=String.format(Locale.getDefault(),"%s:H2:%d:%d:%s:%s:%s:%s",pktHeader,pid,seq,pdte,answrg2,answrg3,answrg4);					
					} else
					{
						mydb.optVisit(pid, answrg0, answrg1, answrg2, answrg3, answrg4,gsumm.toString());
						longMessage=String.format(Locale.getDefault(),"%s:O1:%d:%d:%s:%s:%s:%s",pktHeader,pid,seq,pdte,answrg0,answrg1,gsumm);					
						longMessage1=String.format(Locale.getDefault(),"%s:O2:%d:%d:%s:%s:%s:%s",pktHeader,pid,seq,pdte,answrg2,answrg3,answrg4);					
					}
				  String phoneNumber=getString(R.string.smsno);
					if (send_sms) {
						mydb.sendSMS(phoneNumber, longMessage);
						mydb.sendSMS(phoneNumber, longMessage1);
					}
					
				}
			Toast.makeText(getApplicationContext(),"Questions over", Toast.LENGTH_SHORT).show();
			btnNext.setVisibility(android.view.View.INVISIBLE);
			Intent intentSumm = new Intent(ctx,Visit_summary.class);
			intentSumm.putExtra("id", id);
			intentSumm.putExtra("hv_str", hv_str);
			intentSumm.putExtra("seq", seq);
			intentSumm.putExtra("pid", pid);
			intentSumm.putExtra("bflag", false);
			intentSumm.putExtra("fvisit", fvisit);
			ctx.startActivity(intentSumm);
			if (intent!=null) ctx.startActivity(intent);
			mp.reset();
			mp.release();
			beep_mp.release();
			finish();
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();     
	}


	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage("Think again! Do you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	    mp.reset();
	            	    c.close();
		            	   mydb.close();
	                    finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}
 
	final private Handler mHandler = new Handler();
    Runnable mUpdateTime = new Runnable() {
        public void run() { updateTimeView(); }
    };
    
	public void updateTimeView() {
        sec++;
        /*
        if(sec >= 60) {
            sec = 0;
            min += 1;
            if (min >= 60) {
                min = 0;
                hour += 1;
            }
        }*/
        
        timeView.setText(String.format(mTimeFormat, hour, min, sec));
        if (sec==60) { beep_mp.start();srun=false;sec=0; }
        if (srun)
        	mHandler.postDelayed(mUpdateTime, 1000); 
    }
/*
	private void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        final String q_msg=message;
        
 
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
    
	private void sendSMS1(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        final String q_msg=message;
        
 
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

}
