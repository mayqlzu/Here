package com.mayqlzu.apphere;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class OtherFragment extends Fragment {
	private class Listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
    		new AlertDialog.Builder(v.getContext()) 
            				.setMessage("thanks for your reply :)")  
            				.setPositiveButton("OK", null)  
            				.show(); 
		}
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.other_fragment_layout, container, false);
    }
    
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
    	Button like = (Button)this.getActivity().findViewById(R.id.btn_like);
    	Button dislike = (Button)this.getActivity().findViewById(R.id.btn_dislike);
    	Listener listener = new Listener();
    	like.setOnClickListener(listener);
    	dislike.setOnClickListener(listener);

    }
}