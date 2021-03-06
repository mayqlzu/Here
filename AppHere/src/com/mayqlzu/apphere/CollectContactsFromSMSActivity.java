package com.mayqlzu.apphere;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class CollectContactsFromSMSActivity extends Activity{
	private String[] m_address;
	private String[] m_body;
	private String[] m_date_sent;
	
	private String[] m_rangeHint;
	
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
        
        m_rangeHint = new String[]{this.getString(R.string.todays),
        		this.getString(R.string.today_and_yesterdays),
        		this.getString(R.string.allSMS)};
        
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
				
				// prepare date range
				Date start_of_today = new Date(); //current time
				start_of_today.setHours(0);
				start_of_today.setMinutes(0);
				start_of_today.setSeconds(0);
				
				long million_seconds_one_day = 24*60*60*1000;
				Date start_of_yesterday = new Date(start_of_today.getTime() - million_seconds_one_day);
				
				// set selection condition
				SeekBar seekBar = (SeekBar)hostActivity.findViewById(R.id.seekBar);
				int progress = seekBar.getProgress();
				String selection = "";
				if(0 == progress){
					selection = "date_sent > " + start_of_today.getTime();
				}else if(1 == progress){
					selection = "date_sent > " + start_of_yesterday.getTime();
				}else{
					selection = "";
				}
				
				//Log.d("filter SMS", selection);
				
				Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), 
						null, selection, null, null);
				
				// hint user if no valid sms found
				if(false == cursor.moveToFirst()){
	    			new AlertDialog.Builder(hostActivity)    
	                .setMessage(hostActivity.getString(R.string.no_contacts_found))  
	                .setPositiveButton(hostActivity.getString(R.string.ok), null)  
	                .show();
					return;
				}
				ArrayList<SMS> msgs = new ArrayList<SMS>();
		        //System.out.println(msgs.size() + " candidates found");
				do{
					// todo: can not handle Chinese now, "����ǿ" will be "???" in inbox; solve it later
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
