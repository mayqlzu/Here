package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class CollectContactsFromSMSActivity extends Activity{
	private String[] m_address;
	private String[] m_body;
	private String[] m_date_sent;
	
	private String[] m_rangeHint = new String[]{ "today's SMS", "today and yesterday's SMS", "All SMS"};
	
	private class MySeekBarListener implements SeekBar.OnSeekBarChangeListener{
		private TextView m_range;
		MySeekBarListener(TextView v){
			m_range = v;
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			m_range.setText(m_rangeHint[progress]);
		}
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.collect_contacts_from_sms_layout);
        
        // register callback for seekBar
        SeekBar seekBar = (SeekBar)this.findViewById(R.id.seekBar);
        TextView range = (TextView)this.findViewById(R.id.range);
        range.setText(m_rangeHint[2]);
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener(range));
        
        
        // register callback for button "cancel"
        Button btn_cancel = (Button)this.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity hostActivity = CollectContactsFromSMSActivity.this;
				hostActivity.finish();
			}
        });
        
        // register callback for button "start"
        Button btn_start = (Button)this.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity hostActivity = CollectContactsFromSMSActivity.this;
				
				// read inbox
				// todo: do it in another thread to avoid UI freeze
				Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), 
						null, null, null, null);
				cursor.moveToFirst();
				// hint user if inbox is empty
				if(false == cursor.moveToFirst()){
	    			new AlertDialog.Builder(hostActivity)    
	                .setMessage("inbox is empty")  
	                .setPositiveButton("OK", null)  
	                .show();
					return;
				}
				ArrayList<SMS> msgs = new ArrayList<SMS>();
		        //System.out.println(msgs.size() + " candidates found");
				do{
					// todo: can not handle Chinese now, "ÂíÓÂÇ¿" will be "???" in inbox; solve it later
				       String address = cursor.getString(cursor.getColumnIndex("address"));
				       String body = cursor.getString(cursor.getColumnIndex("body"));
				       String date_sent = cursor.getString(cursor.getColumnIndex("date_sent"));
				       
				       //System.out.println("body:" + body + " address:" + address + " date_sent:" + date_sent);
				       
				       // how can i filter the candidates?
				       // for now, just filter the ones that has short length and not contain ?, leave user to filter again
				       if(body.length() < 10 && !body.contains("?")){
				    	  msgs.add(new SMS(address, body, date_sent));
				       }
				}while(cursor.moveToNext());
				
				// arrange data into 3 arrays
				// save them in member variables cause i will use them in other functions
				int count = msgs.size();
				m_address 	= new String[count]; 
				m_body 		= new String[count];
				m_date_sent = new String[count];
				for(int i=0; i<count; i++){
					SMS msg = msgs.get(i);
					m_address[i] = msg.m_address;
					m_body[i] = msg.m_body;
					m_date_sent[i] = msg.m_date_sent;
				}
				
				// start next Activity: FilterCandidatesActivity
				Intent intent = new Intent(hostActivity, FilterCandidatesActivity.class);
				intent.putExtra(CONST.address, m_address);
				intent.putExtra(CONST.body, m_body);
				intent.putExtra(CONST.date_sent, m_date_sent);
				//System.out.println("check emun ordinal: " + RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal());
				hostActivity.startActivityForResult(intent, 
						RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal());
			}
		});
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("onActivityResult() called");
    	
    	if(RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal() == requestCode
    			&& RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal() == resultCode){
    		int[] selectedIndice = data.getIntArrayExtra(CONST.selected_indice);
    		
    		/* check
    		for(int i: selectedIndice){
    			System.out.println(i);
    		}
    		*/
    		
    		// filter user choice from all by index
			// as user has filtered them, i call body as name and call address as number from now on :)
    		ArrayList<String> selectedNames = new ArrayList<String>();
    		ArrayList<String> selectedNumbers = new ArrayList<String>();
    		for(int i: selectedIndice){
    			selectedNames.add(m_body[i]);
    			selectedNumbers.add(m_address[i]);
    		}
    		
    		// ArrayList<String> => String[]
    		String[] strArrNames = new String[selectedIndice.length];
    		String[] strArrNumbers = new String[selectedIndice.length];
    		
    		for(int i=0; i<selectedNames.size(); i++)
    			strArrNames[i] = selectedNames.get(i);
    		
    		for(int i=0; i<selectedNumbers.size(); i++)
    			strArrNumbers[i] = selectedNumbers.get(i);
    		
    		// return user choice to caller
			Intent intent = new Intent();
			intent.putExtra(CONST.names, strArrNames);
			intent.putExtra(CONST.numbers, strArrNumbers);
			this.setResult(
					// set result code with a same value of request code of caller
					RequestCodes.MEMBERS_FRAGMENT_TO_COLLECT_CONTACTS_FROM_SMS.ordinal(),
					intent);
			
			// finish myself, caller will create a new instance next time
			this.finish();
    	}
    }
}
