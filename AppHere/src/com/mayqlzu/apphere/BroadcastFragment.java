package com.mayqlzu.apphere;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    	if(0 == cursor.getCount())
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
        hostActivity.findViewById(R.id.btn_broadcast).setOnClickListener(new Listener(this));
    }
    
	private class Listener implements View.OnClickListener{
		private BroadcastFragment m_fragment;
		
		public Listener(BroadcastFragment f){
			m_fragment = f;
		}
		
		@Override
    	public void onClick(View v) {
			Activity hostActivity = m_fragment.getActivity();
			
			// get text
    		String message = ((EditText)(hostActivity.findViewById(R.id.broadcast_text))).getText().toString();
    		if(0 == message.length()){
    			new AlertDialog.Builder(hostActivity)    
    		                .setMessage("empty :(")  
    		                .setPositiveButton("OK", null)  
    		                .show();
    			return;
    		}
    		
    		// get numbers and send sms
    		ArrayList<Contact> recvs = m_fragment.getAllReceivers();
    		Iterator<Contact> itor = recvs.iterator();
    		while(itor.hasNext()){
    			Contact c = itor.next();
    			String to = c.m_number;
    			Log.d("BroadcastFragment", "sending: '" + message + "' to: " + to);
    			m_fragment.sendSMS(to, message);
    		}
    		
    		new AlertDialog.Builder(hostActivity)    
    		                .setMessage("done :)")  
    		                .setPositiveButton("OK", null)  
    		                .show(); 
		}
    }
    	
}