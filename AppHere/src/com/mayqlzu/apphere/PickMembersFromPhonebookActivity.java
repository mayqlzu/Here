package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PickMembersFromPhonebookActivity extends Activity{
	public static String separator = " ";
	
	private class Listener implements View.OnClickListener{
		private ListView m_list;
		private Activity m_activity;
		private String[] m_numbers;
		private String[] m_names;
		
		public Listener(ListView list, Activity act, String[] names, String[] nums){
			m_list = list;
			m_activity = act;
			m_names = names;
			m_numbers = nums;
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
			ArrayList<String> selNames = new ArrayList<String>();
			ArrayList<String> selNumbers = new ArrayList<String>();
			int total = m_names.length;
			for(int i=0; i<total; i++){
				// keep consistency with format of m_itemsShown
				String combined = m_names[i] + PickMembersFromPhonebookActivity.separator + m_numbers[i];
				if(checkedItems.contains(combined)){
					selNames.add(m_names[i]);
					selNumbers.add(m_numbers[i]);
				}
			}
		
			String[] selNamesArr = Contact.ArrayList2Array(selNames);
			String[] selNumbersArr = Contact.ArrayList2Array(selNumbers);
			
			Intent intent = new Intent();
			intent.putExtra(CONST.names, selNamesArr);
			intent.putExtra(CONST.numbers, selNumbersArr);
			m_activity.setResult(
					// set result code with a same value of request code of caller
					RequestCodes.MEMBERS_FRAGMENT_TO_PICK_MEMBERS_FROM_PHONEBOOK.ordinal(),
					intent);
			
			// create a new activity if the caller sent intent to me again
			m_activity.finish();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pick_members_from_phonebook_layout);
        
        
        // read phonebook
        ArrayList<Contact> contacts = readPhonebook();
        
        // prepare items for show
        // attention: keep consistency with the comparison algorithm between array and list
        String[] itemsToShow = new String[contacts.size()];
        String[] names = new String[contacts.size()];
        String[] numbers = new String[contacts.size()];
        for(int i=0; i<contacts.size(); i++){
        	Contact c = contacts.get(i);
        	names[i] = c.m_name;
        	numbers[i] = c.m_number;
        	itemsToShow[i] = names[i] + this.separator + numbers[i];
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
        Listener listener = new Listener(listView, this, names, numbers);
        Button btn_chooseAll = (Button)this.findViewById(R.id.btn_choose_all);
        Button btn_markedOnly = (Button)this.findViewById(R.id.btn_choose_marked_only);
        Button btn_cancel = (Button)this.findViewById(R.id.btn_cancel);
        btn_chooseAll.setOnClickListener(listener);
        btn_markedOnly.setOnClickListener(listener);
        btn_cancel.setOnClickListener(listener);
        
        // hint if no contact
        if(0 == contacts.size()){
    		new AlertDialog.Builder(this)    
            .setMessage(this.getString(R.string.no_contacts_found))  
            .setPositiveButton(this.getString(R.string.ok), null)  
            .show();
        }
    }

    public ArrayList<Contact> readPhonebook(){
    	ArrayList<Contact> result = new ArrayList<Contact>();
    	String TAG = "reading phonebook";
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(false == cursor.moveToFirst())
        	return result;
        do {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            
            //Log.d(TAG, "Name is : "+name);
            int hasPhoneNumber = Integer.parseInt(
            		cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if(hasPhoneNumber>0){
                Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                         ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + id,null,null);
                String number = "";
                while(c.getCount() > 0 && c.moveToNext()){
                	// remember the last if multiple
                    number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Log.d(TAG    , "Number is : "+number);
                }
                if(1 == c.getCount()){
                	result.add(new Contact(name, cookNumber(number)));//system save number as "1 368-174-9102"
                }else if(c.getCount()>1){
                	Log.d(TAG, name + " has more than one phone numbers, ignored for simplicity !");
                }
                c.close();
            }
        } while(cursor.moveToNext());
        cursor.close();
        return result;
    }
    
    // "1 368-174-9102" => "13681749102"
    private String cookNumber(String orig){
    	String temp = orig.replaceAll(" ", "");
    	String cooked = temp.replaceAll("-", "");
    	return cooked;
    }
    
    
}