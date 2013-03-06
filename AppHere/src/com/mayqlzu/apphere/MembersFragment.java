package com.mayqlzu.apphere;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MembersFragment extends Fragment {
	private SimpleCursorAdapter m_adapter;
	private Database m_db;
	private View m_thisFragmentView; // used by inner class
	private ListView m_listView;	// for convenience
    private Activity m_activity;
	
    public MembersFragment(Database db) {
		super();
		m_db = db;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView =  inflater.inflate(R.layout.members_fragment_layout, container, false);
        
        // saved for convenience
        m_thisFragmentView = thisFragmentView;
        m_listView = (ListView)thisFragmentView.findViewById(R.id.members_listview);
        
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
        
        // register callback on button "delSelected"
        thisFragmentView.findViewById(R.id.btn_delSelected).setOnClickListener(
            	new View.OnClickListener() {
            		public void onClick(View v) {
            			// do nothing if no item checked
            			if(0 == m_listView.getCheckedItemCount())
            				return;
            			
            			// make sure id stable
            			if(false == m_adapter.hasStableIds())
            				System.out.println("adapter.hasStableIds() == false!");
            			
            			// get checked item ids
            			long[] ids = m_listView.getCheckedItemIds();
            			/*
            			for(long id:ids){
            				System.out.println("checked id: " + id);
            			}
            			*/
            			
            			// delete these records from DB by id
            			// todo: ok to use these ids as ids in DB ? so far so good
            			MembersFragment.this.m_db.deleteMembers(ids);
            			
            			// refresh UI
            			MembersFragment.this.refreshListView();
            		}
            	});
        
        
        // register callback on button "fromSMS"
        thisFragmentView.findViewById(R.id.btn_fromSMS).setOnClickListener(
            	new View.OnClickListener() {
            		public void onClick(View v) {
            			Intent intent = new Intent(m_activity, CollectContactsFromSMSActivity.class);
            			/* attention: if A calls startActivityFromResult(),
            			 * only A.onActivityResult() will be called,
            			 * so here, caller should be fragment not it's host activity,
            			 * or your onActivityResult() won't be called
            			 * 
            			 * m_activity.startActivityForResult(intent, 
            			 *		RequestCodes.MEMBERS_FRAGMENT_TO_COLLECT_CONTACTS_FROM_SMS.ordinal());
            			 */
            			MembersFragment.this.startActivityForResult(intent, 
            					RequestCodes.MEMBERS_FRAGMENT_TO_COLLECT_CONTACTS_FROM_SMS.ordinal());
            		}
            	});
        
        // register callback on button "fromPhonebook"
        thisFragmentView.findViewById(R.id.btn_fromPhonebook).setOnClickListener(
            	new View.OnClickListener() {
            		public void onClick(View v) {
            			Intent intent = new Intent(m_activity, PickMembersFromPhonebookActivity.class);
            			MembersFragment.this.startActivityForResult(intent, 
            					RequestCodes.MEMBERS_FRAGMENT_TO_PICK_MEMBERS_FROM_PHONEBOOK.ordinal());
            		}
            	});
        
        EditText name = (EditText)thisFragmentView.findViewById(R.id.text_name);
        EditText number = (EditText)thisFragmentView.findViewById(R.id.text_number);
        Button add = (Button)thisFragmentView.findViewById(R.id.btn_addOneByHand);
        add.setEnabled(false);
        MyTextWatcher textWatcher = new MyTextWatcher(name, number, add);
        name.addTextChangedListener(textWatcher);
        number.addTextChangedListener(textWatcher);
        
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
    	m_activity = this.getActivity();
    	
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
    
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
   		System.out.println("MembersFragment.onActivityResult() called");
    	
    	if((RequestCodes.MEMBERS_FRAGMENT_TO_COLLECT_CONTACTS_FROM_SMS.ordinal() == requestCode
    			&& RequestCodes.MEMBERS_FRAGMENT_TO_COLLECT_CONTACTS_FROM_SMS.ordinal() == resultCode)
    			|| (RequestCodes.MEMBERS_FRAGMENT_TO_PICK_MEMBERS_FROM_PHONEBOOK.ordinal() == requestCode
    	    			&& RequestCodes.MEMBERS_FRAGMENT_TO_PICK_MEMBERS_FROM_PHONEBOOK.ordinal() == resultCode)){
    		
    		String[] names = data.getStringArrayExtra(CONST.names);
    		String[] numbers = data.getStringArrayExtra(CONST.numbers);
    		
    		/* check
    		for(String name:names)
    			System.out.println(name);
    		
    		for(String number:numbers)
    			System.out.println(number);
    		*/
    		
    		if(names.length != numbers.length)
    			System.out.println("names.length != numbers.length, something must be wrong!");
    			
    		for(int i=0; i<names.length; i++){
    			// caller will take care of collision
    			this.m_db.addOneMember(names[i], numbers[i]);
    		}
     
        	// refresh UI
        	this.refreshListView();
    	}
    }
    
    private class MyTextWatcher implements TextWatcher{
    	private EditText m_name;
    	private EditText m_number;
    	private Button m_add;
    	
    	MyTextWatcher(EditText name, EditText number, Button add){
    		m_name = name;
    		m_number = number;
    		m_add = add;
    	}

		@Override
		public void afterTextChanged(Editable arg0) {
			//Log.d("MembersFragment", "afterTextChanged()");
			String name = m_name.getText().toString();
			String number = m_number.getText().toString();
			m_add.setEnabled(name.length()>0 && number.length() >= CONST.VALID_NUMBER_LEN);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
    	
    	
    }
}