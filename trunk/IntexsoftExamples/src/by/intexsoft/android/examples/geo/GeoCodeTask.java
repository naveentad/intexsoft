package by.intexsoft.android.examples.geo;



import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.TextView;

public class GeoCodeTask extends AsyncTask<String, Integer, String> {
	private Context context;
	private double latitude;
	private double longitude;
	private TextView details;

	public GeoCodeTask(Context context, TextView details,double latitude, double longitude) {
		super();
		this.context = context;
		this.latitude = latitude;
		this.longitude = longitude;
		this.details=details;
	}
	
	@Override
	protected String doInBackground(String... geodata) {
		String addressString = "";

		Geocoder gc = new Geocoder(context, Locale.getDefault());
		try {
				List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					if (address.getLocality() != null) {
						sb.append(address.getLocality());
					}
					if (address.getCountryName() != null) {
						if (sb.length() > 0) {
							sb.append(", ");
						}
						sb.append(address.getCountryName());
					}
				}
				addressString = sb.toString();
		} catch (IOException e) {
		}
		return addressString;

	}
	

	@Override
	protected void onPostExecute(String result) {
		//Display the result
		details.setText(result);
		
	}
}
