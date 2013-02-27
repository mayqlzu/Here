package com.mayqlzu.apphere;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MembersLoader extends AsyncTaskLoader<Cursor>{
	private Database m_db;
	
	public MembersLoader(Context context, Database db) {
		super(context);
		m_db = db;
	}

	public Cursor loadInBackground(){
		return m_db.queryAllMemberNames();
	}
	
	/* 
	 * must override this method and call super.forceLoad() inside, or this.loadInBackground()
	 * won't be called
	 */
	@Override
	protected void onStartLoading (){
		super.forceLoad();
	}
}
