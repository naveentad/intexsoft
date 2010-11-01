package by.intexsoft.android.examples.camera;

import by.intexsoft.android.examples.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;

public class ImageCaptureCallback implements PictureCallback {
	/** Tag. */
	public static final String TAG = "ImageCaptureCallback";
	/** Captured image. */
	public static final String CAPTURED_IMAGE = "CAPTURED_IMAGE";
	/** Activity. */
	private Activity activity;

	private int orientation;

	public ImageCaptureCallback(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			try {
				// decode image data
				Bitmap decoded = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// rotate and scale bitmap
				Bitmap graphics = rotateAndScaleBitmap(decoded);
				// insert image in media store
				MediaStore.Images.Media.insertImage(
						getActivity().getContentResolver(), graphics, null,
						null);
//						);
				decoded.recycle();
				graphics.recycle();
				System.gc();
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
				System.gc();
				return;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			Log.e(TAG, error.getMessage());
			getActivity().setResult(Activity.RESULT_CANCELED);
			getActivity().finish();
			System.gc();			
			return;
		}
		getActivity().finish();
	}

	
	private Bitmap rotateAndScaleBitmap(Bitmap bm) {

		int angle=getRotationAngle();
		int image_width=CameraActivity.CAPTURE_DESIRED_WIDTH;
		int image_height=CameraActivity.CAPTURE_DESIRED_HEIGHT;
		if (angle==270 || angle==90) {
			image_width=CameraActivity.CAPTURE_DESIRED_HEIGHT;
			image_height=CameraActivity.CAPTURE_DESIRED_WIDTH;
		}
		Bitmap graphics = Bitmap.createBitmap(image_width, image_height,
				Config.RGB_565);
		Canvas g = new Canvas(graphics);
		int middleX = image_width / 2;
		int middleY = image_height / 2;
		g.translate(middleX, middleY);
		g.rotate(-angle);
		if (image_width>image_height) {
			g.translate(-middleX, -middleY);
		} else {
			g.translate(-middleY,-middleX);
		}
		Rect rect=new Rect(0, 0, CameraActivity.CAPTURE_DESIRED_WIDTH, CameraActivity.CAPTURE_DESIRED_HEIGHT);
		g.drawBitmap(bm, null, rect, null);
		Bitmap binocular=BitmapFactory.decodeResource(activity.getApplicationContext().getResources(),getBinocularResource());
		g.drawBitmap(binocular,null, rect, null);
		binocular.recycle();
		return graphics;
	}
	
	/**
	 * Get activity.
	 * 
	 * @return activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Get rotation angle.
	 * 
	 * @return rotation angle
	 */
	private int getRotationAngle() {
		int result = 0;
		if (orientation > 315 || orientation <= 45
				|| OrientationEventListener.ORIENTATION_UNKNOWN == orientation) {
			result = 270;
		} else if (orientation > 45 && orientation <= 135) {
			result = 180;
		} else if (orientation > 135 && orientation <= 225) {
			result = 90;
		} else if (orientation > 225 && orientation <= 315) {
			result = 0;
		}
		return result;
	}
	
	public int getBinocularResource() {
		int angle=getRotationAngle();
		switch (angle) {
		case 0:
			return R.drawable.binocular_0;
		case 90:
			return R.drawable.binocular_90;
		case 180:
			return R.drawable.binocular_180;
		case 270:
			return R.drawable.binocular_270;
		}
		return -100;
	}

	/**
	 * Set orientation.
	 * 
	 * @param orientation
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
		Log.i("camera", "Rotation " + getRotationAngle());
	}

}