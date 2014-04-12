package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
//import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Counc_info extends Activity {
	int mnth=0,gid=0,yind,nind,nqid,qtype=-1,y,n,grp=0,clr=0;
	private DBAdapter mydb;
	//Typeface face;
	Button btnQuest,btnBack,btnNext;
	LinearLayout llBack;
	ImageView imgCounc;
	ImageButton btnHelp;
	TextView tvHead;
	MediaPlayer mp,beep_mp;
	Cursor c;
	//String grpinfo[]={"f'k'kq ds tkap","ek¡ fd tkap","thok.kq laØe.k tkap","nLrjksx fd tkap",
	//		"Lruiku lEcaf/kr tkap","Vhdkdj.k lEcaf/kr tkap"};
	//String grpinfo[]={"शिश�? की जांच","मां की जांच","जीवाण�? संक�?रमण जांच","दस�?तरोग की जांच","स�?तनपान सम�?बन�?धित जांच"};
	int click_cnt=1;
	String pghead="",ghead="";
	Context ctx;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.counc_info);
	    tvHead=(TextView)findViewById(R.id.tvHead);
	    imgCounc=(ImageView)findViewById(R.id.imgCounc);
	    llBack=(LinearLayout)findViewById(R.id.llRemedyBack);
	    //tvCatgr=(TextView)findViewById(R.id.tvCatgr);
      //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);
        //tvCatgr.setTypeface(face);
        btnQuest=(Button)findViewById(R.id.btnQuest);
        //LinearLayout llRemedyBack=(LinearLayout)findViewById(R.id.llRemedyBack);
        //btnQuest.setTypeface(face);
        tvHead.setText("Counseling");//Classification
        //btnQuest.setText("¼1½ igyh tkap");
        ctx=getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	mnth = extras.getInt("mnth");
        	gid = extras.getInt("gid");
        }
        beep_mp=MediaPlayer.create(getApplicationContext(), R.raw.beep7);
        try {
			beep_mp.prepare();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
        mp=MediaPlayer.create(getApplicationContext(), R.raw.hv1);
        AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("cosnd/"+String.valueOf(mnth)+"bcoa.3gp"); //Beginning counsel audio
			mp.reset();
			mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			mp.prepare();
			mp.start();	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
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
	    c=mydb.getCouncInfo(mnth,gid);
	    if (c.moveToFirst())
	    {
	    //clr=c.getInt(c.getColumnIndex("clr"));
	    	pghead=c.getString(c.getColumnIndex("grphead"));ghead=pghead;
	    btnQuest.setText(ghead);
	    llBack.setBackgroundResource(R.color.clrYellow);
		String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/counsel/coimg/"+
       		 String.valueOf(nqid)+"gco.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(imgfile);
        imgCounc.setImageBitmap(bmp);

	    //tvCatgr.setText(btnQuest.getText());
	    }
	    addListenerOnButton();
    }
	
	private void addListenerOnButton() {
		
		btnHelp=(ImageButton)findViewById(R.id.btnHelp);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnBack =(Button) findViewById(R.id.btnBack);
		//btnNext.setTypeface(face);
		//btnBack.setTypeface(face);
		btnBack.setVisibility(android.view.View.INVISIBLE);
		//npNumInput=(NumberPicker)findViewById(R.id.npNumInput);
		nqid=0;				
		c=mydb.getCouncInfo(mnth,gid);
		//c.moveToFirst();		
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				
				mp.reset();
				beep_mp.start();
				click_cnt++;
				if (click_cnt==1) {
					btnNext.setBackgroundResource(R.drawable.custom_button_green);
				} else if (click_cnt==2)
				{
				click_cnt=0;
				btnNext.setBackgroundResource(R.drawable.custom_button_yellow);
					// TODO Auto-generated method stub
				String qtext="";
				//c=mydb.getNextQuest(nqid);
				c.moveToNext();
				if(!c.isAfterLast())
				{
					String prompt=c.getString(c.getColumnIndex("prompt"));
					tvHead.setText(prompt);
					ghead=c.getString(c.getColumnIndex("grphead"));
					String gbreak="";
					if (!ghead.equals(pghead)) 
						{ 
						pghead=ghead;
						c.moveToPrevious(); 
						qtext=ghead;
						gbreak="g";
						llBack.setBackgroundResource(R.color.clrYellow);
						} 
					else
						{
						llBack.setBackgroundResource(R.color.clrOffwhite);
						String ctext="";
						if (!c.isNull(c.getColumnIndex("ctext"))) ctext=c.getString(c.getColumnIndex("ctext")); 
						//qtext=String.valueOf(nqid)+") "+ctext;
						qtext=ctext;
						}
					try {
						nqid=c.getInt(c.getColumnIndex("_id"));
				   		String audioFile;
			    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
			    				+ "/mcare/counsel/cosnd/"+String.valueOf(nqid)+"coa.3gp";
			    		mp.reset();
			    		mp.setDataSource(audioFile);
						mp.prepare();
						mp.start();	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
			
					String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/counsel/coimg/"+
		                		 String.valueOf(nqid)+gbreak+"co.jpg";
		                 Bitmap bmp = BitmapFactory.decodeFile(imgfile);
		                 imgCounc.setImageBitmap(bmp);
				
				//tvHead.setText(grpinfo[c.getInt(c.getColumnIndex("grp"))]);
				if (c.getInt(c.getColumnIndex("help"))==1)
					btnHelp.setVisibility(android.view.View.VISIBLE);
				else btnHelp.setVisibility(android.view.View.GONE);
				btnBack.setVisibility(android.view.View.VISIBLE);
				btnQuest.setText(qtext);
				//c.moveToNext();
				} else {
					//Ending No more questions are left
					//Toast.makeText(getApplicationContext(),"Going back..", Toast.LENGTH_SHORT).show();
					openDialog();
					/*
					btnNext.setVisibility(android.view.View.INVISIBLE);
					mp.reset();
					mp.release();
					beep_mp.release();
					mydb.close();
					finish();
					*/
				}
				
				//Toast.makeText(getApplicationContext(),"Record added successfully ..", Toast.LENGTH_SHORT).show();
			}
		}
		});
	
		btnQuest.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {				
				try {
			   		String audioFile;
		    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
		    				+ "/mcare/counsel/cosnd/"+String.valueOf(nqid)+"coa.3gp";
		    		mp.reset();
		    		mp.setDataSource(audioFile);
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
				// TODO Auto-generated method stub
				String qtext="";
				//c=mydb.getNextQuest(nqid);
				c.moveToPrevious();
				if(!c.isBeforeFirst() && !c.isAfterLast())
				{
					try {
						nqid=c.getInt(c.getColumnIndex("_id"));
				   		String audioFile;
			    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
			    				+ "/mcare/counsel/cosnd/"+String.valueOf(nqid)+"coa.3gp";
			    		mp.reset();
			    		mp.setDataSource(audioFile);
						mp.prepare();
						mp.start();	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				tvHead.setText(c.getString(c.getColumnIndex("prompt")));	
				//qtext=String.valueOf(nqid)+" "+c.getString(c.getColumnIndex("ctext"));
				qtext=c.getString(c.getColumnIndex("ctext"));
				//tvHead.setText(grpinfo[c.getInt(c.getColumnIndex("grp"))]);
				if (c.getInt(c.getColumnIndex("help"))==1)
					btnHelp.setVisibility(android.view.View.VISIBLE);
				else btnHelp.setVisibility(android.view.View.GONE);
				btnQuest.setText(qtext);
				
				} else {
					//Ending No more questions are left
					//Toast.makeText(getApplicationContext(),"Going back..", Toast.LENGTH_SHORT).show();
					btnBack.setVisibility(android.view.View.INVISIBLE);
					mp.reset();
					//finish();
				}
				
				//Toast.makeText(getApplicationContext(),"Record added successfully ..", Toast.LENGTH_SHORT).show();

			}
		});

		
		btnHelp.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String movieurl = Environment.getExternalStorageDirectory() + "/v3gp/"+String.valueOf(nqid)+"cov.3gp";
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
	
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(),"Counselling not completed ..", Toast.LENGTH_SHORT).show();
	}

	public void openDialog()
	{
		AlertDialog.Builder customDialog 
	     = new AlertDialog.Builder(Counc_info.this);
		customDialog
         .setMessage("Counselling over! Go ahead ?")
         .setCancelable(false)
         .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
					btnNext.setVisibility(android.view.View.INVISIBLE);
					mp.reset();
					mp.release();
					beep_mp.release();
					mydb.close();
					finish();
             }
         })
         .setNegativeButton("No", null)
         .show();
	}
	
}
