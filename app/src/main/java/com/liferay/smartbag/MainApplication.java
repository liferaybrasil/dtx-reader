package com.liferay.smartbag;

import android.app.Application;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.service.BeaconManager.BeaconMonitoringListener;
import com.estimote.coresdk.service.BeaconManager.ServiceReadyCallback;
import com.wedeploy.android.WeDeploy;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainApplication extends Application {

	private BeaconManager beaconManager;

	@Override
	public void onCreate() {
		super.onCreate();

		sendStatus();
//		detectBeacon();
	}

	private void detectBeacon() {
		beaconManager = new BeaconManager(getApplicationContext());
		beaconManager.setBackgroundScanPeriod(5000, 5000);
		beaconManager.connect(new ServiceReadyCallback() {

			@Override
			public void onServiceReady() {
				beaconManager.startMonitoring(
					new BeaconRegion("bag", null, null, null));
			}

		});

		beaconManager.setMonitoringListener(new BeaconMonitoringListener() {

				@Override
				public void onEnteredRegion(
					BeaconRegion region, List<Beacon> beacons) {

					System.out.println("entrou");
				}

				@Override
				public void onExitedRegion(BeaconRegion region) {
					System.out.println("saiu");
				}
			}

		);
	}

	private void sendStatus() {
		try {
			WeDeploy we = new WeDeploy.Builder().build();

			JSONObject status = new JSONObject();
			status.put("status", "esteira");
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