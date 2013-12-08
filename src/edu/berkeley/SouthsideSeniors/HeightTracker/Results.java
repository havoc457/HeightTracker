package edu.berkeley.SouthsideSeniors.HeightTracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import android.widget.Toast;

public class Results extends Activity {

	private int measureResult;
	private SharedPreferences preferences;
	private int num_users;
	private String current_user;
	private Date myDate;
	private String dateString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		Intent starterIntent = getIntent();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		TextView results=(TextView)findViewById(R.id.resultsText);
		RadioButton user=(RadioButton)findViewById(R.id.radioUser);

		measureResult = starterIntent.getIntExtra("heightInInches", 0);
		String measureFeet = Integer.toString(MainMenu.getFeet(measureResult));
		String measureInches = Integer.toString(MainMenu.getInches(measureResult));
		results.setText(measureFeet + "'" + measureInches + "\"");

		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));

		current_user = preferences.getString("current_user", "");
		user.setText(current_user);

		setTitle(current_user);

		myDate = new Date();
		SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		dateString = dt.format(myDate);

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

		RadioButton userRadio = (RadioButton)findViewById(R.id.radioUser);
		RadioButton otherRadio = (RadioButton)findViewById(R.id.radioOther);
		EditText enteredText = (EditText)findViewById(R.id.editText);
		String objectName = "";
		SharedPreferences.Editor editor = preferences.edit();
		int numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
		int numMeasuresObjects = preferences.getInt(current_user + "numMeasuresObjects", 0);

		if (current_user.equals("") || num_users == 0){
			Toast.makeText(this, "Please create a user before saving height", Toast.LENGTH_SHORT).show();
		} else if (userRadio.isChecked()) {
			editor.putInt(current_user + "Measure" + numMeasuresUser, measureResult);  //this measurement result
			editor.putString(current_user + "Measure" + numMeasuresUser + "Date", dateString);  //date of this result
			editor.putString(current_user + "Measure" + numMeasuresUser + "Name", current_user);  //title for this measurement = userName
			editor.putInt(current_user + "current_height", measureResult);  //update user's most recent height for wall to call up
			editor.putInt(current_user + "numMeasuresUser", numMeasuresUser+1); //increase measurement counter
			editor.commit();
			startActivity(i);
		} else if (otherRadio.isChecked()) {
			if (enteredText.getText().toString().equals("")){
				Toast.makeText(this, "Please enter this object's name.", Toast.LENGTH_SHORT).show();
			} else {
				objectName = enteredText.getText().toString();  //make sure this can't be "" I guess
				editor.putInt(current_user + "Object" + numMeasuresObjects, measureResult);  //this measurement result
				editor.putString(current_user + "Object" + numMeasuresObjects + "Date", dateString);  //date of this result
				editor.putString(current_user + "Object" + numMeasuresObjects + "Name", objectName);  //title for this measurement = userName
				editor.putInt(current_user + "numMeasuresObjects", numMeasuresObjects+1); //increase object measurement counter
				editor.commit();
				startActivity(i);
			}
		} else if (measureResult == 0){
			Toast.makeText(this, "Please measure again'", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Please select 'User' or 'Other'", Toast.LENGTH_SHORT).show();
		}
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
}