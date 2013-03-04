package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CallTheRollFragment extends Fragment {
	private boolean m_isActive = false; // inactive initially
	private ArrayList<Contact> m_abcent;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calltheroll_fragment_layout, container, false);
    }
    
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
        // register listener
    	Activity hostActivity = this.getActivity();
    	ButtonListener buttonListener = new ButtonListener(this);
    	
        Button btn_start_stop = (Button)hostActivity.findViewById(R.id.btn_start_stop);
        Button btn_broadcast = (Button)hostActivity.findViewById(R.id.btn_broadcast);
        EditText editText = (EditText)hostActivity.findViewById(R.id.broadcast_text);
        
        btn_broadcast.setEnabled(false);
        btn_broadcast.setOnClickListener(buttonListener);
        btn_start_stop.setOnClickListener(buttonListener);
        editText.addTextChangedListener(new MyTextWatcher(btn_broadcast));
        
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
    	while(cursor.moveToNext()){
    		String name = cursor.getString(cursor.getColumnIndex(Database.NAME));
    		String number = cursor.getString(cursor.getColumnIndex(Database.NUMBER));
    		result.add(new Contact(name, number));
    	}
    	
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
				m_isActive = !m_isActive;
				
				// change button text
	    		String text = m_isActive ? 
	    				m_hostActivity.getString(R.string.stop) 
	    				: m_hostActivity.getString(R.string.start);
	    		((Button)v).setText(text);
	    		
	    		// reconstruct or clear member list
	    		if(m_isActive){
	    			m_fragment.m_abcent= m_fragment.getAllReceivers();
	    		}else{
	    			m_fragment.m_abcent.clear();
	    		}
	    		
	    		// prepare items for show
	    		String[] names = Contact.filterNames(m_fragment.m_abcent);
	    		
	    		// refresh UI
	            ListView listView = (ListView)m_hostActivity.findViewById(R.id.abcent_list);
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	            		m_hostActivity,  // not sure: fragment or hostActivity ?
	                    android.R.layout.simple_list_item_1,
	                    names);
	            listView.setAdapter(adapter);
	            break;
	            
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
    		                .setMessage("sent :)")  
    		                .setPositiveButton("OK", null)  
    		                .show();
				break;
            
			}
		}
    }
}