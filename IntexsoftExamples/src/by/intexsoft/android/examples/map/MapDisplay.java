package by.intexsoft.android.examples.map;

import java.util.List;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ZoomControls;
import by.intexsoft.android.examples.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapDisplay extends MapActivity {

	private MapController mapController;
	private MapView myMapView;
	private LocationOverlay locationOverlay;

	private GeoPoint rememberMapCenter,rememberLocation = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.map);

		// Get the MapView and its Controller
		myMapView = (MapView) findViewById(R.id.myMapView);
		mapController = myMapView.getController();

		// Configure the map display options
		myMapView.setSatellite(false);
		myMapView.setStreetView(false);
		mapController.setZoom(12);

		List<Overlay> overlays = myMapView.getOverlays();
		locationOverlay = new LocationOverlay(this);
		overlays.add(locationOverlay);

		myMapView.setBuiltInZoomControls(false);
		ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onZoom(true);
			}
		});
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onZoom(false);
			}
		});

	}

	private void onZoom(boolean zoomIn) {
		try {
			System.gc();
			if (zoomIn) {
				mapController.zoomIn();
			} else {
				mapController.zoomOut();
			}
			System.gc();
		} catch (OutOfMemoryError e) {
			System.gc();
			e.printStackTrace();
		} catch (Exception e) {
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getPointerCount() > 1) {
			// We will not process multi-touch events
			return false;
		} else {
			return super.dispatchTouchEvent(ev);
		}

	}

	@Override
	protected void onResume() {
		if (rememberMapCenter==null) {
			Double latitude = 53.710218 * 1E6;
			Double longitude = 23.804192 * 1E6;
			rememberMapCenter = new GeoPoint(latitude.intValue(), longitude
					.intValue());
		}
		mapController.setCenter(rememberMapCenter);
		
		if (rememberLocation==null) {
			rememberLocation=rememberMapCenter;
		}
		locationOverlay.setCurrentPoint(rememberLocation);
		
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		myMapView = null;
		mapController = null;
		locationOverlay = null;
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		GeoPoint mapCenter=myMapView.getMapCenter();
		savedInstanceState.putIntArray("mapcenter", new int[]{mapCenter.getLatitudeE6(),mapCenter.getLongitudeE6()});
		GeoPoint location=locationOverlay.getCurrentPoint();
		savedInstanceState.putIntArray("location", new int[]{location.getLatitudeE6(),location.getLongitudeE6()});
		savedInstanceState.putInt("zoom", myMapView.getZoomLevel());
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int[] mapcenter=savedInstanceState.getIntArray("mapcenter");
		rememberMapCenter=new GeoPoint(mapcenter[0], mapcenter[1]);
		int[] location=savedInstanceState.getIntArray("location");
		rememberLocation = new GeoPoint(location[0],location[1]);
		mapController.setZoom(savedInstanceState.getInt("zoom"));
	}

}