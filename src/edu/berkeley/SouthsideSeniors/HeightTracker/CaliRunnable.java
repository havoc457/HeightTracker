package edu.berkeley.SouthsideSeniors.HeightTracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;

public class CaliRunnable implements Runnable {
	private int seconds;
	private AlertDialog mDialog;
	private Measure mActivity;

	public CaliRunnable(Measure thisActivity, AlertDialog new_dialog,
			int new_seconds) {
		seconds = new_seconds;
		mDialog = new_dialog;
		mActivity = thisActivity;
	}

	public void run() {
		if (seconds < -4) {
			mDialog.dismiss();
			mActivity.cali_end();
			int g = (int) (mActivity.gravityOffset * 100);
			double g_result = g / 100.0;
			mDialog.dismiss();
			mDialog = new AlertDialog.Builder(mActivity).create();
			mDialog.setTitle("Calibration completed");
			mDialog.setMessage("Calibrated gravity value is "
					+ String.valueOf(g_result) + " m/s^2");
			mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			mDialog.show();
			return;
		}
		if (seconds < 1) {
			mActivity.cali_start();
			mDialog.dismiss();
			mDialog = new AlertDialog.Builder(mActivity).create();
			mDialog.setTitle("Calibrating...");
			mDialog.setMessage("Progress: "
					+ String.valueOf(seconds * (-20) + 20) + "%");
			mDialog.show();
			Handler caliHandler1 = new Handler();
			CaliRunnable runPrecaliCountdown = new CaliRunnable(mActivity,
					mDialog, seconds - 1);
			caliHandler1.postDelayed(runPrecaliCountdown, 1000);
			return;
		}

		mDialog.dismiss();
		mDialog = new AlertDialog.Builder(mActivity).create();
		mDialog.setTitle("Start calibrating in");
		mDialog.setMessage(String.valueOf(seconds));
		mDialog.show();
		Handler caliHandler1 = new Handler();
		CaliRunnable runPrecaliCountdown = new CaliRunnable(mActivity, mDialog,
				seconds - 1);
		caliHandler1.postDelayed(runPrecaliCountdown, 1000);
		return;
	}
}
