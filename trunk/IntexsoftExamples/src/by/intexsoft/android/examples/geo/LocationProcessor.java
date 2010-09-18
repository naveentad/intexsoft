package by.intexsoft.android.examples.geo;

import java.util.Iterator;

import by.intexsoft.android.examples.R;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

public class LocationProcessor implements GpsStatus.Listener {

	private static final int GPS_FINAL_WAIT_INTERVAL = 30000; // in ms
	private static final int GPS_AVAILABLE_SATS_INTERVAL_1 = 5000; // in ms
	private static final int GPS_AVAILABLE_SATS_INTERVAL_1_NUMBER = 1;
	private static final int GPS_AVAILABLE_SATS_INTERVAL_2 = 10000; // in ms
	private static final int GPS_AVAILABLE_SATS_INTERVAL_2_NUMBER = 3;
	private static final int GPS_AVAILABLE_SATS_INTERVAL_3 = 18000; // in ms
	private static final int GPS_AVAILABLE_SATS_INTERVAL_3_NUMBER = 5;

	private boolean displayedLocationProviderSelectionScreen = false;

	GpsStatus status = null;
	private boolean firstFixReceived = false;
	private long startTime = -1;
	private boolean switchToNetwork = false;
	private String lastMessage = "";
	private int satsAvailable = 0;
	private long timeInterval;
	private TextView satsCount = null;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private float minDistance;
	private long minTime;
	private Activity activity;

	public LocationProcessor(Activity activity,
			LocationManager locationManager, LocationListener locationListener,
			long minTime, float minDistance) {
		this.activity = activity;
		this.locationManager = locationManager;
		this.locationListener = locationListener;
		this.minTime = minTime;
		this.minDistance = minDistance;
	}

	public void setDebugDisplayView(TextView tv) {
		satsCount = tv;
	}

	public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			boolean switchedAlready = switchToNetwork;
			status = locationManager.getGpsStatus(status);
			if (!switchToNetwork) {
				int sat_counter = 0;
				Iterator<GpsSatellite> it = status.getSatellites().iterator();
				while (it.hasNext()) {
					it.next();
					sat_counter++;
				}
				satsAvailable = sat_counter;
				if (firstFixReceived) {
					// If after a firstFix was received, we get less than 3 sats
					// available, we switch to network
					if (satsAvailable < GPS_AVAILABLE_SATS_INTERVAL_2_NUMBER) {
						switchToNetwork = true;
					}
				} else {
					timeInterval = System.currentTimeMillis() - startTime;

					if (timeInterval > GPS_AVAILABLE_SATS_INTERVAL_1) {
						// Check do we have enough satellites on wait interval 1
						if (satsAvailable < GPS_AVAILABLE_SATS_INTERVAL_1_NUMBER) {
							switchToNetwork = true;
						}
					}
					if (timeInterval > GPS_AVAILABLE_SATS_INTERVAL_2) {
						// Check do we have enough satellites on wait interval 2
						if (satsAvailable < GPS_AVAILABLE_SATS_INTERVAL_2_NUMBER) {
							switchToNetwork = true;
						}
					}
					if (timeInterval > GPS_AVAILABLE_SATS_INTERVAL_3) {
						// Check do we have enough satellites on wait interval 3
						if (satsAvailable < GPS_AVAILABLE_SATS_INTERVAL_3_NUMBER) {
							switchToNetwork = true;
						}
					}

					if (timeInterval > GPS_FINAL_WAIT_INTERVAL) {
						// If we are unable to get the fix till now, we switch
						// to
						// network anyway
						switchToNetwork = true;
					}
				}
			}
			if (!switchedAlready && switchToNetwork) {
				locationManager.removeUpdates(locationListener);
				if (locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, minTime,
							minDistance, locationListener);
				} else {
					Toast.makeText(activity.getApplicationContext(),
							R.string.gps_failed_network_disabled,
							Toast.LENGTH_LONG).show();
					openSettings();
				}
			}

			break;
		case GpsStatus.GPS_EVENT_STARTED:
			startTime = System.currentTimeMillis();
			switchToNetwork = false;
			firstFixReceived = false;
			lastMessage = "GPS started";
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			lastMessage = "GPS stopped";
			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			lastMessage = "GPS fix received";
			firstFixReceived = true;
			break;
		}

			if (satsCount != null) {
				satsCount.setText((switchToNetwork ? "Switched to network. "
						: satsAvailable + " " + timeInterval)
						+ " " + lastMessage);
			}

	}

	public Location initializeProvider() {
		boolean switchedToNetworkState = switchToNetwork;
		String provider = LocationManager.GPS_PROVIDER;
		if (!locationManager.isProviderEnabled(provider)) {
			provider = LocationManager.NETWORK_PROVIDER;
			if (!locationManager.isProviderEnabled(provider)) {
				Toast.makeText(activity.getApplicationContext(),
						R.string.providers_disabled, Toast.LENGTH_LONG).show();
				if (!displayedLocationProviderSelectionScreen) {
					displayedLocationProviderSelectionScreen = true;
					openSettings();
				}

			}
		} else {
			locationManager.addGpsStatusListener(this);
		}
		// Requesting updates
		locationManager.requestLocationUpdates(provider, minTime, minDistance,
				locationListener);
		if (switchedToNetworkState) {
			provider = LocationManager.NETWORK_PROVIDER;
		}
		return locationManager.getLastKnownLocation(provider);
	}

	private void openSettings() {
		try {
			Intent location_settings = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			activity.startActivity(location_settings);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stopProvider() {
		locationManager.removeUpdates(locationListener);
		locationManager.removeGpsStatusListener(this);
	}

	public void onDestroy() {
		this.activity = null;
		this.locationManager = null;
		this.locationListener = null;
	}
}
