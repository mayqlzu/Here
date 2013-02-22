package com.mayqlzu.apphere;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabListener implements ActionBar.TabListener {
	private Fragment m_fragment; // corresponding fragment for current tab
	
	public TabListener(Fragment f){
		m_fragment = f;
	}

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	// add fragment to activity's default root view, this view is created by system, not me
    	ft.add(android.R.id.content, m_fragment);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	ft.remove(m_fragment);
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}