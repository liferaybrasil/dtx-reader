package com.liferay.smartbag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.wedeploy.android.WeDeploy;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StatusActivity extends AppCompatActivity {

	private BeaconManager beaconManager;
	private BeaconRegion region;
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

		beaconManager = new BeaconManager(this);
		region = new BeaconRegion(
			"bag", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
			21121, 1);

		beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
			@Override
			public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
				sendStatus();
				System.out.println("MANDOU STATUS " + _status);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		SystemRequirementsChecker.checkWithDefaultDialogs(this);

		beaconManager.connect(() -> beaconManager.startRanging(region));
	}

	@Override
	protected void onPause() {
		beaconManager.stopRanging(region);

		super.onPause();
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
						System.out.println("MANDOU PRO WEDEPLOY");
						System.out.println(response.succeeded());
					},
					throwable -> {
						System.out.println("NAO CONSEGUIR MANDAR PRO WEDEPLOY");
						System.out.println(throwable.getMessage());
					}
				);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}