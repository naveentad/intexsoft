package by.intexsoft.android.examples.camera;

import java.io.IOException;
import java.util.List;

import by.intexsoft.android.examples.R;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	private Camera camera;

	private SurfaceView surfaceView;

	private boolean previewRunning = false;

	public static final int CAPTURE_DESIRED_WIDTH = 480;
	public static final int CAPTURE_DESIRED_HEIGHT = 320;

	SurfaceHolder mHolder;

	private ImageCaptureCallback jpegCallBack;
	private ImageButton photoButton;

	private int binocularResource = -100;
	private ImageView mask;
	private OrientationEventListener oe;

	public void onCreate(Bundle icicle) {
		// Removing title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(icicle);
		// Going full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera_view);

		surfaceView = (SurfaceView) findViewById(R.id.camera_surface);

		jpegCallBack= new ImageCaptureCallback(
					CameraActivity.this);
		
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mask = (ImageView) findViewById(R.id.binocular);
		mask.setScaleType(ImageView.ScaleType.FIT_XY);

		photoButton = (ImageButton) findViewById(R.id.camera_take_picture_button);
		photoButton.setEnabled(false);
		photoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				oe.disable();
				photoButton.setEnabled(false);
				takePicture();
			}
		});

		oe = new OrientationEventListener(this.getApplicationContext()) {

			@Override
			public void onOrientationChanged(int orientation) {
				jpegCallBack.setOrientation(orientation);
				int binocular_res = jpegCallBack.getBinocularResource();
				if (binocular_res != binocularResource) {
					binocularResource = binocular_res;
					mask.setImageResource(binocular_res);
				}
				photoButton.setEnabled(true);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		oe.enable();
	}

	@Override
	protected void onPause() {
		oe.disable();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		jpegCallBack=null;
		super.onDestroy();
	}

	private synchronized void takePicture() {
		if (previewRunning) {
			Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
				public void onShutter() {
				}
			};
			Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
				public void onPictureTaken(byte[] data, Camera c) {
				}
			};
			if (camera == null) {
				Toast.makeText(this.getApplicationContext(),
						R.string.camera_error, Toast.LENGTH_LONG).show();
			} else {
				camera.takePicture(shutterCallback, pictureCallback,
						jpegCallBack);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (camera == null) {
			Toast.makeText(this.getApplicationContext(), R.string.camera_error,
					Toast.LENGTH_LONG).show();
			return;
		}
		Camera.Parameters params = camera.getParameters();

		List<Size> supportedPictureSizes = params.getSupportedPictureSizes();
		Size currentPictureSize = params.getPictureSize();
		if (supportedPictureSizes != null) {
			for (Size size : supportedPictureSizes) {
				if (size.height >= CAPTURE_DESIRED_WIDTH
						&& size.height < currentPictureSize.height
						&& size.width >= CAPTURE_DESIRED_WIDTH
						&& size.width < currentPictureSize.width) {
					currentPictureSize = size;
				}
			}
		}
		params.setPictureSize(currentPictureSize.width,
				currentPictureSize.height);
		Log.i("Camera", "picture size " + currentPictureSize.width + ","
				+ currentPictureSize.height);

		Size currentPreviewSize = params.getPreviewSize();
		List<Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
		float currentDelta = Math.abs((float) w - currentPreviewSize.width);
		if (supportedPreviewSizes != null) {
			for (Size size : supportedPreviewSizes) {
				float delta = Math.abs((float) w - size.width);
				if (delta < currentDelta) {
					currentDelta = delta;
					currentPreviewSize = size;
				}
			}
		}
		params.setPreviewSize(currentPreviewSize.width,
				currentPreviewSize.height);
		Log.i("Camera", "preview size " + currentPreviewSize.width + ","
				+ currentPreviewSize.height);
		camera.setParameters(params);
		camera.startPreview();
		previewRunning = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		previewRunning = false;
		try {
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				camera.release();
				camera = null;
			}
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			camera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			previewRunning = false;
			camera.release();
			camera = null;
		}
	}

}