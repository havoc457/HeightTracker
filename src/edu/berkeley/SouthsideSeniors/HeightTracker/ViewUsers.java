package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;

public class ViewUsers extends Activity {
	
	private SharedPreferences preferences;
	private int num_users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_users);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
	
		String current_user;
		if (num_users == 0){
			current_user = "Please Add New User";
		} else {
			current_user = preferences.getString("current_user", "");
		}
		int resID;
		TextView text;
		ImageButton edit, userButton;
		
		for(int i = 12; i > num_users; i--){
			System.out.println("I: " + i);
			resID = getResources().getIdentifier("user" + i, "id", getPackageName());
			text = (TextView) findViewById(resID);
			text.setVisibility(View.GONE);
			resID = getResources().getIdentifier("editbutton" + i, "id", getPackageName());
			edit = (ImageButton) findViewById(resID);
			edit.setVisibility(View.GONE);
			resID = getResources().getIdentifier("userbutton" + i, "id", getPackageName());
			userButton = (ImageButton) findViewById(resID);
			userButton.setVisibility(View.GONE);
		} 
		for (int i = 1; i <= num_users; i++){
			resID = getResources().getIdentifier("user" + i, "id", getPackageName());
			text = (TextView) findViewById(resID);
			text.setText(preferences.getString("user" + i, ""));
		}
		setTitle(current_user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_users, menu);
		return true;
	}
	public void editUser(View view){
		Intent i = new Intent(this, EditUsers.class);
		i.putExtra("EditUser", Integer.parseInt(view.getContentDescription().toString()));
		startActivity(i);
	}
	
	public void newUser(View view){
		Intent i = new Intent(this, EditUsers.class);
		i.putExtra("EditUser", "0");
		startActivity(i);
	}
	
	public void selectUser(View view){
		int userNum = Integer.parseInt(view.getContentDescription().toString());
		String current_user = preferences.getString("user" + userNum, "");
		SharedPreferences.Editor editor = preferences.edit();
	    editor.putString("current_user", current_user);
	    editor.commit();
	    //setTitle(current_user);
	    Intent i = new Intent(this, MainMenu.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(i);
	}
	
}
