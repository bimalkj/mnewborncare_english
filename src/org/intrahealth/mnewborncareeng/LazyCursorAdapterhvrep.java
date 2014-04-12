package org.intrahealth.mnewborncareeng;

import java.io.File;

import org.intrahealth.mnewborncareeng.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
//import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class LazyCursorAdapterhvrep extends SimpleCursorAdapter  {

	//private Context context;
	private int layout;
	public ImageLoader imageLoader;
	private LayoutInflater inflater=null;
	int nameCol,sv_dtCol,diffCol,slCol,slHvInd,pidCol,mstInd,cstInd;
	Typeface face;

	
	public LazyCursorAdapterhvrep(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		//this.context = context;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
		imageLoader=new ImageLoader(context);
        nameCol = c.getColumnIndex("name");
        sv_dtCol = c.getColumnIndex("avd");
        slCol = c.getColumnIndex("_id");
        pidCol = c.getColumnIndex("_id");
        diffCol = c.getColumnIndex("diff");
        slHvInd=c.getColumnIndex("hv_str");
        mstInd=c.getColumnIndex("m_stat");
        cstInd=c.getColumnIndex("c_stat");        
        face=Typeface.createFromAsset(context.getAssets(),"monospace821.ttf");
        
		// TODO Auto-generated constructor stub
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

		//int diff;
		String tmp="";
        View v = inflater.inflate(layout, parent, false);
        Cursor c = getCursor();
        int position= c.getPosition();
        if (position == Reporthv.getCurrentSelectedItemId()) {
            v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg_hover));
            } else v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg));

      //  cpos=c.getInt(c.getColumnIndex("seq"));
        TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry); 
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        TextView tvHvInd = (TextView)v.findViewById(R.id.home_visit_ind);
        TextView tvHvLeg= (TextView)v.findViewById(R.id.home_visit_leg);
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        if (c.getInt(mstInd)==1) tmp="*";
        if (c.getInt(cstInd)==1) tmp="**";
        tvName.setText(tmp+c.getString(nameCol));
        tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));
        tvSlno.setText(c.getString(pidCol));
        tvHvInd.setTypeface(face);
        tvHvLeg.setTypeface(face);
        tvHvInd.setText(str_fmt_visit(c.getString(slHvInd)));
        //diff=c.getInt(diffCol);
        //if (diff==0) tvName.setTextColor(Color.GREEN);
        //else if (diff<0) tvName.setTextColor(Color.RED);
        //else tvName.setTextColor(Color.BLACK);        
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(pidCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {

    	//int diff;
    	String tmp="";
    	Cursor c = getCursor();
    	//cpos=c.getInt(c.getColumnIndex("seq"));
    	   int position= c.getPosition();
           if (position == Reporthv.getCurrentSelectedItemId()) {
               v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg_hover));
               } else v.setBackgroundDrawable(v.getContext().getResources().getDrawable(R.drawable.gradient_bg));
    	TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry); 
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        TextView tvHvInd = (TextView)v.findViewById(R.id.home_visit_ind);
        TextView tvHvLeg= (TextView)v.findViewById(R.id.home_visit_leg);
        if (c.getInt(mstInd)==1) tmp="*";
        if (c.getInt(cstInd)==1) tmp=tmp+"*";
        tvName.setText(c.getString(nameCol));
        tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));
        tvSlno.setText(c.getString(pidCol));
        tvHvInd.setText(str_fmt_visit(c.getString(slHvInd)));
        tvHvInd.setTypeface(face);
        tvHvLeg.setTypeface(face);
        //diff=c.getInt(diffCol);
        //if (diff==0) tvName.setTextColor(Color.GREEN);
        //else if (diff<0) tvName.setTextColor(Color.RED);
        //else tvName.setTextColor(Color.BLACK);        
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "mcare/pdata/"+
        		c.getString(pidCol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

    }
	/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    View currView = super.getView(position, convertView, parent);
    Object currItem = getItem(position);
    if (currItem.isItemSelected) {
        currView.setBackgroundColor(Color.RED);
    } else {
        currView.setBackgroundColor(Color.BLACK);
    }
    return currView;
    }  
    */
	  private String str_fmt_visit(String instr)
	  {
		  //Format the string taken from database
		  String tmp="|",tick="\u221A",tch;
		  char mch;
		  for(int i=0;i<instr.length();i++)
		  {
			mch=instr.charAt(i);  
			if (mch=='1') tch=tick;
			else tch="x";
			//else if (cpos==i+1) tch="*";
			//else tch=" ";
			tmp=tmp+" "+tch+" |";
		  }
		return tmp;
	  }



}
