package org.intrahealth.mnewborncareeng;

//import com.intrahealth.mnewborncare.control.BadgeView;



import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;



import android.content.Intent;

//import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;


import android.widget.Button;



public class Report_list extends Activity {


	
	Intent i;

		
	

	
	@Override	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.report_list);
        Button btnHVList=(Button)findViewById(R.id.btnHVList);
        Button btnDList=(Button)findViewById(R.id.btnDList);
        Button btnAbortRep=(Button)findViewById(R.id.btnAbortRep);        
        
        btnHVList.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),Reporthv.class);
				intent.putExtra("rep_type", "hvlist");
				startActivity(intent);
			}
		});
        
        btnDList.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),Report.class);
				intent.putExtra("rep_type", "drep");
				startActivity(intent);
			}
		});

        btnAbortRep.setOnClickListener(new OnClickListener() {
			
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				Intent intent = new Intent(getApplicationContext(),Report.class);
 				intent.putExtra("rep_type", "abrep");
 				startActivity(intent);
 			}
 		});

        
	}
	
}
