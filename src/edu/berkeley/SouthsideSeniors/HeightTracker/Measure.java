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
import android.widget.TextView;

public class Measure extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mLinearAccel;
	private double resultInMeters = 0;
	private float velocity = 0, velocity_old = 0;
	private boolean started = false;
	private LinearLayout chartLayout;
	private View mChart;
	private TextView magnitudeResult, debugXResult, debugYResult, debugZResult;
	private ArrayList<Datapoint> accelData;
	private float linearAccelz = 0, linearAccelz_old = 0;
	private long time, timeOld;
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
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnitudeResult = (TextView) findViewById(R.id.textView_magnitudeResult);
		chartLayout = (LinearLayout) findViewById(R.id.chart_container);
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
	@Override
	protected void onPause() {
        super.onPause();
        if (started == true) {
            mSensorManager.unregisterListener(this);
        }
    }

	public void onSensorChanged(SensorEvent event) {
		if (started) {
			time = System.nanoTime();
			float[] temp = {event.values[0], event.values[1], event.values[2]};
			//linearAccelz = event.values[2];
			linearAccelz = mag(temp)-(float)9.71;
            float dt = (float) ((time - timeOld) / 1000000000.0);
            //float dt = (float) ((time - timeOld) / 1000000000.0 / count++);
            timeOld = time;
    		if (Double.isNaN(velocity)) {
    			velocity = (float) 0.0;
    		}
    		velocity += (float)(dt*(linearAccelz+linearAccelz_old)/2.0);
    		linearAccelz_old = linearAccelz;

    		resultInMeters += (float)(dt*(velocity+velocity_old)/2.0);
    		velocity_old = velocity;
    		if (Double.isNaN(resultInMeters)) {
    			resultInMeters = 0;
    		}
    		
    		double zAccel = linearAccelz*1000;
    		double zVelocityFlipped = velocity*1000;
    		double zDistance = resultInMeters*1000;
            long timestamp = System.currentTimeMillis();
            Datapoint data = new Datapoint(timestamp, zDistance, zVelocityFlipped, 0);
            accelData.add(data);

    		//magnitudeResult.setText(String.valueOf((int) (velocity * 1000)));
    		magnitudeResult.setText(String.valueOf(resultInMeters));
    		//debugXResult.setText(String.valueOf((int) (0)));
    		//debugYResult.setText(String.valueOf((int) (0)));
    		//debugZResult.setText(String.valueOf((int) (linearAccelz * 1000)));
        }
	}

	public float mag(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	public float lowPassFilter(float base, float input) {
		float alpha = (float) 0.9;
		return alpha * base + (1 - alpha) * input;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}

	public void startMeasure(View view) {
		accelData = new ArrayList<Datapoint>();
        started = true;
		mSensorManager.registerListener(this, mLinearAccel,
				SensorManager.SENSOR_DELAY_FASTEST);
		count = 1;
		velocity = 0;
		velocity_old = 0;
		linearAccelz = 0;
		linearAccelz_old = 0;
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
		
		/*
		started = false;
        mSensorManager.unregisterListener(this);
        chartLayout.removeAllViews();
        openChart();
        */
	}
	
	private void openChart() {
        if (accelData != null || accelData.size() > 0) {
            long t = accelData.get(0).getTimestamp();
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
 
            XYSeries xSeries = new XYSeries("X");
            XYSeries ySeries = new XYSeries("Y");
            XYSeries zSeries = new XYSeries("Z");
 
            for (Datapoint data : accelData) {
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
                multiRenderer.addYTextLabel(i + 1, ""+i);
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

	public double metersToFeet(double meters) {
		return meters * 3.28084;
	}
}
