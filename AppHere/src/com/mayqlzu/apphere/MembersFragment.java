package com.mayqlzu.apphere;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MembersFragment extends Fragment {
	private SimpleCursorAdapter m_adapter;
	private Database m_db;
	private View m_thisFragmentView; // used by inner class
	
    public MembersFragment(Database db) {
		super();
		m_db = db;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView =  inflater.inflate(R.layout.members_fragment_layout, container, false);
        m_thisFragmentView = thisFragmentView;
        
        // register listener to button "addOneByHand"
        thisFragmentView.findViewById(R.id.btn_addOneByHand).setOnClickListener(
        	new View.OnClickListener() {
        		public void onClick(View v) {
        			System.out.println("button addOneByHand clicked");
        			// Perform action on click
        			View fragmentViewInOuterClass = MembersFragment.this.m_thisFragmentView;
        			String name = ((EditText)(fragmentViewInOuterClass.findViewById(R.id.text_name))).getText().toString();
        			String number = ((EditText)(fragmentViewInOuterClass.findViewById(R.id.text_number))).getText().toString();
        			
        			/* 
        			 * insert new record to database
        			 * deem jeff 123 and jeff 124 as two persons with same name but different numbers
        			 */
        			MembersFragment.this.m_db.addOneMember(name, number);
     
        			// refresh UI
        			MembersFragment.this.refreshListView();
        		}
        	});
        
        // register listener to button "deleteAll"
        thisFragmentView.findViewById(R.id.btn_deleteAll).setOnClickListener(
        	new View.OnClickListener() {
        		public void onClick(View v) {
        			// delete all record from DB;
        			MembersFragment.this.m_db.deleteAllMembers();
        			
        			// refresh UI
        			MembersFragment.this.refreshListView();
        		}
        	});
        
        
        return thisFragmentView;
    }
	
	
	private void refreshListView(){
        // refresh UI, how ?
        // yes, it works, haha :)
        // 0 is the loader id, must be the same with the id i used in initLoader()
        getLoaderManager().getLoader(0).forceLoad();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
            	m_adapter.notifyDataSetChanged();
            }
        });
	}
    
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
        ListView listView = (ListView)this.getActivity().findViewById(R.id.members_listview);
        m_adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, null,
                new String[] { Database.NAME },
                new int[] { android.R.id.text1 }, 0); //you can use android.R.id.text2 if want to show more
        listView.setAdapter(m_adapter);
        /*
         *  add this line and user can set checkmark now, 
         *  it's supported by system by default,
         *  do NOT need to set checkmark in event handler manually. 
         */
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
    	getLoaderManager().initLoader(0, // loader id
    			null, // Optional arguments to supply to the loader at construction
    			new LoaderCallbacksForMemberList(getActivity(), m_adapter, m_db) // A LoaderManager.LoaderCallbacks implementation
    			);
    }
}