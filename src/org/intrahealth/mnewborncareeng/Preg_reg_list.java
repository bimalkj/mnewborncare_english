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
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Preg_reg_list extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	//Typeface face;
	ListView lst;
	int preg_id=-1;
	MediaPlayer mediaPlayer = new MediaPlayer();
	//private View lastSelectedView = null;
	static int lastSel=-1;
		
	public static int getCurrentSelectedItemId() {
	    return lastSel;
	}
	
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.preg_reg);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
      //  face=Typeface.createFromAsset(getAssets(),getString(R.string.hindi_font));
        //tvHead.setTypeface(face);

	    txtCount=(TextView)findViewById(R.id.txtCount);
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
	    
	    Cursor c=mydb.getAllPregList();
	    txtCount.setText("Total pregnant women: "+String.valueOf(c.getCount()));
	    startManagingCursor(c);
        // the desired columns to be bound
	            String[] from = new String[] { DBAdapter.KEY_NAME, "edd",DBAdapter.KEY_ROWID };
	            // the XML defined views which the data will be bound to
	            int[] to = new int[] { R.id.name_entry, R.id.edd_entry,R.id.slno };	
		
		//SimpleCursorAdapter ca=new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item, c, new String [] {DatabaseHelper.colDeptName}, new int []{android.R.id.text1});
		LazyCursorAdapter ca=new LazyCursorAdapter(this,R.layout.list_row,c,from,to);
		//ca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    setListAdapter(ca);
	    lst=(ListView)findViewById(android.R.id.list);
	    lst.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    lst.setOnItemClickListener(new OnItemClickListener()
	    {
	        public void onItemClick(AdapterView<?> adapterView, View v,int position, long arg3)
	        { 
	            lastSel = position;
	            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                 int key_id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 preg_id=key_id;
	        	//Toast.makeText(getApplicationContext(), "" + key_id, Toast.LENGTH_SHORT).show();
	        	try {
		    		String audioFile;
		    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
		    				+ "/mcare/pdata/"+String.valueOf(key_id)+".3gp";
	        		
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
                 int key_id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 lastSel = position;
                 ImageView img=new ImageView(getApplicationContext());
                 String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/pdata/"+
                 String.valueOf(key_id) +".jpg";
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
		  Button btnChange=(Button) findViewById(R.id.btnChange);
		  //btnChange.setTypeface(face);
		  Button btn=(Button) findViewById(R.id.btnNew);
		  //btn.setTypeface(face);
		  btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Context ctx=arg0.getContext();
				Intent intent = new Intent(ctx,preg_entry.class);
				intent.putExtra("slno", mydb.getPregNo()+1);
				intent.putExtra("change", false);
				ctx.startActivity(intent);
			}
		  });
			
			  btnChange.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (preg_id==-1)
							Toast.makeText(getApplicationContext(), "Select Benificiery", Toast.LENGTH_LONG).show();
						else {	
						Context ctx=arg0.getContext();
						Intent intent = new Intent(ctx,preg_entry.class);
						intent.putExtra("slno", preg_id);
						intent.putExtra("change", true);
						ctx.startActivity(intent);
						}
					}
		  });
	}

	@Override
	  protected void onResume() {
	    try {
	    	preg_id=-1;
	    	lastSel=-1;
	    	mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    mydb.close();
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
