package com.mayqlzu.apphere;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class LoaderCallbacksForMemberList implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter m_adapter;
	private Activity m_activity;
	private Database m_db;

	public LoaderCallbacksForMemberList(Activity a, SimpleCursorAdapter adapter, Database db) {
		super();
		m_activity = a;
		m_adapter = adapter;
		m_db = db;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new MembersLoader(m_activity, m_db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        m_adapter.swapCursor(data);
	}

	@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        m_adapter.swapCursor(null);
    }
}