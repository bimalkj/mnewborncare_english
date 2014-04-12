package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.IOException;

import org.intrahealth.mnewborncareeng.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Anc_visit_list extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	int id=0,pid,seq;
	String mname,hv_str;
	Cursor c;
	static int lastSel=-1;
	MediaPlayer mediaPlayer = new MediaPlayer();
	 Button btnModBirth,btn;
	 
		@Override
		public void onDestroy()
		{		
			if (c!=null) c.close();
			mydb.close();
			super.onDestroy();
		}

		public static int getCurrentSelectedItemId() {
		    return lastSel;
		}
		
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.anc_preg_list);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
	    txtCount=(TextView)findViewById(R.id.txtCount);
		  btn=(Button) findViewById(R.id.btnNewBirth);
		  //btnModBirth.setTypeface(face);
	    //mydb = new DBAdapter(this);
		    mydb=DBAdapter.getInstance(getApplicationContext());
		/*    
	    try {
	    	mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    */
	    //txtCount.setText(String.valueOf(mydb.getPregNo()));
	    c=mydb.getANCList();
	    txtCount.setText("Total Pregnant Women : "+String.valueOf(c.getCount()));
	    startManagingCursor(c);
        // the desired columns to be bound
	            String[] from = new String[] { DBAdapter.KEY_NAME, "edd",DBAdapter.KEY_ROWID };
	            // the XML defined views which the data will be bound to
	            int[] to = new int[] { R.id.name_entry, R.id.number_entry };	
		
		LazyCursorAdapterAnc ca=new LazyCursorAdapterAnc(this,R.layout.list_row,c,from,to);
	    setListAdapter(ca);
	    ListView lst=(ListView)findViewById(android.R.id.list);
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
                /*
                 if (cursor.isNull(cursor.getColumnIndex("dob")))
                	 {
                	 btnModBirth.setVisibility(android.view.View.INVISIBLE);
                	 btn.setVisibility(android.view.View.VISIBLE);
                	 }
                 else {
                	 btnModBirth.setVisibility(android.view.View.VISIBLE);
                	 btn.setVisibility(android.view.View.INVISIBLE); 
                 	}*/
                 mname=cursor.getString(cursor.getColumnIndex("name"));
	        	//Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();
	        	try {
		    		String audioFile;
		    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
		    				+ "/mcare/pdata/"+String.valueOf(id)+".3gp";
	        		
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
	        	//cursor.close();
	        }
	    });
	    
	    lst.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?>  adapterView, View v,int position, long arg3) {
				// TODO Auto-generated method stub
	        	 Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                 int key_id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 lastSel = position;
                 ImageView img=new ImageView(getApplicationContext());
                 String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/pdata/"+
                 String.valueOf(key_id) +".jpg";
                 Bitmap bmp = BitmapFactory.decodeFile(imgfile);
                 img.setImageBitmap(bmp);
				loadPhoto(img, 100, 100);
				cursor.close();
				return false;
			}
		
	    });


	    addListenerOnButton();
	  }
	
	  private void addListenerOnButton() {
		// TODO Auto-generated method stub

		  
		  //btn.setTypeface(face);
		  btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (id==0)
		        	Toast.makeText(getApplicationContext(), "Select Beneficiary", Toast.LENGTH_LONG).show();
				else
				{
					Context ctx=arg0.getContext();
				Intent intent = new Intent(ctx,AVisit_summary.class);
				intent.putExtra("id", id);
				intent.putExtra("hv_str", hv_str);
				intent.putExtra("seq", seq);
				intent.putExtra("pid", pid);
				intent.putExtra("bflag", true);
				ctx.startActivity(intent);
				}
			}
			  
		  });

		  
		  
		  
	}

	@Override
	  protected void onResume() {
	    lastSel=-1;
	    super.onResume();
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
