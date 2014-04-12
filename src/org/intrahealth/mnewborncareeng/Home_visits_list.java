package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.intrahealth.mnewborncareeng.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
//import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.app.DatePickerDialog;

public class Home_visits_list extends ListActivity {
	private DBAdapter mydb;
	//TextView txtCount;
	//Typeface face;
	int id=0,seq=0,pid,dstat=0;
	private int year,cy;
	private int month,cm;
	private int day,cd;
	static final int DATE_DIALOG_ID = 999;
	Button btnDate;
	ListView lst;
	String mydt;
	Cursor c;
	MediaPlayer mediaPlayer = new MediaPlayer();
	public String hv_str ="",mname="";
	boolean learn=false,death=false,fvisit=false,current=true;
	Button btnNewTest,btnImmun;
	static int lastSel=-1;
	
	@Override
	public void onStop()
	{		
		mydb.close();
		super.onStop();
	}
	
	public static int getCurrentSelectedItemId() {
	    return lastSel;
	}
	
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.home_visit_list);
	    
        //face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);
	    //txtCount=(TextView)findViewById(R.id.txtCount);
	    btnImmun=(Button) findViewById(R.id.btnImmun);
        final Calendar clnd = Calendar.getInstance();
		year = clnd.get(Calendar.YEAR);
		month = clnd.get(Calendar.MONTH);
		day = clnd.get(Calendar.DAY_OF_MONTH);
		cy=year;cm=month;cd=day;
		Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	learn=extras.getBoolean("learn");
        	death=extras.getBoolean("death");
        }
		mydb = DBAdapter.getInstance(getApplicationContext());
       /* mydb = new DBAdapter(this);
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
	    //txtCount.setText(String.valueOf(mydb.getPregNo()));
		mydt=String.format("'%04d-%02d-%02d'",year,month+1,day);
	    c=mydb.getPregVisits(mydt);
		startManagingCursor(c);
        // the desired columns to be bound
	            String[] from = new String[] { DBAdapter.KEY_NAME, "sv_dt","diff",DBAdapter.KEY_ROWID,"hv_str" };
	            // the XML defined views which the data will be bound to
	            int[] to = new int[] { R.id.name_entry, R.id.number_entry };	
		
		LazyCursorAdapterVisits ca=new LazyCursorAdapterVisits(this,R.layout.hv_list_row,c,from,to);
	    setListAdapter(ca);
	    lst=(ListView)findViewById(android.R.id.list);
	    lst.setOnItemClickListener(new OnItemClickListener()
	    {
	        public void onItemClick(AdapterView<?> adapterView, View v,int position, long arg3)
	        { 
	        	 Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
	        	 lastSel = position;
                 id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 hv_str=cursor.getString(cursor.getColumnIndex("hv_str")); 
                 seq=cursor.getInt(cursor.getColumnIndex("seq"));
                 pid=cursor.getInt(cursor.getColumnIndex("pid"));
                 //dob=cursor.getString(cursor.getColumnIndex("dob"));
                 Calendar mc=Calendar.getInstance();
                 String dt_str[]=cursor.getString(cursor.getColumnIndex("sv_dt")).split("\\-");
         	    mc.set(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]));
                Calendar def=Calendar.getInstance();
                if (mc.after(def)) fvisit=true;else fvisit=false;
         	    mname=cursor.getString(cursor.getColumnIndex("name"));
                 if (cursor.getInt(cursor.getColumnIndex("m_stat"))==1) dstat=1;
                 else if (cursor.getInt(cursor.getColumnIndex("c_stat"))==1) dstat=2;
                if (!death)
                	{
                	if (dstat==2) 
                		btnImmun.setVisibility(android.view.View.INVISIBLE);
                	else btnImmun.setVisibility(android.view.View.VISIBLE);
                	}
	        	Toast.makeText(getApplicationContext(), String.valueOf(pid)
	        			//+" "+mc.get(Calendar.YEAR)+" "+mc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)+" "
	        			//+mc.get(Calendar.DATE)+String.valueOf(fvisit)
	        			, Toast.LENGTH_SHORT).show();
	        	try {
		    		String audioFile;
		    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
		    				+ "/mcare/pdata/"+String.valueOf(pid)+".3gp";
	        		
		    		//mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/tmp/test.3gp");
		    		mediaPlayer.reset();
		    		mediaPlayer.setDataSource(audioFile);
		    		mediaPlayer.prepare();
		    		mediaPlayer.start();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    			}

	        	//return true;
	        }
	    });

	    lst.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?>  adapterView, View v,int position, long arg3) {
				// TODO Auto-generated method stub
	        	 Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                 int pid = cursor.getInt(cursor.getColumnIndex("pid"));
                 mname=cursor.getString(cursor.getColumnIndex("name"));
                 lastSel = position;
                 ImageView img=new ImageView(getApplicationContext());
                 String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/pdata/"+
                 String.valueOf(pid) +".jpg";
                 Bitmap bmp = BitmapFactory.decodeFile(imgfile);
                 img.setImageBitmap(bmp);
				loadPhoto(img, 100, 100);
				return false;
			}
		
	    });
	    
	    
	    addListenerOnButton();
	   
	  }
	
	  private void addListenerOnButton() {
		// TODO Auto-generated method stub
		  
		  //btnHistory.setTypeface(face);
	        btnDate=(Button)findViewById(R.id.btnCalender);
		    //DateFormat df=DateFormat.getDateInstance();

		    btnDate.setText(String.format("%d-%s-%d", day,DBAdapter.hmstr[month],year));
		    //btnDate.setText("  "+df.format(new Date()));
		  if (death) btnDate.setVisibility(android.view.View.INVISIBLE);
		  	else btnDate.setVisibility(android.view.View.VISIBLE);
		  btnNewTest=(Button) findViewById(R.id.btnNewTest);
		  //btnNewTest.setTypeface(face);
		  TextView tvHead=(TextView)findViewById(R.id.tvHead);
		  if (death) {
			  tvHead.setText("List");
			  btnNewTest.setText("Death Information");
			  btnNewTest.setBackgroundResource(R.drawable.btn_default_normal_red);
			  btnImmun.setVisibility(android.view.View.INVISIBLE);
		  		}
		  	else {
		  		tvHead.setText("Home visit");
		  		btnNewTest.setText("Start checkup");
		  		btnNewTest.setBackgroundResource(R.drawable.btn_default_normal_green); 
		  		btnImmun.setVisibility(android.view.View.VISIBLE);
		  		}
		  if (dstat==2) btnImmun.setVisibility(android.view.View.INVISIBLE);
		  btnNewTest.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (id==0)
		        	Toast.makeText(getApplicationContext(), "Select Benificiery", Toast.LENGTH_SHORT).show();
				else
				{
					if (!death)
					{
					Context ctx=arg0.getContext();
					Intent intentSumm = new Intent(ctx,Visit_summary.class);
					intentSumm.putExtra("id", id);
					intentSumm.putExtra("hv_str", hv_str);
					intentSumm.putExtra("seq", seq);
					intentSumm.putExtra("pid", pid);
					intentSumm.putExtra("bflag", true);
					intentSumm.putExtra("learn", learn);
					intentSumm.putExtra("dstat", dstat);
					intentSumm.putExtra("fvisit", fvisit);
					ctx.startActivity(intentSumm);
					} else {
						Context ctx=arg0.getContext();
						Intent intentDrep = new Intent(ctx,DReport_entry.class);
						intentDrep.putExtra("pid", pid);
						intentDrep.putExtra("mname", mname);
						ctx.startActivity(intentDrep);
						finish();
					}
				}
			}
			  
		  });

		  btnImmun.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (id==0)
		        	Toast.makeText(getApplicationContext(), "Select Benificiery", Toast.LENGTH_SHORT).show();
				else {
					Context ctx=v.getContext();
					Intent intent = new Intent(ctx,Immun_entry.class);
					intent.putExtra("id", pid);
					intent.putExtra("mname", mname);
					intent.putExtra("change", false);
					ctx.startActivity(intent);
					finish();
				}
			}
		});
		  
		  btnDate.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					showDialog(DATE_DIALOG_ID);
			}
			  
		  });

	  
	  }

	  @Override
		protected Dialog onCreateDialog(int id) {
			switch (id) {
			case DATE_DIALOG_ID:
			   // set date picker as current date
			   return new DatePickerDialog(this, datePickerListener, 
	                         year, month,day);
			}
			return null;
		}	  

	  private DatePickerDialog.OnDateSetListener datePickerListener 
      = new DatePickerDialog.OnDateSetListener() {

// when dialog box is closed, below method will be called.
public void onDateSet(DatePicker view, int selectedYear,
		int selectedMonth, int selectedDay) {
	

	year = selectedYear;
	month = selectedMonth;
	day = selectedDay;
	if ((year==cy) && (month==cm) && (day==cd)) current=true;else current=false;	
    btnDate.setText(String.format("%d-%s-%d", day,DBAdapter.hmstr[month],year));
	mydt=String.format("'%04d-%02d-%02d'",year,month+1,day);
	Toast.makeText(getApplicationContext(), mydt, Toast.LENGTH_SHORT).show();
    c=mydb.getPregVisits(mydt);startManagingCursor(c);
    LazyCursorAdapterVisits adapter = (LazyCursorAdapterVisits)lst.getAdapter();
    adapter.changeCursor(c);
	// set selected date into textview
	/*
	tvDisplayDate.setText(new StringBuilder().append(month + 1)
	   .append("-").append(day).append("-").append(year)
	   .append(" "));

	// set selected date into datepicker also
	dpResult.init(year, month, day, null);
*/
	  if (current) btnNewTest.setVisibility(android.view.View.VISIBLE);
	  	else btnNewTest.setVisibility(android.view.View.INVISIBLE);
	}
};	  
	  
	@Override
	  protected void onResume() {
	    lastSel=-1;
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	  }

	  private void loadPhoto(ImageView imageView, int width, int height) {

	        ImageView tempImageView = imageView;


	        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
	        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

	        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
	                (ViewGroup) findViewById(R.id.layout_root));
	        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
	        image.setImageDrawable(tempImageView.getDrawable());
	        imageDialog.setView(layout);
	        imageDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }

	        });


	        imageDialog.create();
	        imageDialog.show();     
	    }


	  
	  
}
