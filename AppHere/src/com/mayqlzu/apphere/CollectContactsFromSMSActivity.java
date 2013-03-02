package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CollectContactsFromSMSActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.collect_contacts_from_sms_layout);
        
        // register callback for button "start"
        Button btn_start = (Button)this.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// read inbox
				// todo: do it in another thread to avoid UI freeze
				Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), 
						null, null, null, null);
				cursor.moveToFirst();
				if(0 == cursor.getCount())
					return;
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
				int count = msgs.size();
				String[] address 	= new String[count];
				String[] body 	= new String[count];
				String[] date_sent 	= new String[count];
				for(int i=0; i<count; i++){
					SMS msg = msgs.get(i);
					address[i] = msg.m_address;
					body[i] = msg.m_body;
					date_sent[i] = msg.m_date_sent;
				}
				
				// start next Activity: FilterCandidatesActivity
				Activity hostActivity = CollectContactsFromSMSActivity.this;
				Intent intent = new Intent(hostActivity, FilterCandidatesActivity.class);
				intent.putExtra(CONST_STRING.address, address);
				intent.putExtra(CONST_STRING.body, body);
				intent.putExtra(CONST_STRING.date_sent, date_sent);
				//System.out.println("check emun ordinal: " + RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal());
				hostActivity.startActivityForResult(intent, 
						RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal());
			}
		});
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult() called");
    	if(RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal() == requestCode
    			&& RequestCodes.COLLECT_CONTACTS_FROM_SMS_TO_FILTER_CANDIDATES.ordinal() == resultCode){
    		int[] selectedIndice = data.getIntArrayExtra(CONST_STRING.selected_indice);
    		for(int i: selectedIndice){
    			System.out.println(i);
    		}
    	}
    }
}
