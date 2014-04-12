package org.intrahealth.mnewborncareeng;

//import java.io.IOException;



import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
//import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Visit_summary extends Activity {
	int id=0,yind,nind,nqid,qtype=-1,y,n,grp=0,attch_stat=0,seq=0,pid,pqid=0,dstat=0;
	boolean bflag=false,learn=false,fvisit=false;
	//private static int TAKE_PICTURE = 1;
	//private int qind=0;
	private DBAdapter mydb;
	//Typeface face;
	Button btnBack,btnNext;
	TextView tvHead;
	Cursor c;
	String hv_str="";
	//String grpinfo[]={"f'k'kq ds tkap","ek¡ fd tkap","thok.kq laØe.k tkap","nLrjksx fd tkap",
	//		"Lruiku lEcaf/kr tkap","Vhdkdj.k lEcaf/kr tkap"};
	//String grpinfo[]={"शिश�? की जांच",	"मां की जांच",	"जीवाण�? संक�?रमण जांच",	"दस�?तरोग की जांच",	"स�?तनपान सम�?बन�?धित जांच",""};
	String grpinfo[]={"Newborn check-up","Mother's check-up","Bacterial infection check-up","Diarrhea check-up","BreastFeeding check-up",""};
	
	//String vst_seq[]={"¼1½ igyh tkap","¼2½ nwljh tkap","¼3½ rhljh tkap","¼4½ pkSFkh tkap","¼5½ ikapoh tkap","¼6½ Ìëh tkap","¼7½ lkroh tkap"};
//	String vst_seq[]={"1. पहली जांच","2. दूसरी जांच","3. तीसरी जांच","4. चौथी जांच","5. पांचवी जांच","6. छठी जांच","7. सातवीं जांच"};
	String vst_seq[]={"1. First check-up","2. Second check-up","3. Third check-up","4. Fourth check-up","5. Fifth check-up","6. Sixth check-up","7. Seventh check-up"};

	Context ctx;
	LinearLayout ll;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.visit_summary);
        ll=(LinearLayout)findViewById(R.id.llSummary);
	    tvHead=(TextView)findViewById(R.id.tvHead);
      //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);
        ctx=this;

        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	id = extras.getInt("id");
        	hv_str=extras.getString("hv_str");
        	seq=extras.getInt("seq");
        	pid=extras.getInt("pid");
        	bflag=extras.getBoolean("bflag");
        	learn=extras.getBoolean("learn");
        	dstat=extras.getInt("dstat");
        	fvisit=extras.getBoolean("fvisit");
        }
        tvHead.setText(vst_seq[seq-1]);
        if (fvisit) tvHead.setText("Optional check-up");
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
		TextView tvPname=(TextView)findViewById(R.id.tvPname);
		TextView tvHeadVisit=(TextView)findViewById(R.id.tvHeadVisits);
		//btnBack.setVisibility(android.view.View.INVISIBLE);
		//tvHeadVisit.setTypeface(face);
		if (bflag) { tvHeadVisit.setText("Summary (Last visit) ");btnBack.setVisibility(android.view.View.VISIBLE);c=mydb.getVisitSummary(pid,false); }
		else { tvHeadVisit.setText("Summary (This visit)"); btnBack.setVisibility(android.view.View.INVISIBLE);c=mydb.getVisitSummary(pid,fvisit); }
		//btnNext.setTypeface(face);
		//btnBack.setTypeface(face);
		nqid=0;				
			
		if (c.moveToFirst())
		{
			tvPname.setText("Date of visit :"+c.getString(c.getColumnIndex("avd")));
			StringBuilder gsumm=new StringBuilder(c.getString(c.getColumnIndex("gsumm")));
			for(int i=0;i<gsumm.length();i++)
			{
			char stch=gsumm.charAt(i);
			String st="Green"; //
			TextView tv=new TextView(ctx);
			//+" "+gsumm.charAt(i));
			if (stch=='R') { tv.setBackgroundColor(Color.RED);st="Red";}
			else if (stch=='Y') { tv.setBackgroundColor(Color.YELLOW);st="Yellow";}
			else tv.setBackgroundColor(Color.GREEN);
			//tv.setText((i+1)+") "+grpinfo[i]+" - "+st);
			if (!grpinfo[i].trim().equals(""))
				{
				tv.setText((i+1)+") "+grpinfo[i]);
				//tv.setTypeface(face);
				tv.setTextSize(20);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				llp.setMargins(5, 5, 0, 5); // llp.setMargins(left, top, right, bottom);
				tv.setLayoutParams(llp);
				ll.addView(tv);
				} 
			}
		} else
		{
			TextView tv=new TextView(ctx);
			tv.setText("No home visit yet");
			
			ll.addView(tv);			
		}
		
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				
				if (bflag)
				{
				Intent intent = new Intent(ctx,Quest.class);
				intent.putExtra("id", id);
				intent.putExtra("hv_str", hv_str);
				intent.putExtra("seq", seq);
				intent.putExtra("pid", pid);
				intent.putExtra("learn", learn);
				intent.putExtra("dstat", dstat);
				intent.putExtra("fvisit", fvisit);
				ctx.startActivity(intent);
				}
				finish();
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {				
				finish();
			}
		});		

		

	
	}


}
