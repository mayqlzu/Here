package com.mayqlzu.apphere;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends Activity {
	private Fragment m_membersFragment;
	private Fragment m_broadcastFragment;
	private Fragment m_callTheRollFragment;
	private Fragment m_otherFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 
         * activity don't need a layout at all, and don't even need to call 
         * setContentView() if the tab content will fill the activity layout
         * (excluding the action bar)
         * use the root android.R.id.content as the container for each fragment
         */
        //setContentView(R.layout.activity_main);
        
        // new 4 fragments
        m_membersFragment = new MembersFragment();
        m_broadcastFragment = new BroadcastFragment();
        m_callTheRollFragment = new CallTheRollFragment();
        m_otherFragment = new OtherFragment();
        
        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab tab = actionBar.newTab()
                .setText(R.string.members).setTabListener(new TabListener(m_membersFragment));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
            .setText(R.string.broadcast).setTabListener(new TabListener(m_broadcastFragment));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
            .setText(R.string.call_the_roll).setTabListener(new TabListener(m_callTheRollFragment));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
            .setText(R.string.other).setTabListener(new TabListener(m_otherFragment));
        actionBar.addTab(tab);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
