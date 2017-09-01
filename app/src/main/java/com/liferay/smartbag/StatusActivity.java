package com.liferay.smartbag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.service.BeaconManager.BeaconMonitoringListener;
import com.wedeploy.android.WeDeploy;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StatusActivity extends AppCompatActivity {

	private BeaconManager beaconManager;
	private String _status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_activity);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			_status = extras.getString("status");
			TextView textView = (TextView)findViewById(R.id.status);
			textView.setText(_status);
		}

		detectBeacon();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		beaconManager.disconnect();
	}

	private void detectBeacon() {
		beaconManager = new BeaconManager(getApplicationContext());
		beaconManager.setBackgroundScanPeriod(5000, 5000);
		beaconManager.connect(() -> beaconManager.startMonitoring(
			new BeaconRegion("bag", null, null, null)));

		beaconManager.setMonitoringListener(new BeaconMonitoringListener() {

		    @Override
		    public void onEnteredRegion(
	            BeaconRegion region, List<Beacon> beacons) {

		        sendStatus();
		    }

		    @Override
		    public void onExitedRegion(BeaconRegion region) {
		        System.out.println("saiu");
		    }
		});
	}

	private void sendStatus() {
		try {
			WeDeploy we = new WeDeploy.Builder().build();

			JSONObject status = new JSONObject();
			status.put("status", _status);
			status.put("timestamp", System.currentTimeMillis());

			we.data("https://db-dtx.wedeploy.io")
				.create("luggage", status)
				.asSingle()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
					response -> {
						System.out.println(response.succeeded());
					},
					throwable -> {
						System.out.println(throwable.getMessage());
					}
				);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}