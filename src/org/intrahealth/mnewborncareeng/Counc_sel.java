package org.intrahealth.mnewborncareeng;


import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;


public class Counc_sel extends Activity {
	
	Spinner spnCouncSel;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.counc_sel);	
        spnCouncSel=(Spinner)findViewById(R.id.spnCouncSel);
        Button btnNext=(Button)findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        int sel=spnCouncSel.getSelectedItemPosition();
		        if (sel==0)
		        	{
					Intent intent = new Intent(getApplicationContext(),Counc_modlist.class);
					startActivity(intent);
		        	}
		        else
	        		{
					Intent intent = new Intent(getApplicationContext(),Counc_info.class);
					intent.putExtra("mnth", sel+4);
					startActivity(intent);
	        		}
			}
		});
    }
}
