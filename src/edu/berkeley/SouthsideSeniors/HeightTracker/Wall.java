package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Wall extends Activity {
	
	private SharedPreferences preferences;
	private int num_users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wall);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
		TextView userText = (TextView) findViewById(R.id.userHeightText);
	
		String current_user;
		int current_height;
		if (num_users == 0){
			current_user = "Please Add New User";
			current_height = 0;
		} else {
			current_user = preferences.getString("current_user", "");
			current_height = preferences.getInt(current_user + "current_height", 66);
			userText.setText(current_user);
		}
		setTitle(current_user);
		
		ImageView current = (ImageView) findViewById(R.id.current);
		MarginLayoutParams mlp = (MarginLayoutParams) current.getLayoutParams();
		mlp.setMargins(0, 0, 0, 175*(current_height-1) - 31);//all in pixels
		current.setLayoutParams(mlp);
		
		ImageView dad = (ImageView) findViewById(R.id.dadHeightArrow);
		mlp = (MarginLayoutParams) dad.getLayoutParams();
		int dadHeight = preferences.getInt(current_user + "dadHeight", 0);
		mlp.setMargins(0, 0, 0, 175*dadHeight - 31);//all in pixels
		dad.setLayoutParams(mlp);
		
		ImageView mom = (ImageView) findViewById(R.id.momHeightArrow);
		mlp = (MarginLayoutParams) mom.getLayoutParams();
		int momHeight = preferences.getInt(current_user + "momHeight", 0);
		mlp.setMargins(0, 0, 0, 175*momHeight - 31);//all in pixels
		mom.setLayoutParams(mlp);
		
		ImageView eventualView = (ImageView) findViewById(R.id.futureArrow);
		mlp = (MarginLayoutParams) eventualView.getLayoutParams();
		int eventual = preferences.getInt(current_user + "eventual", 0);
		mlp.setMargins(0, 0, 0, 175*eventual - 31);//all in pixels
		eventualView.setLayoutParams(mlp);
		
		TextView dadText = (TextView) findViewById(R.id.dadHeightText);
		TextView momText = (TextView) findViewById(R.id.momHeightText);
		if ((dadHeight == 0 && momHeight == 0) || num_users == 0){
			dad.setVisibility(View.GONE);
			mom.setVisibility(View.GONE);
			dadText.setVisibility(View.GONE);
			momText.setVisibility(View.GONE);
			TextView eventualText = (TextView) findViewById(R.id.futureText);
			eventualView.setVisibility(View.GONE);
			eventualText.setVisibility(View.GONE);
		} else if (momHeight == 0){
			mom.setVisibility(View.GONE);
			momText.setVisibility(View.GONE);
		} else if (dadHeight == 0){
			dad.setVisibility(View.GONE);
			dadText.setVisibility(View.GONE);
		}
		if (current_height == 0){
			ImageView userStar = (ImageView) findViewById(R.id.userStar);
			userText.setVisibility(View.GONE);
			userStar.setVisibility(View.GONE);
			current.setVisibility(View.GONE);
		}
		int numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.wallLayout);
		Resources r = getResources();
		int width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics()));
		int height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
		int right = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
		int bottom = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()));

		System.out.println("numMeasures: " + numMeasuresUser);
		for (int i = 0; i < numMeasuresUser-1; i++) {
			
			RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(width, height);
			arrowParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.ruler0);
			arrowParams.addRule(RelativeLayout.LEFT_OF, R.id.ruler0);
			

			
			ImageView pastArrow = new ImageView(this);
			pastArrow.setImageResource(R.drawable.green_arrow_paint);
			int measureResults = preferences.getInt(current_user + "Measure" + i, 0);
			arrowParams.setMargins(0, 0, 0, 175*(measureResults-1) - 31);//all in pixels
			pastArrow.setLayoutParams(arrowParams);
			pastArrow.setScaleType(ImageView.ScaleType.FIT_XY);
			pastArrow.setId(i+1);
			
			RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			textParams.addRule(RelativeLayout.ALIGN_BOTTOM, i+1);
			textParams.addRule(RelativeLayout.LEFT_OF, i+1);
			textParams.setMargins(0, 0, right, bottom);
			
			TextView pastText = new TextView(this);
			pastText.setTextSize(18);
			pastText.setText(preferences.getString(current_user + "Measure" + i + "Date", "No Date"));
			rl.addView(pastArrow, arrowParams);
			rl.addView(pastText, textParams);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wall, menu);
		return true;
	}
	
	public boolean users(MenuItem item){
		Intent i = new Intent(this, ViewUsers.class);
		startActivity(i);
	    return true;
	}
	
}
