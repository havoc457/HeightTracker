package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import edu.berkeley.SouthsideSeniors.HeightTracker.R;

public class Measure extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mLinearAccel;
	private double resultInMeters = 0;
	private float velocity = 0;
	private TextView magnitudeResult;
	private TextView debugXResult;
	private TextView debugYResult;
	private TextView debugZResult;
	private float[] linearAccel = { 0, 0, 0 };
	private long time;
	private long timeOld;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_measure);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mLinearAccel = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		magnitudeResult = (TextView) findViewById(R.id.textView_magnitudeResult);
		debugXResult = (TextView) findViewById(R.id.vec0);
		debugYResult = (TextView) findViewById(R.id.vec1);
		debugZResult = (TextView) findViewById(R.id.vec2);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		count = 0;
		time = System.nanoTime();
		timeOld = System.nanoTime();
		resultInMeters = 0;
		velocity = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measure, menu);
		return true;
	}

	public void onSensorChanged(SensorEvent event) {
		float dt = (float) 0.04;
		linearAccel[2] = lowPassFilter(linearAccel[2], event.values[2]);
		// scalar projection

		if (Double.isNaN(velocity)) {
			velocity = (float) 0.0;
		}

		time = System.nanoTime();
		dt = (float) ((time - timeOld) / 1000000000.0 / count++);
		velocity = velocity - linearAccel[2] * dt;

		resultInMeters += velocity * dt;
		if (Double.isNaN(resultInMeters)) {
			resultInMeters = 0;
		}

		//magnitudeResult.setText(String.valueOf((int) (velocity * 1000)));
		magnitudeResult.setText(String.valueOf(resultInMeters));
		debugXResult.setText(String.valueOf((int) (0)));
		debugYResult.setText(String.valueOf((int) (0)));
		debugZResult.setText(String.valueOf((int) (linearAccel[2] * 1000)));
	}

	public float mag(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	public float lowPassFilter(float base, float input) {
		float alpha = (float) 0.8;
		return alpha * base + (1 - alpha) * input;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}

	public void startMeasure(View view) {
		mSensorManager.registerListener(this, mLinearAccel,
				SensorManager.SENSOR_DELAY_GAME);
		count = 1;
		velocity = 0;
		linearAccel[0] = 0;
		linearAccel[1] = 0;
		linearAccel[2] = 0;
		resultInMeters = 0;
		time = System.nanoTime();
		timeOld = System.nanoTime();
		return;
	}

	public void results(View view) {
		mSensorManager.unregisterListener(this);
		Intent i = new Intent(this, Results.class);
		i.putExtra("heightInMeters", resultInMeters);
		startActivity(i);
	}

	public double metersToFeet(double meters) {
		return meters * 3.28084;
	}
}
