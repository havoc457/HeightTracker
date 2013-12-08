package edu.berkeley.SouthsideSeniors.HeightTracker;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class Measure extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccel;
	private double resultInMeters = 0;
	private boolean started = false;
	private LinearLayout chartLayout;
	private View mChart;
	//private TextView magnitudeResult debugXResult, debugYResult, debugZResult;
	private ArrayList<Datapoint> accelData, velocityData, outPutData;
	private int swipes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_measure);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//magnitudeResult = (TextView) findViewById(R.id.textView_magnitudeResult);
		//chartLayout = (LinearLayout) findViewById(R.id.chart_container);
//		debugXResult = (TextView) findViewById(R.id.vec0);
//		debugYResult = (TextView) findViewById(R.id.vec1);
//		debugZResult = (TextView) findViewById(R.id.vec2);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		resultInMeters = 0;
		
		SeekBar seekBar = (SeekBar)findViewById(R.id.measure_slider); 
	    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

		    @Override 
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
		    } 
	
		    @Override 
		    public void onStartTrackingTouch(SeekBar seekBar) { 
		    } 
	
		    @Override 
		    public void onStopTrackingTouch(SeekBar seekBar) { 
		    	int progress = seekBar.getProgress();
		    	if (progress > 90){
		    		if (swipes == 0){
		    			startMeasure();
		    			swipes++;
		    			seekBar.setProgress(0);
		    		} else if (swipes == 1){
		    			results();
		    			swipes--;
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
		if (started) {
			float[] temp = { event.values[0], event.values[1], event.values[2] };
			double accel = (mag(temp) - (float) 9.71);
			long timestamp = System.currentTimeMillis();
			Datapoint data = new Datapoint(timestamp, 0, 0, accel);
			accelData.add(data);

			// magnitudeResult.setText(String.valueOf((int) (velocity * 1000)));
			// magnitudeResult.setText(String.valueOf(resultInMeters));
			// debugXResult.setText(String.valueOf((int) (0)));
			// debugYResult.setText(String.valueOf((int) (0)));
			// debugZResult.setText(String.valueOf((int) (linearAccelz *
			// 1000)));
		}
	}

	// calculate distance from the acceleration data
	public void calculateDistance() {
		if (accelData == null || accelData.isEmpty()) {
			resultInMeters = 0;
			return;
		}
		/*
		 * // needs at least 3 points to calculate generalized simpson if
		 * (accelData.get(1) == null || accelData.get(2) == null) {
		 * resultInMeters = 0; return; }
		 */

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

		for (Datapoint data : accelData) {
			time1 = data.getTimestamp();
			accel1 = data.getZ();
			dt = ((double)(time1 - time0)) / 1000.0;
			time0 = time1;
			velocity1 += (dt * (accel0 + accel1) / 2.0);
			v_max = Math.max(v_max, velocity1);
			accel0 = accel1;
			Datapoint vData = new Datapoint(time1, 0,
					velocity1, 0);
			velocityData.add(vData);
		}
		time0 = velocityData.get(0).getTimestamp();
		time1 = time0;
		velocity0 = velocityData.get(0).getY();
		
		v_end = velocityData.get(velocityData.size()-1).getY();
		
		for (int i = velocityData.size()-1; i>-1; i--) {
			Datapoint currentData = velocityData.get(i);
			double currentV = currentData.getY();
			if (currentData.getY() == v_max) {
				break;
			}
			if (Math.abs(currentV-v_end)>Math.abs(v_end)) {
				correctionStr = 0;
			} else {
				correctionStr = Math.sqrt(1 - Math.abs((currentV - v_end)/v_end));
			}
			currentData.setY(currentV - currentV*correctionStr);
		}
		
		for (Datapoint data : velocityData) {
			time1 = data.getTimestamp();
			velocity1 = data.getY();
			dt = ((double)(time1 - time0)) / 1000.0;
			time0 = time1;
			resultInMeters += (dt * (velocity0 + velocity1) / 2.0);
			velocity0 = velocity1;
			Datapoint dva = new Datapoint(time1, resultInMeters * 1000, velocity1 * 1000, 0);
			outPutData.add(dva);
		}
	}

	public float mag(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	/*
	 * public float lowPassFilter(float base, float input) { float alpha =
	 * (float) 0.9; return alpha * base + (1 - alpha) * input; }
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}

	public void startMeasure() {
		accelData = new ArrayList<Datapoint>();
		started = true;
		mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
		resultInMeters = 0;
		return;
	}

	public void results() {
		if (started == false) {
			return;
		}
		started = false;
		mSensorManager.unregisterListener(this);
		velocityData = new ArrayList<Datapoint>();
		outPutData = new ArrayList<Datapoint>();
		calculateDistance();

		
		Intent i = new Intent(this, Results.class);
		i.putExtra("heightInInches", metersToInches(resultInMeters)); startActivity(i);
		 
		//magnitudeResult.setText(String.valueOf(resultInMeters));
		//chartLayout.removeAllViews();
		//openChart();
	}

	private void openChart() {
		if (outPutData != null || outPutData.size() > 0) {
			long t = outPutData.get(0).getTimestamp();
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

			XYSeries xSeries = new XYSeries("X");
			XYSeries ySeries = new XYSeries("Y");
			XYSeries zSeries = new XYSeries("Z");

			for (Datapoint data : outPutData) {
				xSeries.add(data.getTimestamp() - t, data.getX());
				ySeries.add(data.getTimestamp() - t, data.getY());
				zSeries.add(data.getTimestamp() - t, data.getZ());
			}

			dataset.addSeries(xSeries);
			dataset.addSeries(ySeries);
			dataset.addSeries(zSeries);

			XYSeriesRenderer xRenderer = new XYSeriesRenderer();
			xRenderer.setColor(Color.RED);
			xRenderer.setPointStyle(PointStyle.CIRCLE);
			xRenderer.setFillPoints(true);
			xRenderer.setLineWidth(1);
			xRenderer.setDisplayChartValues(false);

			XYSeriesRenderer yRenderer = new XYSeriesRenderer();
			yRenderer.setColor(Color.GREEN);
			yRenderer.setPointStyle(PointStyle.CIRCLE);
			yRenderer.setFillPoints(true);
			yRenderer.setLineWidth(1);
			yRenderer.setDisplayChartValues(false);

			XYSeriesRenderer zRenderer = new XYSeriesRenderer();
			zRenderer.setColor(Color.BLUE);
			zRenderer.setPointStyle(PointStyle.CIRCLE);
			zRenderer.setFillPoints(true);
			zRenderer.setLineWidth(1);
			zRenderer.setDisplayChartValues(false);

			XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
			multiRenderer.setXLabels(0);
			multiRenderer.setLabelsColor(Color.RED);
			multiRenderer.setChartTitle("t vs (x,y,z)");
			multiRenderer.setXTitle("Sensor Data");
			multiRenderer.setYTitle("Values of Acceleration");
			multiRenderer.setZoomButtonsVisible(true);
			for (int i = 0; i < accelData.size(); i++) {

				multiRenderer.addXTextLabel(i + 1, ""
						+ (accelData.get(i).getTimestamp() - t));
			}
			for (int i = 0; i < 12; i++) {
				multiRenderer.addYTextLabel(i + 1, "" + i);
			}

			multiRenderer.addSeriesRenderer(xRenderer);
			multiRenderer.addSeriesRenderer(yRenderer);
			multiRenderer.addSeriesRenderer(zRenderer);
			multiRenderer.setLabelsTextSize(30);

			// Getting a reference to LinearLayout of the MainActivity Layout

			// Creating a Line Chart
			mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
					multiRenderer);

			// Adding the Line Chart to the LinearLayout
			chartLayout.addView(mChart);
		}
	}

	public int metersToInches(double meters) {
		return (int) Math.round(meters * 39.3701);
	}
}
