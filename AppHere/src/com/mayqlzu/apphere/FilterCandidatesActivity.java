package com.mayqlzu.apphere;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FilterCandidatesActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.filter_candidates_layout);
        
        // fill listView with items
        ListView listView = (ListView)this.findViewById(R.id.candidates_list);
        String[] testStrArray = new String[] {"item1", "item2", "item3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, 
                android.R.layout.simple_list_item_multiple_choice, 
                testStrArray);  
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); 
    }

}
