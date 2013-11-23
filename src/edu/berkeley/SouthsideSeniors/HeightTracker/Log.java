package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;

public class Log extends Activity {
	
	private SharedPreferences preferences;
	private int num_users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
	
		String current_user;
		if (num_users == 0){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("current_user", "");
		}
		setTitle(current_user);
		
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		//TableLayout table = (TableLayout) findViewById(R.id.User);
		tabHost.setup();
		TabSpec specs = tabHost.newTabSpec("User");
		specs.setContent(R.id.User);
		specs.setIndicator("User");
		tabHost.addTab(specs);
		specs = tabHost.newTabSpec("Other");
		specs.setContent(R.id.Other);
		specs.setIndicator("Other");
		tabHost.addTab(specs);
		
		RelativeLayout relative = (RelativeLayout) tabHost.getChildAt(0);
		TabWidget tw = (TabWidget) relative.getChildAt(0);

		// for changing the text size and font of first tab
		LinearLayout linear = (LinearLayout) tw.getChildAt(0);
		TextView lf = (TextView) linear.getChildAt(1);
		lf.setTextSize(27);
		
		// for changing the text size and font of second tab
		linear = (LinearLayout) tw.getChildAt(1);
		lf = (TextView) linear.getChildAt(1);
		lf.setTextSize(27);
		
		//For programmatically adding rows
		/*
	    for (int i = 0; i <2; i++) {

	        TableRow row= new TableRow(this);
	        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
	        row.setLayoutParams(lp);
	        CheckBox checkBox = new CheckBox(this);
	        TextView tv = new TextView(this);
	        ImageButton addBtn = new ImageButton(this);
	        addBtn.setImageResource(R.drawable.pencil2);
	        ImageButton minusBtn = new ImageButton(this);
	        minusBtn.setImageResource(R.drawable.pencil2);
	        checkBox.setText("hello");
	        tv.setText("10");
	        row.addView(checkBox);
	        row.addView(minusBtn);
	        row.addView(tv);
	        row.addView(addBtn);
	        table.addView(row,i);
	    }*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}
	public boolean users(MenuItem item){
		Intent i = new Intent(this, ViewUsers.class);
		startActivity(i);
	    return true;
	}
	
	public boolean settings(MenuItem item){
		//Intent i = new Intent(this, SettingsActivity.class);
		//startActivity(i);
	    return true;
	}
}
