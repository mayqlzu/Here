package com.mayqlzu.apphere;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FilterCandidatesActivity extends Activity{
	private String[] m_address;
	private String[] m_body;
	private String[] m_date_sent;
	
	private class Listener implements View.OnClickListener{
		private ListView m_list;
		private Activity m_activity;
		private String[] m_address;
		private String[] m_body;
		private String[] m_date_sent;
		
		public Listener(ListView list, Activity act, String[] add, String[] body, String[] date){
			m_list = list;
			m_activity = act;
			m_address = add;
			m_body = body;
			m_date_sent = date;
		}

		@Override
		public void onClick(View v) {
			ArrayList<String> checkedItems = new ArrayList<String>();
			ArrayAdapter<String> adapter = (ArrayAdapter<String>) m_list.getAdapter();
			
			if(R.id.btn_choose_all == v.getId()){
				//System.out.println("btn_choose_all clicked");
				int total = adapter.getCount();
				for(int i=0; i<total; i++){
					checkedItems.add(adapter.getItem(i));
				}
			}else if(R.id.btn_choose_marked_only == v.getId()){
				//System.out.println("btn_markedOnly clicked");
				
				// has unstable ids, so can NOT use this line
				// long[] ids = m_list.getCheckedItemIds();
				
				/* API bug? 
				 * when i only check the second item, boolArr only contains the status of the first item, why?
				 * so, check and uncheck every items ahead, and i get reasonable result now :)
				 */
				SparseBooleanArray boolArr = m_list.getCheckedItemPositions();
				int size = boolArr.size();
				for(int i=0; i<size; i++){
					//System.out.println(i + ": " + boolArr.get(i) + " " + adapter.getItem(i));
					if(boolArr.get(i)){
						//System.out.println(adapter.getItem(i));
						checkedItems.add(adapter.getItem(i));
					}
				}
			}else{
				//System.out.println("btn_cancel clicked");
				// cancel
				// do nothing
			}
			
			// list adapter has UNSTABLE ids, so i have to match them one by one
			ArrayList<Integer> selectedIndice = new ArrayList<Integer>();
			int total = m_address.length;
			for(int i=0; i<total; i++){
				// keep consistency with format of m_itemsShown
				String combined = m_body[i] + " " + m_address[i];
				if(checkedItems.contains(combined)){
					selectedIndice.add(i);
				}
			}
			
			// check selected indice before sent back to caller
			/*
			System.out.println("check selected items before sent back:");
			for(int i: selectedIndice){
				System.out.println(m_body[i] + " " + m_address[i]);
			}
			*/
			
			// Array<Integer> => int[], toArray() is dangerous
			int[] cookedIndice = new int[selectedIndice.size()];
			for(int i=0; i<selectedIndice.size(); i++){
				cookedIndice[i] = selectedIndice.get(i);
			}
			
			Intent intent = new Intent();
			intent.putExtra(CONST.selected_indice, cookedIndice);
			m_activity.setResult(
					// set result code with a same value of request code of caller
					RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal(),
					intent);
			
			// create a new activity if the caller sent intent to me again
			m_activity.finish();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.filter_candidates_layout);
        
        // receive param from caller
        Intent intent = this.getIntent();
        m_address = intent.getStringArrayExtra(CONST.address);
        m_body = intent.getStringArrayExtra(CONST.body);
        m_date_sent = intent.getStringArrayExtra(CONST.date_sent);
        
        // convert 3 array to one
        // attention: keep consistency with the comparison algorithm between array and list
        String[] itemsToShow = new String[m_address.length];
        for(int i=0; i<m_address.length; i++){
        	itemsToShow[i] = m_body[i] + " " + m_address[i];
        }
        
        // fill listView with items
        ListView listView = (ListView)this.findViewById(R.id.candidates_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, 
                android.R.layout.simple_list_item_multiple_choice, 
                itemsToShow);  
        // attention: this adapter has UNSTABLE ids
        // System.out.println("hasStableIds? " + adapter.hasStableIds());
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); 
        // fix an API bug(maybe it's a bug)
		for(int i=0; i<listView.getCount(); i++)
			listView.setItemChecked(i, false); // the same as check and uncheck action
        
        // handle event from button "chooseAll"
        Listener listener = new Listener(listView, this, m_address, m_body, m_date_sent);
        Button btn_chooseAll = (Button)this.findViewById(R.id.btn_choose_all);
        Button btn_markedOnly = (Button)this.findViewById(R.id.btn_choose_marked_only);
        Button btn_cancel = (Button)this.findViewById(R.id.btn_cancel);
        btn_chooseAll.setOnClickListener(listener);
        btn_markedOnly.setOnClickListener(listener);
        btn_cancel.setOnClickListener(listener);
    }

}
