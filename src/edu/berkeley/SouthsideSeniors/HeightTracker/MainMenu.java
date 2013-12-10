package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;

public class MainMenu extends Activity {

	private SharedPreferences preferences;
	private int num_users;
	private String current_user;
	private int current_height;
	private String current_height_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/comic_sans.ttf");
		FontsOverride.setDefaultFont(this, "MONOSPACE", typeface);
		setContentView(R.layout.activity_main_menu);
		TextView recent=(TextView)findViewById(R.id.recentMeasurement);
		recent.setTypeface(typeface);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));

		if (num_users == 0){
			current_user = "Please Add New User";
			current_height_text = "No Recent Height";
			addUser(recent);
		} else {
			current_user = preferences.getString("current_user","Please Add New User");
			current_height = preferences.getInt(current_user + "current_height", 0);
		}

		setTitle(current_user);
		if (current_height == 0){
			current_height_text = "No Recent Height";
		} else {
			current_height_text = "Most Recent: " + Integer.toString(getFeet(current_height)) + "'" 
												+ Integer.toString(getInches(current_height))+ "\"";
		}
		recent.setText(current_height_text);
		//addJunkData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView recent=(TextView)findViewById(R.id.recentMeasurement);
		if (num_users == 0){
			current_user = "Please Add New User";
			current_height_text = "No Recent Height";
			addUser(recent);
		} else {
			current_user = preferences.getString("current_user","Please Add New User");
			current_height = preferences.getInt(current_user + "current_height", 0);
		}

		setTitle(current_user);
		if (current_height == 0){
			current_height_text = "No Recent Height";
		} else {
			current_height_text = "Most Recent: " + Integer.toString(getFeet(current_height)) 
											+ "'" + Integer.toString(getInches(current_height))+ "\"";
		}
		recent.setText(current_height_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public void viewWall(View view){
		Intent i = new Intent(this, Wall.class);
		startActivity(i);
	}

	public void viewMeasure(View view){
		Intent i = new Intent(this, Measure.class);
		startActivity(i);
	}

	public void viewLog(View view){
		Intent i = new Intent(this, Log.class);
		startActivity(i);
	}

	public void viewCompare(View view){
		Intent i = new Intent(this, Compare.class);
		startActivity(i);
	}

	public void addUser(View view){
		Intent i = new Intent(this, EditUsers.class);
		i.putExtra("EditUser", 0);
		startActivity(i);
	}

	public boolean users(MenuItem item){
		Intent i = new Intent(this, ViewUsers.class);
		startActivity(i);
		return true;
	}

	public final static int getFeet(int inches){
		return inches / 12;
	}

	public final static int getInches(int inches){
		return inches % 12;
	}
	
	public void addJunkData() {
		SharedPreferences.Editor editor = preferences.edit();
		int x = 5;
		
		for (int i = 0; i < 20; i++) {
			editor.putInt(current_user + "Object" + i, (x+i));
			editor.putString(current_user + "Object" + i + "Name", current_user);
			editor.putString(current_user + "Object" + i + "Date", "12/05/2013");
		}
		
		editor.putInt(current_user + "numMeasuresObjects", 19);
		editor.commit();
	}
}
