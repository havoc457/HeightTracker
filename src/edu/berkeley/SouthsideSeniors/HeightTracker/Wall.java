package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Wall extends Activity {

	private SharedPreferences preferences;
	private int num_users;
	private ScrollView scroll;
	private TextView userText;
	private int current_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wall);
		
		scroll = (ScrollView) findViewById(R.id.wallScroll);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		num_users = Integer.parseInt(preferences.getString("num_users","0"));
		userText = (TextView) findViewById(R.id.userHeightText);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		String current_user;
		if (num_users == 0){
			current_user = "Please Add New User";
			current_height = 0;
		} else {
			current_user = preferences.getString("current_user", "");
			current_height = preferences.getInt(current_user + "current_height", 0);
			userText.setText(current_user);
		}
		setTitle(current_user);

		int dadHeight = preferences.getInt(current_user + "dadHeight", 0);
		int momHeight = preferences.getInt(current_user + "momHeight", 0);
		int eventual = preferences.getInt(current_user + "eventual", 0);

		ImageView current = (ImageView) findViewById(R.id.current);
		ImageView dad = (ImageView) findViewById(R.id.dadHeightArrow);
		ImageView mom = (ImageView) findViewById(R.id.momHeightArrow);
		ImageView eventualView = (ImageView) findViewById(R.id.futureArrow);

		MarginLayoutParams mlp = (MarginLayoutParams) current.getLayoutParams();
		mlp.setMargins(0, 0, 0, 175*(current_height-1) - 31);//all in pixels
		current.setLayoutParams(mlp);

		mlp = (MarginLayoutParams) dad.getLayoutParams();
		mlp.setMargins(0, 0, 0, 175*dadHeight - 31);//all in pixels
		dad.setLayoutParams(mlp);

		mlp = (MarginLayoutParams) mom.getLayoutParams();
		mlp.setMargins(0, 0, 0, 175*momHeight - 31);//all in pixels
		mom.setLayoutParams(mlp);

		mlp = (MarginLayoutParams) eventualView.getLayoutParams();
		mlp.setMargins(0, 0, 0, 175*eventual - 31);//all in pixels
		eventualView.setLayoutParams(mlp);

		TextView dadText = (TextView) findViewById(R.id.dadHeightText);
		TextView momText = (TextView) findViewById(R.id.momHeightText);
		TextView eventualText = (TextView) findViewById(R.id.futureText);

		boolean momBool = false, dadBool = false, eventualBool = false, dadIcon = false, momIcon = false;

		if ((dadHeight == 0 && momHeight == 0) || num_users == 0){
			momBool = true;
			dadBool = true;
			eventualBool = true;
		} else if (momHeight == 0){
			momBool = true;
		} else if (dadHeight == 0){
			dadBool = true;
		}

		ImageView star = (ImageView) findViewById(R.id.userStar);
		ImageView dadIconView = (ImageView) findViewById(R.id.dadIcon);
		ImageView momIconView = (ImageView) findViewById(R.id.momIcon);

		if (current_height == 0){
			userText.setVisibility(View.GONE);
			current.setVisibility(View.GONE);
			star.setVisibility(View.GONE);
		}

		if (current_user.length() > 9){
			star.setVisibility(View.GONE);
		}

		MarginLayoutParams currentMLP = (MarginLayoutParams) userText.getLayoutParams();
		MarginLayoutParams eventualMLP = (MarginLayoutParams) eventualText.getLayoutParams();
		MarginLayoutParams dadMLP = (MarginLayoutParams) dadText.getLayoutParams();
		MarginLayoutParams momMLP = (MarginLayoutParams) momText.getLayoutParams();
		if (dadHeight == momHeight && current_height == momHeight){
			momBool = true;
			dadBool = true;
			dadIcon = true;
			momIcon = true;
			userText.setText(current_user + "\nDad, Mom");
			userText.setTextSize(18);
			currentMLP.setMargins(0, 0, 0, -23);//all in pixels
			userText.setLayoutParams(currentMLP);
		} else if (dadHeight == momHeight && eventual == momHeight){
			momBool = true;
			dadBool = true;
			dadIcon = true;
			momIcon = true;
			eventualText.setText("Dad, Mom\nFuture Height");
			eventualText.setTextSize(18);
			eventualMLP.setMargins(0, 0, 0, -23);//all in pixels
			eventualText.setLayoutParams(eventualMLP);
		} else if (dadHeight == current_height && eventual == dadHeight){
			eventualBool = true;
			dadBool = true;
			dadIcon = true;
			userText.setText(current_user + ", Dad\nFuture Height");
			userText.setTextSize(18);
			currentMLP.setMargins(0, 0, 0, -23);//all in pixels
			userText.setLayoutParams(currentMLP);
		} else if (momHeight == current_height && eventual == momHeight){
			eventualBool = true;
			momBool = true;
			momIcon = true;
			userText.setText(current_user + ", Mom\nFuture Height");
			userText.setTextSize(18);
			currentMLP.setMargins(0, 0, 0, -23);//all in pixels
			userText.setLayoutParams(currentMLP);
		} else if (current_height == eventual){
			eventualBool = true;
			userText.setText(current_user + ", Future Height");
			userText.setTextSize(18);
			currentMLP.setMargins(0, 0, 0, -23);//all in pixels
			userText.setLayoutParams(currentMLP);
		} else if (dadHeight == momHeight){
			momBool = true;
			dadIcon = true;
			momIcon = true;
			dadText.setText("Dad, Mom");
			dadMLP.setMargins(0, 0, 0, -23);//all in pixels
			dadText.setLayoutParams(dadMLP);
		} else if (dadHeight == eventual){
			eventualBool = true;
			dadIcon = true;
			dadText.setText("Dad\nFuture Height");
			dadText.setTextSize(18);
			dadMLP.setMargins(0, 0, 0, -23);//all in pixels
			dadText.setLayoutParams(dadMLP);
		} else if (dadHeight == current_height){
			dadBool = true;
			dadIcon = true;
			userText.setText(current_user + ", Dad");
			userText.setTextSize(18);
			currentMLP.setMargins(0, 0, 0, -23);//all in pixels
			userText.setLayoutParams(currentMLP);
		} else if (momHeight == eventual){
			eventualBool = true;
			momIcon = true;
			momText.setText("Mom\nFuture Height");
			momText.setTextSize(18);
			momMLP.setMargins(0, 0, 0, -23);//all in pixels
			momText.setLayoutParams(momMLP);
		} else if (momHeight == current_height){
			momBool = true;
			momIcon = true;
			momText.setText(current_user + "\nMom");
			momText.setTextSize(18);
			momMLP.setMargins(0, 0, 0, -23);//all in pixels
			momText.setLayoutParams(momMLP);
		} 
		
		scroll.smoothScrollTo(0, Math.round(userText.getY()));

		if (dadIcon){
			dadIconView.setVisibility(View.GONE);
		}

		if (momIcon){
			momIconView.setVisibility(View.GONE);
		}

		if (momBool){
			momText.setVisibility(View.GONE);
			mom.setVisibility(View.GONE);
		}
		if (dadBool){
			dad.setVisibility(View.GONE);
			dadText.setVisibility(View.GONE);
		}
		if (eventualBool){
			eventualText.setVisibility(View.GONE);
			eventualView.setVisibility(View.GONE);
		}

		int numMeasuresUser = preferences.getInt(current_user + "numMeasuresUser", 0);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.wallLayout);
		Resources r = getResources();
		int width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics()));
		int height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
		int right = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
		int bottom = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()));

		int measureResults[] = new int[numMeasuresUser]; 

		for (int i = 0; i < numMeasuresUser; i++){
			measureResults[i] = preferences.getInt(current_user + "Measure" + i, 0);
		}

		for (int i = numMeasuresUser-1; i >= 0; i--) {
			boolean alreadyPlacedDate = false;
			if (measureResults[i] != eventual && measureResults[i] != current_height && measureResults[i] != dadHeight 
					&& measureResults[i] != momHeight){
				for (int j = i+1; j < numMeasuresUser; j++){
					if (measureResults[i] == measureResults[j]){
						alreadyPlacedDate = true;
					}
				}
				if (!alreadyPlacedDate){
					RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(width, height);
					arrowParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.ruler0);
					arrowParams.addRule(RelativeLayout.LEFT_OF, R.id.ruler0);

					ImageView pastArrow = new ImageView(this);
					pastArrow.setImageResource(R.drawable.green_arrow_paint);

					arrowParams.setMargins(0, 0, 0, 175*(measureResults[i]-1) - 31);//all in pixels
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
		}
		 ViewTreeObserver vto = scroll.getViewTreeObserver();
		 vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		      public void onGlobalLayout() {
		           scroll.scrollTo(0, userText.getBottom()-900);
		      }
		 });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		scroll.smoothScrollTo(0, userText.getBottom()-900);
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
