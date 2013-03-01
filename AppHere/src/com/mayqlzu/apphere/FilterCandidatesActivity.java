package com.mayqlzu.apphere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FilterCandidatesActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.filter_candidates_layout);
        
        // receive param from caller
        Intent intent = this.getIntent();
        String[] address = intent.getStringArrayExtra(CONST_STRING.address);
        String[] body = intent.getStringArrayExtra(CONST_STRING.body);
        String[] date_sent = intent.getStringArrayExtra(CONST_STRING.date_sent);
        
        // convert 3 array to one
        String[] items = new String[address.length];
        for(int i=0; i<address.length; i++){
        	items[i] = body[i] + " " + address[i];
        }
        
        // fill listView with items
        ListView listView = (ListView)this.findViewById(R.id.candidates_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, 
                android.R.layout.simple_list_item_multiple_choice, 
                items);  
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); 
    }

}
