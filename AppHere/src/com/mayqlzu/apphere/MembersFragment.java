package com.mayqlzu.apphere;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MembersFragment extends Fragment {
	private SimpleCursorAdapter m_adapter;
	private Database m_db;
	
    public MembersFragment(Database db) {
		super();
		m_db = db;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView =  inflater.inflate(R.layout.members_fragment_layout, container, false);
        
        return thisFragmentView;
    }
    
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
        ListView listView = (ListView)this.getActivity().findViewById(R.id.members_listview);
        m_adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, null,
                new String[] { Database.NAME },
                new int[] { android.R.id.text1 }, 0);
        listView.setAdapter(m_adapter);
        
    	getLoaderManager().initLoader(0, // loader id
    			null, // Optional arguments to supply to the loader at construction
    			new LoaderCallbacksForMemberList(getActivity(), m_adapter, m_db) // A LoaderManager.LoaderCallbacks implementation
    			);
    }
}