package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;

public class Results extends Activity {
	
	private double measureResult;
	private SharedPreferences preferences;
	private int num_users;
	private String current_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		Intent starterIntent = getIntent();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		TextView results=(TextView)findViewById(R.id.resultsText);
		RadioButton user=(RadioButton)findViewById(R.id.radioUser);
		//RadioButton other=(RadioButton)findViewById(R.id.radioOther);
		//EditText edit=(EditText)findViewById(R.id.editText);
		
		measureResult = starterIntent.getDoubleExtra("heightInMeters", (double) 0.0);
		results.setText(String.valueOf(measureResult));
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
	
		if (num_users == 0){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("current_user", "");
			user.setText(current_user);
		}
		setTitle(current_user);
		
		//Hide keyboard when hitting enter on the EditText field
		final EditText edit = ((EditText) findViewById(R.id.editText));
		edit.setOnEditorActionListener(new OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        	if (actionId == EditorInfo.IME_ACTION_DONE) {
	                if (edit.getText().toString().length() > 0) {
	                    // Perform action on key press
	                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	                }
	            }
				return true;
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}
	
	public void viewWall(View view){
		Intent i = new Intent(this, Wall.class);
		int numMeasures = preferences.getInt(current_user + "numMeasures", 0);
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(current_user + "Measure" + numMeasures, (int) measureResult);
		editor.putInt(current_user + "numMeasures", numMeasures+1);
		editor.putInt(current_user + "current_height", (int) measureResult);
		editor.commit();
		startActivity(i);
	}
	
	public void viewMeasure(View view){
		Intent i = new Intent(this, Measure.class);
		startActivity(i);
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