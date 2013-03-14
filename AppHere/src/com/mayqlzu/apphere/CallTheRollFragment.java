package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CallTheRollFragment extends Fragment {
	private boolean m_isActive = false; // inactive initially
	private ArrayList<Contact> m_abcent = new ArrayList<Contact>();
	private SMSReceiver m_receiver;
	private Contact m_callee;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	System.out.println("onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calltheroll_fragment_layout, container, false);
    }
    
    public void onActivityCreated (Bundle savedInstanceState){
        System.out.println("onActivityCreated");
        
    	super.onActivityCreated(savedInstanceState);
    	
        // register listener
    	Activity hostActivity = this.getActivity();
    	ButtonListener buttonListener = new ButtonListener(this);
    	
        Button btn_start_stop = (Button)hostActivity.findViewById(R.id.btn_start_stop);
        Button btn_broadcast = (Button)hostActivity.findViewById(R.id.btn_broadcast);
        EditText editText = (EditText)hostActivity.findViewById(R.id.broadcast_text);
    	ListView listView = (ListView)hostActivity.findViewById(R.id.abcent_list);
        Button btn_call = (Button)hostActivity.findViewById(R.id.btn_call);
        
        btn_broadcast.setEnabled(false);
        btn_broadcast.setOnClickListener(buttonListener);
        btn_start_stop.setOnClickListener(buttonListener);
        btn_call.setOnClickListener(buttonListener);
        editText.addTextChangedListener(new MyTextWatcher(btn_broadcast));
        updateStatusInfoVisiblity();
        
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new MyOnItemClickListener(this));
        
    }
    
    // return the Contact for convenience
    private Contact rememberCallee(int index){
    	return m_callee = m_abcent.get(index);
    }
    
    private class MyOnItemClickListener implements OnItemClickListener{
    	private CallTheRollFragment m_fragment;
    	private ListView m_list;
    	private Button m_btn_call;
    	MyOnItemClickListener(CallTheRollFragment fragment){
    		m_fragment = fragment;
    		m_list = (ListView) m_fragment.getActivity().findViewById(R.id.abcent_list);
    		m_btn_call = (Button) m_fragment.getActivity().findViewById(R.id.btn_call);
    	}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String TAG = "CallTheRollFragment.MyOnItemClickListener.onItemClick()";
			/*
			 * for ArrayList, position == id
			 */
			Log.d(TAG, "item clicked, position="
					+ position + " id=" + id);
			/* 
			 * you can NOT keep persistent focus for an list item, because
			 * In touch mode, there is no focus and no selection,
			 * read section "touch mode" in doc for detail
			 * 
			 * focused, selected and checked are different;
			 * in touch mode, you can keep item checked if you called setChoiseMode() ahead, 
			 * but you can NOT keep item focused or selected
			 */
			String call = view.getContext().getString(R.string.call);
			Contact callee = m_fragment.rememberCallee(position);
			String name = callee.m_name;
			/* i can not keep item selected, so i ask the button to show the selected name
			 * another solution is item.setBackgroundColor(A_DIFFERENT_COLOR), not used for now
			 */
			m_btn_call.setText(call + ": " + name);
		}
    }
    
    public Contact getContact(int index){
    	return m_abcent.get(index);
    }
    
    private void sendSMS(String toNumber, String message){
    	SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(toNumber, null, message, null, null);
    }
    
    public ArrayList<Contact> getAllReceivers(){
    	ArrayList<Contact> result = new ArrayList<Contact>();
    	
    	MainActivity hostActivity = (MainActivity)(this.getActivity());
    	Cursor cursor = hostActivity.getDB().queryAllNamesAndNumbers();
    	if(false == cursor.moveToFirst())
    		return result;
    	do{
    		String name = cursor.getString(cursor.getColumnIndex(Database.NAME));
    		String number = cursor.getString(cursor.getColumnIndex(Database.NUMBER));
    		result.add(new Contact(name, number));
    	}while(cursor.moveToNext());
    	
    	return result;
    }
    
    private class MyTextWatcher implements TextWatcher{
    	private Button m_btn_broadcast;
    	
    	MyTextWatcher(Button b){
    		m_btn_broadcast = b;
    	}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			//Log.d("MyTextWatcher", "afterTextChanged");
			m_btn_broadcast.setEnabled(arg0.length()>0);
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
    
    private class ButtonListener implements View.OnClickListener{
		private CallTheRollFragment m_fragment;
		private Activity m_hostActivity;
		private String m_tag = "CallTheRollFragment.Listener";
		
		public ButtonListener(CallTheRollFragment f){
			m_fragment = f;
			m_hostActivity = m_fragment.getActivity();
		}
		
		@Override
    	public void onClick(View v) {
			//Log.d(m_tag, "onClicked");
			
			switch(v.getId()){
			case R.id.btn_start_stop:
				//Log.d(m_tag, "btn_start_stop clicked");
				
				// switch status
				m_fragment.m_isActive = !m_fragment.m_isActive;
				
				// change button text
	    		String text = m_fragment.m_isActive ? 
	    				m_hostActivity.getString(R.string.stop) 
	    				: m_hostActivity.getString(R.string.start);
	    		((Button)v).setText(text);
	    		
	    		// reconstruct or clear member list
	    		if(m_fragment.m_isActive){
	    			m_fragment.m_abcent= m_fragment.getAllReceivers();
	    		}else{
	    			m_fragment.m_abcent.clear();
	    		}
	    		
	    		// prepare items for show
	    		String[] names = Contact.filterNames(m_fragment.m_abcent);
	    		
	    		// refresh UI
	    		m_fragment.refreshListUI();
	            
	            if(m_fragment.m_isActive){
	            	/* 1) you have to register broadcastReceiver to Activity, not to Fragment
	            	 * 2) remember this receiver and you must unregister the same receiver later,
	            	 * 		or you will get an exception
	            	 */
	            	m_fragment.m_receiver = new SMSReceiver(m_fragment);
	            	m_hostActivity.registerReceiver(
	            		m_fragment.m_receiver,
	            		new IntentFilter(SMSReceiver.SMS_RECEIVED));
	            }else{
	            		m_hostActivity.unregisterReceiver(m_fragment.m_receiver);
	            }
	            
	    		m_fragment.updateStatusInfoText("?");
	            // show or hide status info
	    		m_fragment.updateStatusInfoVisiblity();
	            
	            break; // do NOT forget
	            
			case R.id.btn_broadcast:
				//Log.d(m_tag, "btn_broadcast clicked");
				
				// get receiver numbers
				String[] numbers = Contact.filterNumbers(m_fragment.m_abcent);
				
				// get text
	    		EditText editText = (EditText)(m_hostActivity.findViewById(R.id.broadcast_text));
	    		String message = editText.getText().toString();
	    		
	    		// send
				for(String n: numbers){
					m_fragment.sendSMS(n, message);
				}
				
				// clear ediText
				editText.setText(null);
				
				// hint
    			new AlertDialog.Builder(m_hostActivity)    
    		                .setMessage(m_hostActivity.getString(R.string.sent))  
    		                .setPositiveButton(m_hostActivity.getString(R.string.ok), null)  
    		                .show();
	            break;
			case R.id.btn_call:
				Contact callee = m_fragment.getCallee();
				if(null == callee)
					return;
				String toNumber = callee.m_number;
				Uri uri = Uri.parse("tel:" + toNumber);
				Intent intent = new Intent(Intent.ACTION_CALL, uri);
				startActivity(intent);

				break;
            
			}
		}
    }
    
    public Contact getCallee(){
    	return m_callee;
    }
    
    private void refreshListUI(){
    	String[] names = Contact.filterNames(m_abcent);
    	Activity hostActivity = this.getActivity();
    	ListView listView = (ListView)hostActivity.findViewById(R.id.abcent_list);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    		hostActivity,  // not sure: fragment or hostActivity ?
            android.R.layout.simple_list_item_1,
            names);
    	listView.setAdapter(adapter);
    }

    // created myself, not system callback API
	public void onSMSArrived(String fromNumber, String message) {
		Log.d("CallTherRollFragment", "onSMSArrived()");
		// ignore msg other than "here"
		if(!message.equals(CONST.HERE))
			return;
		
		// update m_abcent if found
		String associatedNameInList = removeMemberFromArrayIfFound(fromNumber);
		if(null != associatedNameInList){
			refreshListUI();
		
			// update status info text
			updateStatusInfoText(associatedNameInList);
		}
	}
	
	// do NOT refresh UI in this function, caller will take care of it
	private String removeMemberFromArrayIfFound(final String number){
		/* 
		 * attention: do not delete any item when iterating, or you may introduce chaos
		 * so i remember the index and delete it later, do in 2 steps
		 */
		int index_found = -1;  //means not found
		for(int i=0; i<m_abcent.size(); i++){
			if(number.equals(m_abcent.get(i).m_number)){
				index_found = i;
				// assume numbers are all unique, so find one is enough
				break;
			}
		}
		if(index_found >= 0){
			Contact removedItem = m_abcent.remove(index_found);
			return removedItem.m_name;
		}
		
		return null;
	}
	
	private void updateStatusInfoVisiblity(){
		// decide visibility first
		TextView statusTextView = (TextView)(this.getActivity().findViewById(R.id.status));
		int newStatus = m_isActive ? View.VISIBLE: View.INVISIBLE;
		statusTextView.setVisibility(newStatus);
	}
	
	private void updateStatusInfoText(String who){
		String newStatus = who + " " + this.getString(R.string.arrived) + ", " 
					+ m_abcent.size() + " " + this.getString(R.string.left);
		TextView textView = (TextView)(this.getActivity().findViewById(R.id.status));
		textView.setText(newStatus);
	}
}