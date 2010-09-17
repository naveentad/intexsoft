package by.intexsoft.android.examples.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import by.intexsoft.android.examples.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LocationOverlay extends Overlay {

  private Bitmap map_flag;
  private GeoPoint geopoint;
  private Point point;

  public LocationOverlay(Context _context) {
    super();
    
    map_flag=BitmapFactory.decodeResource(_context.getResources(),R.drawable.map_flag);
    point=new Point();
  }
  
  protected void setCurrentPoint(GeoPoint point) {
	  this.geopoint=point;
  }
  
  protected GeoPoint getCurrentPoint() {
	  return geopoint;
  }
  
  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {	  

    Projection projection = mapView.getProjection();
    
    if (shadow == false) {
            projection.toPixels(getCurrentPoint(), point);
            canvas.drawBitmap(map_flag,point.x-map_flag.getWidth()/2,point.y-map_flag.getHeight() , null);
    }
    super.draw(canvas, mapView, shadow);
  }
	  
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		mapView.getController().animateTo(point);
		setCurrentPoint(point);
	  return false;
	}
}