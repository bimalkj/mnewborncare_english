package org.intrahealth.mnewborncareeng;

import org.intrahealth.mnewborncareeng.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class Counc_modlist extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	int id=0;
	String mname;
	Cursor c;
	String[] items;
	int[] keys;
	 

	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.counc_modlist);
	    //TextView tvHead=(TextView)findViewById(R.id.tvHead);
		    mydb=DBAdapter.getInstance(getApplicationContext());
	    c=mydb.getModList();
	    startManagingCursor(c);
	    String[] from = new String[] {"module"};
	    int[] to = new int[] {R.id.tvItem};
	    SimpleCursorAdapter ca=new SimpleCursorAdapter(getApplicationContext(),R.layout.list_sel_row,c,from,to);
	    setListAdapter(ca);
	    ListView lst=(ListView)findViewById(android.R.id.list);
	    lst.setOnItemClickListener(new OnItemClickListener()
	    {

			public void onItemClick(AdapterView<?> adapterView,  View v,int position, long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
				Intent intent = new Intent(getApplicationContext(),Counc_msglist.class);
				intent.putExtra("id", cursor.getInt(0));
				startActivity(intent);
			}

	    });
	  }
	
}
