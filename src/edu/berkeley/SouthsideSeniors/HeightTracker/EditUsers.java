package edu.berkeley.SouthsideSeniors.HeightTracker;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditUsers extends Activity {
	
	private SharedPreferences preferences;
	private int num_users;
	private String numEdit;
	private String current_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_users);
		
		Bundle extras = getIntent().getExtras();
		numEdit = Integer.toString(extras.getInt("EditUser"));
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
		if (numEdit.equals("0")){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("user" + numEdit, "");
			EditText edit = ((EditText) findViewById(R.id.editName));
			edit.setText(current_user, TextView.BufferType.EDITABLE);
		}
		setTitle(current_user);
		
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
		
	    EditText edit = ((EditText) findViewById(R.id.editName));
	    current_user = edit.getText().toString();
	    
	    if (current_user.equals("")){
	    	Toast.makeText(getApplicationContext(), "Please enter your name.", Toast.LENGTH_SHORT).show();
	    } else {
		
			//Go back to the home view.
		    Intent intent = new Intent(EditUsers.this, MainMenu.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
		    startActivity(intent);
		    
		    SharedPreferences.Editor editor = preferences.edit();
		    if (numEdit.equals("0")){
		    	num_users++;
		    	editor.putInt(current_user + "numMeasures", 0);
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
		    editor.putString("user"+num_users, current_user);
		    editor.putString("num_users", Integer.toString(num_users));
		    editor.putString("current_user", current_user);
		    editor.commit();
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
	
	public int getFeet(int inches){
		return inches / 12;
	}
	
	public int getInches(int inches){
		return inches % 12;
	}
	
}
