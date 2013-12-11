package edu.berkeley.SouthsideSeniors.HeightTracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class Measure extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccel;
	private double resultInMeters = 0, aggregateResultInMeters = 0, avgResultInMeters = 0;
	public boolean started = false, cali_started = false;
	private AlertDialog caliDialog;
	private Handler caliHandler1;
	private Runnable runPrecaliCountdown;
	private ArrayList<Datapoint> accelData, velocityData, v_GaussianData, outPutData;
	public static final int UP = 1, DOWN = -1;
	private int direction = UP, count = 1;
	public double gravityOffset;
	private int swipes = 0;
	private int originalProgress;
	private int smoothnessFactor = 10;
	private Measure thisActivity = this;
	private SharedPreferences preferences;
	private MediaPlayer mp;
	private int paddingDp;
	private TextView step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_measure);
		Context context = getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		resultInMeters = 0;
		aggregateResultInMeters = 0;
		avgResultInMeters = 0;
		started = false;
		cali_started = false;
		gravityOffset = (double) preferences.getFloat("gravityOffset", (float) 9.71);
		resultInMeters = 0;

		// Setting initial seekbar state
		LayerDrawable bgShape = (LayerDrawable) getResources().getDrawable(R.drawable.bg_shape);
		Drawable[] bgLayers = new Drawable[2];
		bgLayers[0] = bgShape.getDrawable(0);
		bgLayers[1] = getResources().getDrawable(R.drawable.feet_swipe_bm);
		LayerDrawable newbgLayers = new LayerDrawable(bgLayers);
		step = (TextView) findViewById(R.id.stepText);
		step.setText("Step\n 1/3");
		SeekBar sb = (SeekBar) findViewById(R.id.measure_slider);
		sb.setBackground(newbgLayers);
		sb.setProgress(0);
		int paddingPixel = 16;
		float density = context.getResources().getDisplayMetrics().density;
		paddingDp = (int)(paddingPixel * density);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser == true) {
					// only allow changes by 1 up or down
					if ((progress > (originalProgress + 35))
							|| (progress < (originalProgress - 35))) {
						seekBar.setProgress(originalProgress);
					} else {
						originalProgress = progress;
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				originalProgress = seekBar.getProgress();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				seekBar.setProgress(Math
						.round((progress + (smoothnessFactor / 2))
								/ smoothnessFactor)
								* smoothnessFactor);
				if (progress > 75) {
					mp.start();
					if (swipes < 2) {
						swipes++;
						LayerDrawable bgShape = (LayerDrawable) getResources().getDrawable(R.drawable.bg_shape);
						Drawable[] bgLayers = new Drawable[2];
						bgLayers[0] = bgShape.getDrawable(0);
						if (swipes == 1) {
							bgLayers[1] = getResources().getDrawable(R.drawable.head_swipe_bm);
							step.setText("Step\n 2/3");
							step.setPadding(0, 0, paddingDp, 0);
						} else if (swipes == 2) {
							bgLayers[1] = getResources().getDrawable(R.drawable.feet_swipe_bm_dash);
							step.setText("Step\n 3/3");
							step.setPadding(0, 0, paddingDp, 0);
						}

						LayerDrawable newbgLayers = new LayerDrawable(bgLayers);
						SeekBar sb = (SeekBar) findViewById(R.id.measure_slider);
						sb.setBackground(newbgLayers);
						seekBar.setProgress(0);
						startMeasure();
					} else {
						swipes = 0;
						results();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measure, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (started == true) {
			mSensorManager.unregisterListener(this);
		}
	}

	public void onSensorChanged(SensorEvent event) {
		if (started || cali_started) {
			long timestamp = System.currentTimeMillis();
			float[] temp = { event.values[0], event.values[1], event.values[2] };
			double accel = (mag(temp) - (float) gravityOffset);
			Datapoint data = new Datapoint(timestamp, 0, 0, accel * direction);
			accelData.add(data);
		}
	}

	// calculate distance from the acceleration data
	public void calculateDistance() {
		if (accelData == null || accelData.isEmpty()) {
			resultInMeters = 0;
			return;
		}

		double dt;
		long time0, time1;
		double accel0, accel1;
		double velocity0, velocity1, v_max, v_end, correctionStr;
		time0 = accelData.get(0).getTimestamp();
		time1 = time0;
		accel0 = accelData.get(0).getZ();
		accel1 = accel0;
		velocity0 = 0;
		velocity1 = 0;
		v_max = 0;
		v_end = 0;

		// calculating velocity
		for (Datapoint data : accelData) {
			time1 = data.getTimestamp();
			accel1 = data.getZ();
			dt = ((double) (time1 - time0)) / 1000.0;
			time0 = time1;
			velocity1 += (dt * (accel0 + accel1) / 2.0);
			v_max = Math.max(v_max, velocity1);
			accel0 = accel1;
			Datapoint vData = new Datapoint(time1, 0, velocity1, 0);
			velocityData.add(vData);
		}
		time0 = velocityData.get(0).getTimestamp();
		time1 = time0;
		velocity0 = velocityData.get(0).getY();

		v_end = velocityData.get(velocityData.size() - 1).getY();

		// velocity end point matching
		for (int i = velocityData.size() - 1; i > -1; i--) {
			Datapoint currentData = velocityData.get(i);
			double currentV = currentData.getY();
			if (currentData.getY() == v_max) {
				break;
			}
			if (Math.abs(currentV - v_end) > Math.abs(v_end)) {
				correctionStr = 0;
			} else {
				correctionStr = Math.sqrt(1 - Math.abs((currentV - v_end)
						/ v_end));
			}
			currentData.setY(currentV - currentV * correctionStr);
		}

		// Gaussian filter on velocity
		for (int i = 0; i < velocityData.size(); i++) {
			double convolution = 0;
			double delta = 0;
			double delta_default = 0.015;
			double timeDiff = 0;
			Datapoint currentData = velocityData.get(i);
			// convolution within a range of 10 intervals
			for (int j = -20; j < 21; j++) {
				if (((i + j) < 0) || (i + j) > velocityData.size() - 1) {
					convolution += currentData.getY()
							* gaussian(j * delta_default) * delta_default;
					continue;
				}
				Datapoint convolutingData = velocityData.get(i + j);
				timeDiff = (currentData.getTimestamp() - convolutingData
						.getTimestamp()) / 1000.0;
				// getting delta
				if (i + j - 1 < 0) {
					delta = (velocityData.get(i + j + 1).getTimestamp() - convolutingData
							.getTimestamp()) / 1000.0;
				} else if (i + j + 1 > velocityData.size() - 1) {
					delta = (convolutingData.getTimestamp() - velocityData.get(
							i + j - 1).getTimestamp()) / 1000.0;
				} else {
					delta = (velocityData.get(i + j + 1).getTimestamp() - velocityData
							.get(i + j - 1).getTimestamp()) / 2000.0;
				}
				// improve with a more accurate delta
				convolution += convolutingData.getY() * gaussian(timeDiff)
						* delta;
			}
			Datapoint v_GaussianDatapoint = new Datapoint(
					currentData.getTimestamp(), 0, convolution,
					currentData.getY());
			v_GaussianData.add(v_GaussianDatapoint);
		}

		// calculating distance based on filtered velocity data
		for (Datapoint data : v_GaussianData) {
			time1 = data.getTimestamp();
			velocity1 = data.getY();
			dt = ((double) (time1 - time0)) / 1000.0;
			time0 = time1;
			resultInMeters += (dt * (velocity0 + velocity1) / 2.0);
			velocity0 = velocity1;
			Datapoint dva = new Datapoint(time1, resultInMeters * 1000,
					velocity1 * 1000, data.getZ() * 1000);
			// for debugging Z is the pre filtered v
			outPutData.add(dva);
		}
	}

	public double gaussian(double x) {
		double sigma = 0.04;
		return Math.pow(Math.E, -(x * x) / 2.0 / (sigma * sigma))
				/ Math.sqrt(2 * Math.PI * sigma * sigma);
	}

	public float mag(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}

	public void startMeasure() {
		if (started == false) {
			accelData = new ArrayList<Datapoint>();
			started = true;
			resultInMeters = 0;
			aggregateResultInMeters = 0;
			avgResultInMeters = 0;
			count = 1;
			direction = UP;
			mSensorManager.registerListener(this, mAccel,
					SensorManager.SENSOR_DELAY_FASTEST);
			return;
		}

		if (started == true) {
			// calculate last distance
			mSensorManager.unregisterListener(this);
			velocityData = new ArrayList<Datapoint>();
			v_GaussianData = new ArrayList<Datapoint>();
			outPutData = new ArrayList<Datapoint>();
			calculateDistance();
			aggregateResultInMeters += resultInMeters;

			// new measurement
			accelData = new ArrayList<Datapoint>();
			resultInMeters = 0;
			count++;
			direction = direction * (-1);
			mSensorManager.registerListener(this, mAccel,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		return;
	}

	public void results() {
		if (started == false) {
			return;
		}
		started = false;
		mSensorManager.unregisterListener(this);
		velocityData = new ArrayList<Datapoint>();
		v_GaussianData = new ArrayList<Datapoint>();
		outPutData = new ArrayList<Datapoint>();
		calculateDistance();
		aggregateResultInMeters += resultInMeters;
		avgResultInMeters = aggregateResultInMeters / count;

		Intent i = new Intent(this, Results.class);
		i.putExtra("heightInInches", metersToInches(avgResultInMeters));
		startActivity(i);
	}
	
	public void cali_start() {
		if (cali_started == false) { 
	 		accelData = new ArrayList<Datapoint>();
	 		cali_started = true;
	 		direction = UP;
	 		mSensorManager.registerListener(this, mAccel,
	 		SensorManager.SENSOR_DELAY_FASTEST); 
	 	}
	}
	
	public void cali_end() {
		mSensorManager.unregisterListener(this);
	 	double avgGravityError = 0;
	 	int num = 0; 
	  	for (int i = 0; i < accelData.size() - 1; i++) { 

	  		avgGravityError += accelData.get(i).getZ(); 
	  		num++; 
	  	} 
	  	avgGravityError = avgGravityError / num; 
	  	gravityOffset += avgGravityError;
	  	cali_started = false;
	  	SharedPreferences.Editor editor = preferences.edit();
	  	editor.putFloat("gravityOffset", (float) gravityOffset);
	  	editor.commit();
	}

	public void calibrate(View view) {
		if (started == true || cali_started == true) {
			return;
		}
		caliDialog = new AlertDialog.Builder(this).create();
		caliDialog
				.setMessage("Please place the phone on a flat surface to calibrate. The whole process takes 8 seconds to complete.");
		caliDialog.setTitle(R.string.cali_title);
		caliDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						caliDialog.dismiss();
						preCaliCountdown(3);
					}
				});

		caliDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		caliDialog.show();
		return;
	}
	
	public void preCaliCountdown(int seconds) {
		caliHandler1 = new Handler();
		runPrecaliCountdown = new CaliRunnable(thisActivity, caliDialog, seconds);
		caliHandler1.postDelayed(runPrecaliCountdown, 1000);
	}

	public boolean help(View view) {
		Intent i = new Intent(this, Tutorial.class);
		startActivity(i);
		return true;
	}

	public int metersToInches(double meters) {
		return (int) Math.round(meters * 39.3701);
	}

}
