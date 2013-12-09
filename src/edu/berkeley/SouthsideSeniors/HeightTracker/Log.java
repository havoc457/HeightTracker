package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
			nameView.setText(preferences.getString(current_user + "Measure" + i + "Name", "No Username Detected"));
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
		for (int i = 0; i < numMeasuresObjects; i++) {
			TableRow newRow = new TableRow(this);

			CheckBox checkBox = new CheckBox(this);
			TextView nameView = new TextView(this);
			nameView.setText(preferences.getString(current_user + "Object" + i + "Name", "No Object Name"));
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
	protected void onResume() {
		super.onResume();
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		int currentTab = preferences.getInt("currentTab", tabHost.getCurrentTab());
		tabHost.setCurrentTab(currentTab);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("currentTab", 0);
		editor.commit();
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

	public void selectAll(View selectButton) {
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		TableLayout userTable = (TableLayout) findViewById(R.id.userTable);
		TableLayout otherTable = (TableLayout) findViewById(R.id.otherTable);
		int numRowsUser = userTable.getChildCount();
		int numRowsOther = otherTable.getChildCount();

		if (tabHost.getCurrentTabTag().equals("User")) {
			for (int i = 0; i < numRowsUser; i++) {
				CheckBox thisCB = (CheckBox) ((TableRow) userTable.getChildAt(i)).getChildAt(0);
				thisCB.setChecked(true);
			}
		} else if (tabHost.getCurrentTabTag().equals("Other")) {
			for (int i = 0; i < numRowsOther; i++) {
				CheckBox thisCB = (CheckBox) ((TableRow) otherTable.getChildAt(i)).getChildAt(0);
				thisCB.setChecked(true);
			}
		} else {
			System.out.println("DEBUG: Something went wrong with tabs selection.");
		}
	}

	public void unselectAll(View unselectButton) {
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		TableLayout userTable = (TableLayout) findViewById(R.id.userTable);
		TableLayout otherTable = (TableLayout) findViewById(R.id.otherTable);
		int numRowsUser = userTable.getChildCount();
		int numRowsOther = otherTable.getChildCount();

		if (tabHost.getCurrentTabTag().equals("User")) {
			for (int i = 0; i < numRowsUser; i++) {
				CheckBox thisCB = (CheckBox) ((TableRow) userTable.getChildAt(i)).getChildAt(0);
				thisCB.setChecked(false);
			}
		} else if (tabHost.getCurrentTabTag().equals("Other")) {
			for (int i = 0; i < numRowsOther; i++) {
				CheckBox thisCB = (CheckBox) ((TableRow) otherTable.getChildAt(i)).getChildAt(0);
				thisCB.setChecked(false);
			}
		} else {
			System.out.println("DEBUG: Something went wrong with tabs UNselection.");
		}
	}

	public void delete(View view) {
		SharedPreferences.Editor editor = preferences.edit();
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		TableLayout userTable = (TableLayout) findViewById(R.id.userTable);
		TableLayout otherTable = (TableLayout) findViewById(R.id.otherTable);
		int numRowsUser = userTable.getChildCount();
		int numRowsOther = otherTable.getChildCount();

		int numDeleted;
		if (tabHost.getCurrentTabTag().equals("User")) {
			numDeleted = 0;
			System.out.println("DEBUG: delete, user tab: " + tabHost.getCurrentTabTag());
			for (int i = numRowsUser-1; i >= 0; i--) {
				CheckBox thisCB = (CheckBox) ((TableRow) userTable.getChildAt(i)).getChildAt(0);
				if (thisCB.isChecked()) {
					numDeleted++;
					for (int j = i; j < numRowsUser; j++){
						int measureResult = preferences.getInt(current_user + "Measure" + (j+1), 0);  //this measurement result
						String dateString = preferences.getString(current_user + "Measure" + (j+1) + "Date", "No Recorded Date");

						editor.putInt(current_user + "Measure" + j, measureResult);  //this measurement result
						editor.putString(current_user + "Measure" + j + "Date", dateString);  //date of this result

						editor.commit();
					}
					editor.remove(current_user + "Measure" + (numMeasuresUser-numDeleted+1));
					editor.remove(current_user + "Measure" + (numMeasuresUser-numDeleted+1) + "Date");
					editor.remove(current_user + "Measure" + (numMeasuresUser-numDeleted+1) + "Name");
				}
			}
			if (numDeleted > 0){
				numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
				editor.putInt(current_user + "numMeasuresUser", numMeasuresUser-numDeleted);
				editor.putInt(current_user + "current_height", preferences.getInt(current_user + "Measure" + (numMeasuresUser-numDeleted-1), 0));
				editor.putInt("currentTab", tabHost.getCurrentTab());
				editor.commit();
				finish();
				startActivity(getIntent());
			}
		} else if (tabHost.getCurrentTabTag().equals("Other")) {
			numDeleted = 0;
			for (int i = numRowsOther-1; i >= 0; i--) {
				CheckBox thisCB = (CheckBox) ((TableRow) otherTable.getChildAt(i)).getChildAt(0);
				if (thisCB.isChecked()) {
					numDeleted++;
					for (int j = i; j < numRowsOther; j++){
						int measureResult = preferences.getInt(current_user + "Object" + (j+1), 0);  //this measurement result
						String dateString = preferences.getString(current_user + "Object" + (j+1) + "Date", "No Recorded Date");
						String objectName = preferences.getString(current_user + "Object" + (j+1) + "Name", "No Name");  //this measurement result

						editor.putInt(current_user + "Object" + j, measureResult);  //this measurement result
						editor.putString(current_user + "Object" + j + "Date", dateString);  //date of this result
						editor.putString(current_user + "Object" + j + "Name", objectName);

						editor.commit();
					}
				}
				editor.remove(current_user + "Object" + (numMeasuresObjects-numDeleted+1));
				editor.remove(current_user + "Object" + (numMeasuresObjects-numDeleted+1) + "Date");
				editor.remove(current_user + "Object" + (numMeasuresObjects-numDeleted+1) + "Name");
			}
			if (numDeleted > 0){
				numMeasuresObjects = preferences.getInt(current_user + "numMeasuresObjects", 0);
				editor.putInt(current_user + "numMeasuresObjects", numMeasuresObjects-numDeleted);
				editor.putInt("currentTab", tabHost.getCurrentTab());
				editor.commit();
				finish();
				startActivity(getIntent());
			}
		} else {
			System.out.println("DEBUG: Something went wrong with delete");
		}
	}

}
