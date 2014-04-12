package org.intrahealth.mnewborncareeng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LMP = "lmp";
	//private static final String TAG = "DBAdapter";
	private static String dbPath= "/data/data/org.intrahealth.mnewborncareeng/databases/"; 
	public static final String DATABASE_NAME = "mnewborncare.db";
	private static final String DB_PREG_TABLE = "preg_reg";
	//private static final int DATABASE_VERSION = 1;
	public static final int qc[]={26,39,52,60,78};
	public static final String mstr[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
	public static final String hmstr[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
	private static DBAdapter mInstance = null;
	int qarr[],anc_qcnt;
	
	private Context context;
//	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	String seq_str;
	
	 public DBAdapter(Context ctx) {		 
		 super(ctx, DATABASE_NAME, null, 1);
		 this.context = ctx;
		 }	
	
	 
	 public static DBAdapter getInstance(Context ctx) {
	        /** 
	         * use the application context as suggested by CommonsWare.
	         * this will ensure that you dont accidentally leak an Activitys
	         * context (see this article for more information: 
	         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
	         */
	        if (mInstance == null) {
	            mInstance = new DBAdapter(ctx.getApplicationContext());
	            try {
					mInstance.createDataBase();
		            mInstance.openDataBase();
	            } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        return mInstance;
	    }
	 
	private boolean checkDataBase(){
        File dbFile = new File( dbPath +  DATABASE_NAME);
        return dbFile.exists();
		}
	
	 private void copyDataBase() throws IOException{
		 try {

		                   InputStream input =  context .getAssets().open(DATABASE_NAME);
		                            String outPutFileName=  dbPath  +  DATABASE_NAME ;
		                      OutputStream output = new FileOutputStream( outPutFileName);
		                       byte[] buffer = new byte[1024];
		                   int length;
		                   while ((length = input.read(buffer))>0){
		                  output.write(buffer, 0, length);
		                   }
		                   output.flush();
		                   output.close();
		                   input.close();
		        }
		                        catch (IOException e) {
		                     Log.v("error",e.toString());
		                     }
		    }

	 
	 /*	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
	
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			/*
			try {
				Log.d(TAG, "Creating database");
				String[] queries = DATABASE_CREATE.split(";");
				 for(String query : queries){
				        db.execSQL(query);
				     }
				//db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			
			db.execSQL("DROP TABLE IF EXISTS [preg_reg]");
			db.execSQL("DROP TABLE IF EXISTS [sch_visits]");
			db.execSQL("Drop Trigger If Exists [trg_insert]");
			db.execSQL("Drop Trigger If Exists [trg_update]");			
			onCreate(db);
		}
	}
	*/
	//---opens the database---
	/*
	public DBAdapter open() throws SQLException
		{
		db = DBHelper.getWritableDatabase();
		return this;
		}
	*/
	
	
	 public void createDataBase() throws IOException{
		 
		 boolean dbExist = checkDataBase();
		  
		 if(dbExist){
		 //do nothing - database already exist
		 	} else {
		  
		 //By calling this method and empty database will be created into the default system path
		 //of your application so we are gonna be able to overwrite that database with our database.
		 		this.getReadableDatabase();
		  
		 try {
		  
		 copyDataBase();
		  
		 } catch (IOException e) {
		  
		 throw new Error("Error copying database");
		  
		 }
		 }
		  
		 }
	
	public void openDataBase() throws SQLException, IOException{
        String fullDbPath= dbPath + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase( fullDbPath,null,SQLiteDatabase.OPEN_READWRITE);
	}
	
	public SQLiteDatabase getDB()
	{
		try {
			openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}
	//---closes the database---
	@Override
	public synchronized void close()
		{
//	      if( db!=null) db.close();
//	       super.close();
		}

	public synchronized void myclose()
		{
	      
			if( db!=null) db.close();
	       super.close();
	       mInstance=null;
		//DBHelper.close();
		}
	
	
	//--New registration Pregnancy--
	public synchronized long insertPreg(String name, String lmp)
	{
	ContentValues initialValues = new ContentValues();
	initialValues.put(KEY_NAME, name);
	initialValues.put(KEY_LMP, lmp);
	initialValues.put("m_stat", 0);
	initialValues.put("c_stat", 0);
	return db.insert(DB_PREG_TABLE, null, initialValues);
	}
	
	public synchronized long logAct(String info)
	{
		Calendar c = Calendar.getInstance(Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String dtime = df.format(c.getTime());
	ContentValues initialValues = new ContentValues();
	initialValues.put("info", info);
	initialValues.put("dtime", dtime);
	return db.insert("actlog", null, initialValues);
	}
	
	public synchronized long insertSms(String msg)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put("msg", msg);		
		return db.insert("pend_sms", null, initialValues);		
	}
	
	public synchronized void sendSMS(String phoneNumber,String msg)
	{
		long l=insertSms(msg);
		Log.d("info", msg+" "+l);
	}
	
	
	public synchronized boolean updateSms(long rowId,int retry)
	{
		ContentValues args = new ContentValues();
		args.put("retry", retry);
		return db.update("pend_sms", args, "_id=" + rowId, null) > 0;
	}
	
	public synchronized boolean deleteSms(long rowId)
	{
		ContentValues args = new ContentValues();
		args.put("sent", 1);
		return db.update("pend_sms", args, "_id=" + rowId, null) > 0;
//		return db.delete("pend_sms", "_id=" + rowId, null) > 0;
	}
	
	//---deletes a particular contact---
	public synchronized boolean deletePreg(long rowId)
		{
		return db.delete(DB_PREG_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
		}
	//---retrieves all the contacts---
	public synchronized Cursor getAllPreg()
		{
		//return db.rawQuery("select * from preg_reg where dob is null order by edd", null);
		String sql="select _id,name,edd,dob,round((julianday('NOW')-julianday(lmp))/30) mnth from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
		return db.rawQuery(sql, null);
		}

	public synchronized Cursor getANCList()
	{
		String sql="select sa._id _id,sa.pid pid,name,edd,svd,avd,sa.seq seq,qseq, "+
				" answr,gsumm,round((julianday('NOW')-julianday(lmp))/30) mnth,hv_str "+
				" from sch_avisits sa "+
				" inner join preg_reg pr on sa.pid=pr._id "+
				" inner join (select pid,min(seq) seq "+
				" from sch_avisits where julianday(svd)>julianday('now') " + 
				" group by pid) tav on sa.pid=tav.pid and sa.seq=tav.seq "+
				" where dob is null";
		return db.rawQuery(sql, null);
	}
	
	public synchronized boolean UpdateQlist(int seq)
	{
		int ind=0;
		boolean res=true;
		String sql="select count(_id) cnt from qb_anc where tri like '%"+String.valueOf(seq)+"%'";
		Cursor c=db.rawQuery(sql,null);
		c.moveToFirst();anc_qcnt=c.getInt(0);
		//Log.d("info","Debug message"+String.valueOf(cnt)+" "+String.valueOf(seq));
		qarr=new int[anc_qcnt];
		sql="select _id from qb_anc where tri like '%"+String.valueOf(seq)+"%'";
		c=db.rawQuery(sql,null);
		if (c.moveToFirst())
			while (!c.isAfterLast())
				{
				qarr[ind++]=c.getInt(0);
				c.moveToNext();
				}
		 else res=false;
		return res;
	}
	
	
	public synchronized Cursor getHVCompList()
	{
	//return db.rawQuery("select * from preg_reg where dob is null order by edd", null);
	String sql="select p.*,avd,(julianday(date(\"now\"))-julianday(svd)) diff from preg_reg p " + 
			"left join sch_visits s on p._id=s.pid and s.seq=7 " + 
			"where (julianday(date(\"now\"))-julianday(svd))>15 or last_visit=7 ";	
	//String sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
	return db.rawQuery(sql, null);
	}
	
	public synchronized Cursor getDthList(String rep_type)
	{
	//return db.rawQuery("select * from preg_reg where dob is null order by edd", null);
		
	String sql="select _id,name,edd,dob,m_stat,c_stat,m_death,c_death, " +
			"round((julianday(c_death)-julianday(dob))/30) mnth from preg_reg "+
			"where (m_stat=1 and m_death is not null) or (c_stat=1 and c_death is not null)";	
	if (rep_type.equals("abrep"))
		sql="select _id,name,dob,edd,m_stat,c_stat,m_death,c_death, "+
				"round((julianday(dob)-julianday(lmp))/30) mnth "+
				"from preg_reg where c_stat=1 and c_death is null";
	//String sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
	return db.rawQuery(sql, null);
	}
	
	
	public synchronized Cursor getAllPregList()
	{
	return db.rawQuery("select _id,name,edd,dob,round((julianday('NOW')-julianday(lmp))/30) mnth from preg_reg where dob is null order by edd", null);
	//String sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
	//return db.rawQuery(sql, null);
	}


	
	
	public synchronized void custom_qry(String sql)
	{
		db.execSQL(sql);
	}
	
	public synchronized Cursor getImmunList()
	{
	return	db.rawQuery("SELECT _id, descr FROM immun", null);
	}
	
	public synchronized String getImmun(long rowid)
	{
		String res="";
		Cursor c=db.rawQuery("select im_id,immun.descr,imdt from imm_det " + 
				"inner join immun on imm_det.im_id=immun._id "+ 
				"where pid="+String.valueOf(rowid)+
				" order by imdt", null);
		c.moveToFirst();
		while (!c.isAfterLast())
		{
			res=res+"|"+c.getString(0)+":"+c.getString(1)+":"+strtodate(c.getString(2));
			c.moveToNext();
		}
		return res;
	}
	
	public synchronized Cursor getPregVisits(String mydt)
	{
		/*String sql=
		"select v._id,v.seq,v.pid,p.name,min(svd) sv_dt,(julianday(svd)-julianday(date("+mydt+"))) diff,p.hv_str,p.m_stat,p.c_stat from sch_visits v "
		+"inner join preg_reg p on p._id=v.pid "
		+"inner join ( "
		+"select pid,min(abs(julianday(svd)-julianday(date("+mydt+")))) mday "
		+" from sch_visits v inner join preg_reg p on p._id=v.pid "
		+"where seq>p.last_visit and avd is null "
		+"group by pid) m on v.pid=m.pid "
		+"where (m_stat<>1 or c_stat<>1) and avd is null and abs(julianday(svd)-julianday(date("+mydt+")))=mday "
		+"group by v.pid "
		+"order by sv_dt";
		*/
		String sql=
		"select sc._id,seq,sc.pid,p.name,svd sv_dt,(julianday(svd)-julianday(date("+mydt+"))) diff,p.hv_str,p.m_stat,p.c_stat "  
		+"from sch_visits sc inner join preg_reg p on p._id=sc.pid "		
		+"inner join (select pid,0 ind,max(svd) svdt from sch_visits v " 
		+"inner join preg_reg p on p._id=v.pid "
		+"where avd is null	and seq>p.last_visit "
		+" and svd between date("+mydt+",'-15 days') and date("+mydt+") "	
		+"group by pid "
		+"UNION "
		+"select pid,1 ind,min(svd) svdt from sch_visits where pid not in "
		+"(select distinct pid from sch_visits v "
		+"inner join preg_reg p on p._id=v.pid "
		+"where avd is null	and seq>p.last_visit "
		+" and svd between date("+mydt+",'-15 days') and date("+mydt+")) " 
		+"and svd>date("+mydt+") "
		+"group by pid) tmp on sc.pid=tmp.pid and sc.svd=tmp.svdt "
		+"where (m_stat<>1 or c_stat<>1) and avd is null "
		+"order by sv_dt";
		return db.rawQuery(sql,null);
	}
	
	public synchronized Cursor getQuest(int grp)
	{
	String sql="select * from q_bank where grp="+String.valueOf(grp)+" order by _id";
	return db.rawQuery(sql, null);
	}
	
	public synchronized int getPregNo()
	{
		Cursor mCursor=db.rawQuery("Select seq From sqlite_sequence where name='preg_reg'", null);
		if (mCursor.moveToFirst())
			return mCursor.getInt(0);
		else return 0;
	}
	
	public synchronized Cursor getNextQuest(int qid,boolean branch,int dstat)
	{
		String sql="select * from q_bank where _id="+String.valueOf(qid);
		if (!branch) sql=sql+" and grp<2";
		if (dstat==1) sql=sql+" and grp<>1 and grp<>4";
		else if(dstat==2) sql=sql+" and grp=1";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getNextQuestAnc(int qid,boolean branch,int seq)
	{
		String sql="select * from qb_anc where _id="+String.valueOf(qid);
		//if (!branch) sql=sql+" and grp<2";
		//if (dstat==1) sql=sql+" and grp<>1 and grp<>4";
		//else if(dstat==2) sql=sql+" and grp=1";
		return db.rawQuery(sql, null);
	}

	
	
	public synchronized Cursor getCatgr(int cid)
	{
		String sql="select * from catgr where _id="+String.valueOf(cid);
		return db.rawQuery(sql, null);		
	}
	
	public synchronized Cursor getCouncInfo(int mnth,int gid)
	{
		String sql;
		if (mnth!=0)
			sql="SELECT i._id _id,prompt,i.msg ctext,ms.msg grphead,help from infolist i " + 
					"inner join msglist ms on i.msg_id=ms._id " + 
					"inner join mnth_disp_order mo on ms._id=mo.msg_id and mo.mnth=" +String.valueOf(mnth)+ 
					" order by mo.disp_order,i._id";
		else sql="SELECT i._id,prompt,i.msg ctext,ms.msg grphead,help from infolist i " + 
					"inner join msglist ms on i.msg_id=ms._id and ms._id=" +String.valueOf(gid)+ 
					" order by i._id";
		Log.d("MyDebug",sql);
		return db.rawQuery(sql, null);		
	}

	
	
	public synchronized Cursor getModList()
	{
		String sql="select _id,module from modlist";
		return db.rawQuery(sql, null);		
	}

	public synchronized Cursor getMsgList(int mod_id)
	{
		String sql="select _id,msg from msglist where mod_id="+String.valueOf(mod_id);
		return db.rawQuery(sql, null);		
	}

	
	
	public synchronized Cursor getAllRem(int cid)
	{
		String sql="select * from remedy where cid="+String.valueOf(cid);
		return db.rawQuery(sql, null);
	}

	public synchronized String getpans(int pid,int qid)
	{
		//int qc[]={26,13,13,8,18,6};
		int pos=qid;
		String res="",col="answrg5";
		
		if (qid<=qc[0]) col="answrg0";
		else if (qid<=qc[1]) { col="answrg1";pos=pos-qc[0]-1; }
		else if (qid<=qc[2]) { col="answrg2";pos=pos-qc[1]-1; }
		else if (qid<=qc[3]) { col="answrg3";pos=pos-qc[2]-1; }
		else if (qid<=qc[4]) { col="answrg4";pos=pos-qc[3]-1; }
		String sql="select "+col+" answr,seq from sch_visits where pid="+String.valueOf(pid)
				+" and avd is not null order by seq";
		Cursor c=db.rawQuery(sql, null);
		c.moveToFirst();seq_str="";
		while (!c.isAfterLast())
		{
			String mstr=c.getString(0);
			String mcharr[]=mstr.split("\\ ");
			res=res+" "+mcharr[pos];
			seq_str=seq_str+" "+c.getInt(1);
			c.moveToNext();
		}
		return res;
	}
	
	public synchronized boolean UpdateQc()
	{
		int qs=0,ind=0;
		boolean res=true;
		String sql="select grp,count(*) cnt from q_bank group by grp";
		Cursor c=db.rawQuery(sql, null);
		if (c.moveToFirst())
			while (!c.isAfterLast())
				{
				qs=qs+c.getInt(1);ind=c.getInt(0);
				qc[ind]=qs;
				c.moveToNext();
				}
		 else res=false;
		return res;
	}
	
	public synchronized Cursor getVisitSummary(int pid,boolean fvisit)
	{
		//select avd,gsumm from sch_visits where pid=1 and seq=1
		String tblName="sch_visits";if (fvisit) tblName="opt_visits";
		String sql="select seq,avd,gsumm from "+tblName+" where avd is not null and pid="+String.valueOf(pid);//+" and seq="+String.valueOf(pid);
		sql=sql+" order by avd desc limit 1";
		return db.rawQuery(sql, null);		
	}

	public synchronized Cursor getAVisitSummary(int pid,int seq)
	{
		//select avd,gsumm from sch_visits where pid=1 and seq=1
		String sql="select seq,avd,gsumm from sch_avisits where pid="+String.valueOf(pid)+" and seq+1="+String.valueOf(seq);
		//sql=sql+" order by avd desc limit 1";
		return db.rawQuery(sql, null);		
	}

	
	public synchronized boolean regChild(long rowId, String dob,int pob,float weight,int m_stat,int c_stat,String sex)
	{
		ContentValues args = new ContentValues();
		args.put("dob", dob);
		args.put("weight", weight);
		args.put("c_stat", c_stat);
		args.put("m_stat", m_stat);
		args.put("pob",pob);
		args.put("sex", sex);
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public synchronized boolean regDeath(long rowId, String doDth,int dstat)
	{
		ContentValues args = new ContentValues();
		
		switch (dstat) {
		case 0:args.put("c_death", doDth);args.put("c_stat", 1);break;
		case 1:args.put("m_death", doDth);args.put("m_stat", 1);break;
		case 2:args.put("m_death", doDth);args.put("c_death", doDth);
			args.put("m_stat", 1);args.put("c_stat", 1);break;
		case 3:	args.put("m_stat", 1);args.put("c_stat", 1);break;
		}		
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
	//---retrieves a particular contact---
	public synchronized Cursor getPreg(long rowId) throws SQLException
		{
		Cursor mCursor =
				db.query(true, DB_PREG_TABLE, new String[] {KEY_ROWID,
						KEY_NAME, KEY_LMP,"EDD","dob","pob","sex","weight","m_stat","c_stat"}, KEY_ROWID + "=" + rowId, null,
						null, null, null, null);
		if (mCursor != null) {
				mCursor.moveToFirst();
			}
		return mCursor;
		}
	
	public synchronized Cursor getPregInfo(long rowId) throws SQLException
	{
	String sql="select p._id,name,lmp,edd,dob,m_stat,c_stat,date(v.avd) avd,last_visit from preg_reg p "+
		"left join sch_visits v on v.pid=p._id and v.seq=p.last_visit"+
		" where p._id="+String.valueOf(rowId);
	Cursor mCursor=db.rawQuery(sql, null);
	if (mCursor != null) mCursor.moveToFirst();
	return mCursor;	
	}
	
	//---updates a contact---
	public synchronized boolean updatePreg(long rowId, String name,String lmp)
		{
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_LMP, lmp);
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		}

	public synchronized boolean updatePregVisitStr(long rowId,String hv_str)
	{
	ContentValues args = new ContentValues();
	args.put("hv_str", hv_str);
	return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
	
	public synchronized boolean updateVisit(long rowId,String answrg0,String answrg1,String answrg2,String answrg3,String answrg4,String gsumm)
	{
		ContentValues args = new ContentValues();
		Calendar c = Calendar.getInstance(Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String avd = df.format(c.getTime());
		args.put("avd", avd);
		args.put("answrg0",answrg0);
		args.put("answrg1", answrg1);
		args.put("answrg2", answrg2);
		args.put("answrg3", answrg3);
		args.put("answrg4", answrg4);
		//args.put("answrg5", answrg5);
		args.put("gsumm", gsumm);
		return db.update("sch_visits", args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	
	public synchronized boolean optVisit(long pid,String answrg0,String answrg1,String answrg2,String answrg3,String answrg4,String gsumm)
	{
		ContentValues args = new ContentValues();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String avd = df.format(c.getTime());
        args.put("pid", pid);
        args.put("svd", avd);
        args.put("seq", 0);
		args.put("avd", avd);
		args.put("answrg0",answrg0);
		args.put("answrg1", answrg1);
		args.put("answrg2", answrg2);
		args.put("answrg3", answrg3);
		args.put("answrg4", answrg4);
		//args.put("answrg5", answrg5);
		args.put("gsumm", gsumm);
		//return db.update("sch_visits", args, KEY_ROWID + "=" + rowId, null) > 0;
		return db.insert("opt_visits", null, args)>0;
	}
	
	public synchronized Cursor getSmsList()
	{
		return db.rawQuery("select _id,msg,retry from pend_sms where sent=0", null);		
	}

	public Cursor rawQuery(String sql)
	{
		return db.rawQuery(sql,null);
	}
	
	@Override
	public synchronized void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized String imm_txt(int immid)
	{
		Cursor c=db.rawQuery("select descr from immun where _id="+String.valueOf(immid), null);
		c.moveToFirst();
		return c.getString(0);
	}
	
	public static String strtodate(String dstr)
	{
	String tmp="-";
		if (dstr!=null)
			{	String dt_str[]=dstr.split("\\-");
				if (dt_str.length>2)
				tmp=dt_str[2]+"-"+hmstr[Integer.parseInt(dt_str[1])-1]+"-"+dt_str[0];
				else tmp=dstr;
			}
		return tmp;
	}
	
	static boolean isAirplaneModeOn(Context context) {

		   return Settings.System.getInt(context.getContentResolver(),
		           Settings.System.AIRPLANE_MODE_ON, 0) != 0;

		}
	
	static Calendar removeTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	
	
	}