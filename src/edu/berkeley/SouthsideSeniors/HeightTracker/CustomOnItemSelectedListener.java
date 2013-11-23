package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;
 
public class CustomOnItemSelectedListener implements OnItemSelectedListener {
 
  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
	  Context context = App.context();
	  String[] location = context.getResources().getStringArray(R.array.landmarksLocation);
	  String[] height = context.getResources().getStringArray(R.array.landmarksHeight);
	  Toast.makeText(parent.getContext(), location[pos], Toast.LENGTH_SHORT).show();
	  RelativeLayout layout = (RelativeLayout) view.getParent().getParent();
	  TextView spinnerText= (TextView) layout.getChildAt(1);
	  spinnerText.setText(" " + height[pos] + " feet");
	  
	  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	  TextView compareTV= (TextView) layout.getChildAt(2);
	  int current_height = preferences.getInt("current_height", 1);
	  compareTV.setText(" That's " + Integer.toString(12*Integer.parseInt(height[pos])/current_height));
	  System.out.println("Current: " + current_height);
	  
	  if (pos == 0){
		  layout.setBackgroundResource(R.drawable.bigben);
	  } else if  (pos == 1) {
		  layout.setBackgroundResource(R.drawable.campanile);
	  } else if  (pos == 2) {
		  layout.setBackgroundResource(R.drawable.eiffel_tower);
	  } else if  (pos == 3) {
		  layout.setBackgroundResource(R.drawable.empire_state);
	  } else if  (pos == 4) {
		  layout.setBackgroundResource(R.drawable.golden_gate);
	  } else if  (pos == 5) {
		  layout.setBackgroundResource(R.drawable.everest_mt);
	  } else if  (pos == 6) {
		  layout.setBackgroundResource(R.drawable.niagara_falls);
	  } else if  (pos == 7) {
		  layout.setBackgroundResource(R.drawable.statue_of_liberty);
	  } else if  (pos == 8) {
		  layout.setBackgroundResource(R.drawable.pisa);
	  } else if  (pos == 9) {
		  layout.setBackgroundResource(R.drawable.washington_monument);
	  }
  }
 
  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
  }
 
}