package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Log extends Activity {
	
	private SharedPreferences preferences;
	private int num_users;
	private int numMeasuresUser;
	private int numMeasuresObjects;
	private String current_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
	
		if (num_users == 0){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("current_user", "");  //what if same names of users?
		}
		setTitle(current_user);
		
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec specs = tabHost.newTabSpec("User");
		specs.setContent(R.id.userTable);
		specs.setIndicator("User");
		tabHost.addTab(specs);
		specs = tabHost.newTabSpec("Other");
		specs.setContent(R.id.otherTable);
		specs.setIndicator("Other");
		tabHost.addTab(specs);
		
		TableLayout userTable = (TableLayout) findViewById(R.id.userTable);
		TableLayout otherTable = (TableLayout) findViewById(R.id.otherTable);
	
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
		
		//load user table
		numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
		for (int i = 0; i < numMeasuresUser; i++) {
			TableRow newRow = new TableRow(this);
			CheckBox checkBox = new CheckBox(this);
			TextView nameView = new TextView(this);
			nameView.setText(preferences.getString(current_user + "Measure" + i + "Name", "No Username Detected."));
			TextView dateView = new TextView(this);
			dateView.setText(preferences.getString(current_user + "Measure" + i + "Date", "No Recorded Date"));
			TextView heightView = new TextView(this);
			int measureResults = preferences.getInt(current_user + "Measure" + i, 9001);
			int measureFeet = MainMenu.getFeet(measureResults);
			int measureInches = MainMenu.getInches(measureResults);
			heightView.setText(measureFeet + "'" + measureInches + "\"");
			
			newRow.addView(checkBox);
			newRow.addView(nameView);
			newRow.addView(dateView);
			newRow.addView(heightView);
			if ( (i % 2) == 0) {
				newRow.setBackgroundColor(Color.parseColor("#bdbdbd"));
			}
			userTable.addView(newRow);
		}
		
		//load Object table
		numMeasuresObjects = preferences.getInt(current_user + "numMeasuresObjects", 0);
		System.out.println("OBJECTS:" + numMeasuresObjects);
		for (int i = 0; i < numMeasuresObjects; i++) {
			TableRow newRow = new TableRow(this);
			
			CheckBox checkBox = new CheckBox(this);
			TextView nameView = new TextView(this);
			nameView.setText(preferences.getString(current_user + "Object" + i + "Name", "No Object Name Detected."));
			TextView dateView = new TextView(this);
			dateView.setText(preferences.getString(current_user + "Object" + i + "Date", "No Recorded Date"));
			TextView heightView = new TextView(this);
			int measureResults = preferences.getInt(current_user + "Object" + i, 9001);
			int measureFeet = MainMenu.getFeet(measureResults);
			int measureInches = MainMenu.getInches(measureResults);
			heightView.setText(measureFeet + "'" + measureInches + "\"");
			
			newRow.addView(checkBox);
			newRow.addView(nameView);
			newRow.addView(dateView);
			newRow.addView(heightView);	
			if ( (i % 2) == 0) {
				newRow.setBackgroundColor(Color.parseColor("#bdbdbd"));
			}
			otherTable.addView(newRow);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}
	
	public boolean users(MenuItem item) {
		Intent i = new Intent(this, ViewUsers.class);
		startActivity(i);
	    return true;
	}

}
