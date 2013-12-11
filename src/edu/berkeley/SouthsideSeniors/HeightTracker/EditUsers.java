package edu.berkeley.SouthsideSeniors.HeightTracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditUsers extends Activity {

	private SharedPreferences preferences;
	private int num_users;
	private int numEdit;
	private String current_user;
	private SharedPreferences.Editor editor;
	private String past_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_users);

		Bundle extras = getIntent().getExtras();
		numEdit = extras.getInt("EditUser");
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
		if (numEdit == 0){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("user" + numEdit, "");
			EditText edit = ((EditText) findViewById(R.id.editName));
			edit.setText(current_user, TextView.BufferType.EDITABLE);
			ImageView delete = (ImageView) findViewById(R.id.deleteEdit);
			delete.setImageResource(R.drawable.delete);
		}
		setTitle(current_user);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//Set maximum, minimum, and default values for the number pickers
		NumberPicker np = (NumberPicker) findViewById(R.id.dadFeetPicker);
		np.setMaxValue(7);
		np.setMinValue(0);
		np.setValue(getFeet(preferences.getInt(current_user + "dadHeight",0)));	
		np = (NumberPicker) findViewById(R.id.dadInchesPicker);
		np.setMaxValue(11);
		np.setMinValue(0);
		np.setValue(getInches(preferences.getInt(current_user + "dadHeight",0)));
		np = (NumberPicker) findViewById(R.id.momFeetPicker);
		np.setMaxValue(7);
		np.setMinValue(0);
		np.setValue(getFeet(preferences.getInt(current_user + "momHeight",0)));
		np = (NumberPicker) findViewById(R.id.momInchesPicker);
		np.setMaxValue(11);
		np.setMinValue(0);
		np.setValue(getInches(preferences.getInt(current_user + "momHeight",0)));

		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupEdit);
		boolean gender = preferences.getBoolean(current_user+"Gender", true);
		if(gender){
			RadioButton radioBtn = (RadioButton) rg.getChildAt(0);
			radioBtn.setChecked(true);
		} else {
			RadioButton radioBtn = (RadioButton) rg.getChildAt(1);
			radioBtn.setChecked(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_users, menu);
		return true;
	}

	public void save(View view){


		String past_user = preferences.getString("user" + numEdit, "");
		EditText edit = ((EditText) findViewById(R.id.editName));
		current_user = edit.getText().toString();

		if (current_user.equals("")){
			Toast.makeText(getApplicationContext(), "Please enter your name.", Toast.LENGTH_SHORT).show();
		} else if (current_user.length() > 13){
			Toast.makeText(getApplicationContext(), "Please enter a shorter name.", Toast.LENGTH_SHORT).show();
		} else {

			SharedPreferences.Editor editor = preferences.edit();
			if (numEdit == 0){
				num_users++;
				editor.putInt(current_user + "numMeasures", 0);
				editor.putString("user"+num_users, current_user);
			} else if (!past_user.equals(current_user)){
				editor.putString("user"+numEdit, current_user);
			}

			NumberPicker np = (NumberPicker) findViewById(R.id.dadFeetPicker);
			int dadFeet = np.getValue();
			np = (NumberPicker) findViewById(R.id.dadInchesPicker);
			int dadInches = np.getValue();
			int dadHeight = 12*dadFeet + dadInches;
			np = (NumberPicker) findViewById(R.id.momFeetPicker);
			int momFeet = np.getValue();
			np = (NumberPicker) findViewById(R.id.momInchesPicker);
			int momInches = np.getValue();
			int momHeight = 12*momFeet + momInches;

			RadioButton genderBtn = (RadioButton) findViewById(R.id.radioBoy);
			boolean gender = true;
			if (!genderBtn.isChecked()){
				gender = false;
			}

			int eventual = estimateHeight(dadHeight, momHeight, gender);
			editor.putInt(current_user+"dadHeight", dadHeight);
			editor.putInt(current_user+"momHeight", momHeight);
			editor.putBoolean(current_user+"Gender", gender);
			editor.putInt(current_user+"eventual", eventual);
			editor.putString("num_users", Integer.toString(num_users));
			editor.putString("current_user", current_user);

			int numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
			int numMeasuresObjects = preferences.getInt(current_user + "numMeasuresObjects", 0);
			if (!past_user.equals(current_user)){
				editor.remove(past_user+"dadHeight");
				editor.remove(past_user+"momHeight");
				editor.remove(past_user+"Gender");
				editor.remove(past_user+"eventual");

				if (numMeasuresUser > 0){
					for (int i = 0; i < numMeasuresUser; i++){
						editor.putInt(current_user + "Measure" + i, preferences.getInt(past_user + "Measure" + i, 0));
						editor.putString(current_user + "Measure" + i + "Date", preferences.getString(past_user + "Measure" + i + "Date", ""));
						editor.putString(current_user + "Measure" + i + "Name", preferences.getString(past_user + "Measure" + i + "Name", ""));
						editor.remove(past_user + "Measure" + i);
						editor.remove(past_user + "Measure" + i + "Date");
						editor.remove(past_user + "Measure" + i + "Name");
					}
					editor.remove(current_user + "numMeasuresUser");
				}
				if (numMeasuresObjects > 0){		
					for (int i = 0; i < numMeasuresUser; i++){
						editor.putInt(current_user + "Object" + i, preferences.getInt(past_user + "Object" + i, 0));
						editor.putString(current_user + "Object" + i + "Date", preferences.getString(past_user + "Object" + i + "Date", ""));
						editor.putString(current_user + "Object" + i + "Name", preferences.getString(past_user + "Object" + i + "Name", ""));
						editor.remove(past_user + "Object" + i);
						editor.remove(past_user + "Object" + i + "Date");
						editor.remove(past_user + "Object" + i + "Name");
					}
					editor.remove(current_user + "numMeasuresObjects");
				}
			}
			editor.commit();
		}
		//Go back to the home view.
		Intent intent = new Intent(EditUsers.this, MainMenu.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
		startActivity(intent);

	}

	//Deletes the user and all their data if they confirm the alert dialog
	public void delete(View view){

		editor = preferences.edit();
		past_user = preferences.getString("user" + numEdit, "");
		EditText edit = ((EditText) findViewById(R.id.editName));
		current_user = edit.getText().toString();
		AlertDialog caliDialog;

		if (!current_user.equals(past_user)){
			Toast.makeText(getApplicationContext(), "Please only delete the edited user", Toast.LENGTH_SHORT).show();
		} else if (numEdit != 0){
			caliDialog = new AlertDialog.Builder(this).create();
			caliDialog.setMessage("Are you sure you want to delete " + current_user + "?");
			caliDialog.setTitle("Delete?");
			caliDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					int numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
					int numMeasuresObjects = preferences.getInt(current_user + "numMeasuresObjects", 0);

					editor.remove(past_user+"dadHeight");
					editor.remove(past_user+"momHeight");
					editor.remove(past_user+"Gender");
					editor.remove(past_user+"eventual");

					if (numMeasuresUser > 0){
						for (int i = 0; i < numMeasuresUser; i++){
							editor.remove(past_user + "Measure" + i);
							editor.remove(past_user + "Measure" + i + "Date");
							editor.remove(past_user + "Measure" + i + "Name");
						}
						editor.remove(current_user + "numMeasuresUser");
					}
					if (numMeasuresObjects > 0){		
						for (int i = 0; i < numMeasuresUser; i++){
							editor.remove(past_user + "Object" + i);
							editor.remove(past_user + "Object" + i + "Date");
							editor.remove(past_user + "Object" + i + "Name");
						}
						editor.remove(current_user + "numMeasuresObjects");
					}
					editor.remove(current_user + "current_height");
					editor.remove("user" + numEdit);
					num_users = Integer.parseInt(preferences.getString("num_users","0"));
					editor.putString("num_users", Integer.toString(num_users-1));
					editor.putString("current_user", preferences.getString("user" + (numEdit-1),""));
					for(int i = num_users-1; i >= numEdit; i--){
						editor.putString("user" + i, preferences.getString("user" + (i+1), ""));
					}
					editor.commit();
					Intent intent = new Intent(EditUsers.this, MainMenu.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
					startActivity(intent);
				}
			});
			caliDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			caliDialog.show();
		}
	}

	//For calculating eventual height. Takes integer dad's height and mom's height, boolean boy is true for boys, false for girls.
	//Returns height in inches.
	public int estimateHeight(int dad, int mom, boolean boy){
		if(boy){
			return (dad+mom+5)/2;
		} else {
			return (dad+mom-5)/2;
		}
	}

	//Returns number of feet without inches from the number of inches inputted
	public int getFeet(int inches){
		return inches / 12;
	}

	//Returns number of inches without feet from the number of inches inputted
	public int getInches(int inches){
		return inches % 12;
	}

}
