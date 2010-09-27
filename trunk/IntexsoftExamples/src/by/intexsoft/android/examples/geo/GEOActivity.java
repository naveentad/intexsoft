package by.intexsoft.android.examples.geo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import by.intexsoft.android.examples.R;

public class GEOActivity extends Activity {

	private LocationProcessor locationProcessor;
	private TextView satsCount = null;
	private Button currentLocation;

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		setContentView(R.layout.gps);

		locationProcessor = new LocationProcessor(this,
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				locationListener, 15000, 50);

		satsCount = (TextView)findViewById(R.id.information);
		satsCount.setBackgroundColor(Color.BLACK);

		locationProcessor.setDebugDisplayView(satsCount);

		currentLocation = (Button) findViewById(R.id.my_location);
		currentLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentLocation.setEnabled(false);
				locationProcessor.initializeProvider();
				Toast.makeText(GEOActivity.this.getApplicationContext(),
						R.string.geo_message_receiving_location,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			final String provider = location.getProvider();
			// When location is received we stop listening for location updates
			locationProcessor.stopProvider();
			Toast.makeText(
					GEOActivity.this.getApplicationContext(),
					getText(R.string.geo_message_location_received)
							+ (provider != null ? " from " + provider : ""),
					Toast.LENGTH_SHORT).show();
			currentLocation.setEnabled(true);
			new GeoCodeTask(GEOActivity.this.getApplicationContext(), satsCount, location.getLatitude(), location.getLongitude())
			.execute("");

		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	protected void onResume() {
		currentLocation.setEnabled(true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		locationProcessor.stopProvider();
		super.onPause();
	}

	protected void onDestroy() {
		currentLocation = null;
		locationProcessor.onDestroy();
		locationProcessor = null;
		super.onDestroy();
	}

}