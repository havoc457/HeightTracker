package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class Compare extends Activity {

	private SharedPreferences preferences;
	private int num_users;
	private Spinner spinner;
	private int current_height;
	private String current_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		TextView compareTV=(TextView)findViewById(R.id.landmarkCompare);
		TextView userName=(TextView)findViewById(R.id.userName);
		spinner=(Spinner)findViewById(R.id.landmarkSpinner);

		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (num_users == 0){
			current_user = "Please Add New User";
			userName.setVisibility(View.GONE);
			compareTV.setVisibility(View.GONE);
			current_height = 0;
		} else {
			current_user = preferences.getString("current_user", "");
			current_height = preferences.getInt(current_user + "current_height", 0);
			userName.setText("   " + current_user + "s!");
		}
		setTitle(current_user);

		if (current_height == 0 || current_user == ""){
			userName.setVisibility(View.GONE);
			compareTV.setVisibility(View.GONE);
		}

		addListenerOnSpinnerItemSelection();
	}

	public void addListenerOnSpinnerItemSelection() {
		spinner = (Spinner) findViewById(R.id.landmarkSpinner);
		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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

}
