package org.intrahealth.mnewborncareeng;

import java.io.File;

import org.intrahealth.mnewborncareeng.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
//import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class LazyCursorAdapterdth extends SimpleCursorAdapter  {

	//private Context context;
	private int layout;
	public ImageLoader imageLoader;
	private LayoutInflater inflater=null;
	int nameCol,mdthCol,slCol,cdthCol,dobCol,mstCol,mnthCol;

	
	public LazyCursorAdapterdth(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		//this.context = context;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
		imageLoader=new ImageLoader(context);
        nameCol = c.getColumnIndex("name");
        mdthCol = c.getColumnIndex("m_death");
        cdthCol = c.getColumnIndex("c_death");
        dobCol = c.getColumnIndex("dob");
        slCol = c.getColumnIndex("_id");
        mstCol=c.getColumnIndex("m_stat");
        mnthCol=c.getColumnIndex("mnth");
		// TODO Auto-generated constructor stub
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View v = inflater.inflate(layout, parent, false);
        Cursor c = getCursor();

        TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry);
        TextView tvDob = (TextView)v.findViewById(R.id.dob_entry);
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvName.setText(c.getString(nameCol));
//   		String dt_str[]=plmp.split("\\-");
//		dtPicker.init(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]), new OnDateChangedListener() {

        tvSlno.setText(c.getString(slCol));
        tvDob.setText("-");tvEdd.setText("--");
        if (c.isNull(mdthCol) && c.isNull(cdthCol)) 
        	{
        	if (c.getInt(mstCol)==1) {
        		tvDob.setText("Abortion : "+DBAdapter.strtodate(c.getString(dobCol))+
        				"("+c.getInt(mnthCol)+")");
        		tvEdd.setText("");
        		} else {
            		tvDob.setText("Still birth : "+DBAdapter.strtodate(c.getString(dobCol))+
            				"("+c.getString(mnthCol)+")");
            		tvEdd.setText("");        			
        		}
        	}
        else
        if (!c.isNull(mdthCol))
        	tvEdd.setText("Death mother : "+DBAdapter.strtodate(c.getString(mdthCol)));
        if (!c.isNull(cdthCol)) 
        	{ 
        	tvDob.setText("Death child : "+DBAdapter.strtodate(c.getString(cdthCol))+
    				"("+c.getString(mnthCol)+")");
        	tvDob.setTextColor(Color.BLUE);
        	}
        //else tvDob.setText("");
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(slCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry); 
        TextView tvDob = (TextView)v.findViewById(R.id.dob_entry);
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvName.setText(c.getString(nameCol));
        
        tvSlno.setText(c.getString(slCol));
        tvDob.setText("--");tvEdd.setText("--");
        if (c.isNull(mdthCol) && c.isNull(cdthCol)) 
        	{
        	if (c.getInt(mstCol)==1) {
        		tvDob.setText("Abortion : "+DBAdapter.strtodate(c.getString(dobCol))+
        				"("+c.getInt(mnthCol)+")");
        		tvEdd.setText("");
        		} else {
            		tvDob.setText("Death child : "+DBAdapter.strtodate(c.getString(dobCol))+
            				"("+c.getInt(mnthCol)+")");
            		tvEdd.setText("");        			
        		}
        	}
        else
        if (!c.isNull(mdthCol))
        	tvEdd.setText("Death mother : "+DBAdapter.strtodate(c.getString(mdthCol)));
        if (!c.isNull(cdthCol)) 
        	{ 
        	tvDob.setText("Death child : "+DBAdapter.strtodate(c.getString(cdthCol))+
        			"("+c.getInt(mnthCol)+")");
        	tvDob.setTextColor(Color.BLUE);
        	}
        //else tvDob.setText("");        
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(slCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

    }
}
