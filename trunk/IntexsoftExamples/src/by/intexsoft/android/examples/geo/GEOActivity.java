package by.intexsoft.android.examples.geo;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import by.intexsoft.android.examples.R;

public class GEOActivity extends Activity {

	private LocationProcessor locationProcessor;
	private Button currentLocation;

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		setContentView(R.layout.gps);

		locationProcessor = new LocationProcessor(this,
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				locationListener, 0, 0);

		currentLocation = (Button) findViewById(R.id.my_location);
		currentLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentLocation.setEnabled(false);

				((TextView) GEOActivity.this.findViewById(R.id.satscount)).setText("");
				((TextView) GEOActivity.this.findViewById(R.id.time)).setText("");
				((TextView) GEOActivity.this.findViewById(R.id.location)).setText("");
				((TextView) GEOActivity.this.findViewById(R.id.resolved_location)).setText("");
				
				locationProcessor.initializeProvider();
				((TextView) GEOActivity.this.findViewById(R.id.information)).setText(R.string.geo_message_receiving_location);
			}
		});
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			final String provider = location.getProvider();
			// When location is received we stop listening for location updates
			locationProcessor.stopProvider();
			((TextView) GEOActivity.this.findViewById(R.id.information))
					.setText(getText(R.string.geo_message_location_received)
							+ (provider != null ? " "+getText(R.string.geo_message_from)+" " + provider : ""));

			currentLocation.setEnabled(true);
			((TextView) findViewById(R.id.resolved_location)).setText(R.string.geo_text_resolve_location);
			((TextView) findViewById(R.id.location)).setText(getText(R.string.geo_text_location)+":\n"+location.getLatitude()+"\n"+location.getLongitude());
			
			new GeoCodeTask(GEOActivity.this.getApplicationContext(),
					(TextView) findViewById(R.id.resolved_location), location.getLatitude(), location.getLongitude())
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