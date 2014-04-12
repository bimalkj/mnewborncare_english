package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.intrahealth.mnewborncareeng.R;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {

	EditText edtPasswd;
	ImageButton imageButton;
	int a;
	String asha_id="";
	SharedPreferences prefs=null;
	ProgressDialog mProgressDialog;
	/*
	int pending=0;
	
	private SmsService serviceBinder;
	Intent i;
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			//---called when the connection is made---
			serviceBinder = ((SmsService.MyBinder)service).getService();
			startService(i);
		}
		
	public void onServiceDisconnected(ComponentName className) {
	//---called when the service disconnects---
	serviceBinder = null;
		}
	};	
	*/
	
	public String getVersion() {
		String mVersionNumber;
		Context mContext=getApplicationContext();
		try {
            String pkg = mContext.getPackageName();
            mVersionNumber = mContext.getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (NameNotFoundException e) {
            mVersionNumber = "?";
        }
		return " V "+mVersionNumber;
	}

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtPasswd=(EditText)findViewById(R.id.edtPasswd);
        edtPasswd.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtPasswd.setSelectAllOnFocus(true);       
		prefs=PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
        
        TextView tvSlno=(TextView)findViewById(R.id.slno);
        asha_id=prefs.getString("id", "1").trim();
        tvSlno.setText(asha_id);
        setTitle(getResources().getString(R.string.app_name)+ getVersion());
        addListenerOnButton();   
    }
		
    private void addListenerOnButton() {
    	ImageButton btnAbout=(ImageButton)findViewById(R.id.btnAbout);
    	btnAbout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Context ctx=v.getContext();
				Intent intent = new Intent(ctx,Aboutus.class);
				ctx.startActivity(intent);
			}
		});
    	imageButton = (ImageButton) findViewById(R.id.btnStart);
    	 
		imageButton.setOnClickListener(new OnClickListener() {
 
			//@Override
			public void onClick(View arg0) {
				Context ctx=arg0.getContext();
				String pass_str=prefs.getString("user_password", "0000");
				String admpass_str=prefs.getString("adm_password", "123456");
				//Boolean send_sms=prefs.getBoolean("send_sms", false);
				if(arg0.getId() == R.id.btnStart){
					Intent intent=null;
					
					if (edtPasswd.getText().toString().equalsIgnoreCase(admpass_str))
						intent = new Intent(ctx,EditPreference.class);
					else if (!edtPasswd.getText().toString().equalsIgnoreCase(pass_str))
						Toast.makeText(ctx,getString(R.string.errInvPass), Toast.LENGTH_LONG).show();
						else
							{
							/*if (send_sms)
							{
								i = new Intent(MainActivity.this, SmsService.class);
								bindService(i, connection, Context.BIND_AUTO_CREATE);
								pending=serviceBinder.pending;
								//startService(new Intent(getBaseContext(), SmsService.class));
							}
								*/
							intent = new Intent(ctx,Workflow.class);
							
							}
					
					if (intent!=null) ctx.startActivity(intent);
				}
			}
		});
		
	}


    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
        .setMessage("Think again! Do you want to exit?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	try {
            		stopService(new Intent(getBaseContext(), SmsService.class));
            		String pnm = getApplicationContext().getPackageName();
                    File sd = Environment.getExternalStorageDirectory();
                    //String pfname=getFilesDir().getPath()+ "../shared_prefs/"+pnm+"_preferences.xml";

                    if (sd.canWrite()) {
                    	File locDB = new File(getFilesDir(),"../databases/");
                    	//String backupDBPath = "backupdata.db";
                    	String backupDBPath = asha_id+"mnewborncare_back.db";
                        File currentDB = new File(locDB,DBAdapter.DATABASE_NAME);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                       
                       if (true)
                       {
                    	   File locPF = new File(getFilesDir(),"../shared_prefs/");
                    	   File currentPF=new File(locPF,pnm+"_preferences.xml");
                           File backupPF = new File(sd,"prefs.xml");
                    	   FileChannel src=new FileInputStream(currentPF).getChannel();
                    	   FileChannel dst=new FileOutputStream(backupPF).getChannel();
                       dst.transferFrom(src, 0, src.size());
                       src.close();
                       dst.close();
                       }
                        
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                	

                }
                 finish();
            }
        })
        .setNegativeButton("No", null)
        .show();
    }
    
 
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
    
}
