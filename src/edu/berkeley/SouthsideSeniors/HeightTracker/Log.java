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
		
		//have to add in something where view changes when click on different tabs
		
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
		
		
		addJunkData();
		//load user table
		numMeasuresUser = preferences.getInt("current_user" + "numMeasures", 0);
		//let's pretend user has made 3 measurements so far
		//i = 5 means get the 3rd measurement the person took
		for (int i = 1; i <= numMeasuresUser; i++) {
			TableRow newRow = new TableRow(this);
			
			TextView nameView = new TextView(this);
			nameView.setText(preferences.getString(current_user + "Measure" + i + "Name", "No Username Detected."));
			TextView dateView = new TextView(this);
			dateView.setText(preferences.getString(current_user + "Measure" + i + "Date", "No Recorded Date"));
			TextView heightView = new TextView(this);
			heightView.setText(preferences.getInt(current_user + "Measure" + i, 9001));  //does setText auto convert this int to string
			//convert the height data to ft' in'' in Results and save it as such
			
			newRow.addView(nameView);
			newRow.addView(dateView);
			newRow.addView(heightView);
			userTable.addView(newRow);
		}
		
		//load Object table
		numMeasuresObjects = preferences.getInt("current_user" + "numMeasuresObjects", 0);
		for (int i = 1; i <= numMeasuresObjects; i++) {
			TableRow newRow = new TableRow(this);
			
			TextView nameView = new TextView(this);
			nameView.setText(preferences.getString(current_user + "Object" + i + "Name", "No Object Name Detected."));
			TextView dateView = new TextView(this);
			dateView.setText(preferences.getString(current_user + "Object" + i + "Date", "No Recorded Date"));
			TextView heightView = new TextView(this);
			heightView.setText(preferences.getInt(current_user + "Object" + i, 9001));
			//convert the height data to ft' in'' in Results and save it as such
			
			newRow.addView(nameView);
			newRow.addView(dateView);
			newRow.addView(heightView);
			otherTable.addView(newRow);
		}
		
		/*
		//For programmatically adding rows
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
	    }
	    */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}
	
	public void addJunkData(){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(current_user+"Measure1", 1);
		editor.putString(current_user+"Measure1Date", "Dec 04 2013");
		editor.putString(current_user+"Measure1Name", "Jeremy");
		editor.putInt(current_user+"Measure2", 1);
		editor.putString(current_user+"Measure2Date", "Dec 05 2013");
		editor.putString(current_user+"Measure2Name", "Aaron");
		
		editor.putInt(current_user+"Object1", 100);
		editor.putString(current_user+"Object1Date", "June 22 2013");
		editor.putString(current_user+"Object1Name", "tv");
		editor.putInt(current_user+"Object2", 57);
		editor.putString(current_user+"Object2Date", "April 9 2013");
		editor.putString(current_user+"Object2Name", "bat");
		
		editor.commit();
	}
	
	public boolean users(MenuItem item) {
		Intent i = new Intent(this, ViewUsers.class);
		startActivity(i);
	    return true;
	}

}
