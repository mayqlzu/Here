package com.mayqlzu.apphere;

import java.util.ArrayList;

import android.util.Log;

public class Contact {
	public String m_name;
	public String m_number;
	
	public Contact(String name, String number){
		m_name = name;
		m_number = number;
	}
	
	public static String[] filterNames(ArrayList<Contact> contacts){
		String[] names = new String[contacts.size()];
		for(int i=0; i<contacts.size(); i++){
			names[i] = contacts.get(i).m_name;
		}
		return names;
	}
	
	public static String[] filterNumbers(ArrayList<Contact> contacts){
		String[] numbers = new String[contacts.size()];
		for(int i=0; i<contacts.size(); i++){
			numbers[i] = contacts.get(i).m_number;
		}
		return numbers;
	}
	
	// not used for now
	private static String[] combine(String[] names, String[] numbers, String separator){
		if(names.length != numbers.length){
			Log.d("Contact.combine()", "names.length != numbers.length !"); 
			return null;
		}
		
		String[] names_numbers = new String[names.length];
		for(int i=0; i<names.length; i++){
			names_numbers[i] = names[i] + separator + numbers[i];
		}
		
		return names_numbers;
	}
	
	public static String[] ArrayList2Array(ArrayList<String> arrList){
		String[] arr = new String[arrList.size()];
		for(int i=0; i<arrList.size(); i++){
			arr[i] = arrList.get(i);
		}
		return arr;
	}
}
