package com.mayqlzu.apphere;

import java.util.ArrayList;
import java.util.Iterator;
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
import android.widget.Button;
import android.widget.EditText;

public class BroadcastFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.broadcast_fragment_layout, container, false);
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
    
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
        // register listener to button "broadcast"
    	Activity hostActivity = this.getActivity();
        Button btn_broadcast = (Button)hostActivity.findViewById(R.id.btn_broadcast);
        btn_broadcast.setOnClickListener(new ButtonListener(this));
        btn_broadcast.setEnabled(false);
        EditText editText = (EditText)hostActivity.findViewById(R.id.broadcast_text);
        editText.addTextChangedListener(new MyTextWatcher(btn_broadcast));
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
		private BroadcastFragment m_fragment;
		
		public ButtonListener(BroadcastFragment f){
			m_fragment = f;
		}
		
		@Override
    	public void onClick(View v) {
			Activity hostActivity = m_fragment.getActivity();
			
			// get text
    		EditText editText = (EditText)(hostActivity.findViewById(R.id.broadcast_text));
    		String message = editText.getText().toString();
    		
    		// get numbers and send sms
    		ArrayList<Contact> recvs = m_fragment.getAllReceivers();
    		Iterator<Contact> itor = recvs.iterator();
    		while(itor.hasNext()){
    			Contact c = itor.next();
    			String to = c.m_number;
    			Log.d("BroadcastFragment", "sending: '" + message + "' to: " + to);
    			m_fragment.sendSMS(to, message);
    		}
    		
    		// clear editText
    		editText.setText(null);
    		
    		// hint
    		new AlertDialog.Builder(hostActivity)    
    		                .setMessage("done :)")  
    		                .setPositiveButton("OK", null)  
    		                .show(); 
		}
    }
    	
}