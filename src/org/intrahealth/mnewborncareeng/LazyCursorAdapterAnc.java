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


public class LazyCursorAdapterAnc extends SimpleCursorAdapter  {

	//private Context context;
	private int layout;
	public ImageLoader imageLoader;
	private LayoutInflater inflater=null;
	int nameCol,eddCol,slCol,dobCol,mnCol,seqCol;

	
	public LazyCursorAdapterAnc(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		//this.context = context;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
		imageLoader=new ImageLoader(context);
        nameCol = c.getColumnIndex("name");
        eddCol = c.getColumnIndex("edd");
        dobCol = c.getColumnIndex("svd");
        slCol = c.getColumnIndex("_id");
        mnCol = c.getColumnIndex("mnth");
        seqCol=c.getColumnIndex("seq");
        
		// TODO Auto-generated constructor stub
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View v = inflater.inflate(layout, parent, false);
        Cursor c = getCursor();
        int position= cursor.getPosition();
        if (position == Anc_visit_list.getCurrentSelectedItemId()) {
            v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg_hover));
            } else v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg));

        TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry);
        TextView tvDob = (TextView)v.findViewById(R.id.dob_entry);
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvName.setText(c.getString(nameCol));
//   		String dt_str[]=plmp.split("\\-");
//		dtPicker.init(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]), new OnDateChangedListener() {

        tvEdd.setText("EDD: "+DBAdapter.strtodate(c.getString(eddCol))+" ("+c.getString(mnCol)+")");
        tvSlno.setText(c.getString(slCol));
        if (!c.isNull(dobCol)) 
        	{ 
        	tvDob.setText("Trimester date: "+DBAdapter.strtodate(c.getString(dobCol))+" ["+c.getString(seqCol)+"]");
        	tvDob.setTextColor(Color.BLUE);
        	}
        else tvDob.setText("");
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(slCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

 
        int position= c.getPosition();
        if (position == Anc_visit_list.getCurrentSelectedItemId()) {
            v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg_hover));
            } else v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg));
    	TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry); 
        TextView tvDob = (TextView)v.findViewById(R.id.dob_entry);
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvName.setText(c.getString(nameCol));
        tvEdd.setText("EDD: "+DBAdapter.strtodate(c.getString(eddCol))+" ("+c.getString(mnCol)+")");
        tvSlno.setText(c.getString(slCol));
        if (!c.isNull(dobCol)) { 
        	tvDob.setText("Trimester date: "+DBAdapter.strtodate(c.getString(dobCol))+" ["+c.getString(seqCol)+"]");
        	tvDob.setTextColor(Color.BLUE);
        }
        else tvDob.setText("");
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(slCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

    }
 /*  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
    	//inflater = LayoutInflater.from(context);
        //View view = inflater.inflate(R.layout.preg_reg_list, null);
        //super.getView(position, convertView, parent);
    	View v = inflater.inflate(layout, parent, false);
        if (position == Preg_reg_list.getCurrentSelectedItemId()) {
            v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg_hover));
            }

		return v;
        }
*/
	
}
