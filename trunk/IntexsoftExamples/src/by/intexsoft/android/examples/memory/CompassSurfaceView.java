package by.intexsoft.android.examples.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.*;

public class CompassSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	SurfaceHolder mHolder;
	boolean ready = false;
	
	
	private Paint mPaint = new Paint();
	private Scheme scheme = null;
	private final int WIDTH = 320;
	protected int middleX = WIDTH / 2;
	protected int middleY = 215;
	private float screenScale;
	private Bitmap current_display_bitmap,new_display_bitmap;

	private int screenHeight;
	private int screenWidth;
	private SchemeFactory schemeFactory;
	private float needleDirection = 0;


	public SurfaceHolder getMHolder() {
		return mHolder;
	}

	public CompassSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public CompassSurfaceView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		screenScale = context.getResources().getDisplayMetrics().density;
		schemeFactory = SchemeFactory.getInstance(context);
		// Initializing default scheme
		scheme = SchemeFactory.getInstance(context).getNextScheme();
		scheme.loadResources();

		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		screenWidth = width;
		screenHeight = height;
		middleX = width / 2;
		middleY = height / 2;
		current_display_bitmap = Bitmap.createScaledBitmap(scheme
				.getBackground(), screenWidth, screenHeight, true);
		
		render();
	}
	
	
	public void setNeedleDirection(float needleDirection) {
		this.needleDirection=needleDirection;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		ready = true;

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		ready = false;
	}

	/**
	 * This method should be invoked from a single thread
	 */
	public void render() {
		if (ready) {
			Canvas c = null;
			SurfaceHolder holder = this.getMHolder();
			if (holder != null) {
				try {
					c = holder.lockCanvas(null);
					synchronized (holder) {
						if (c != null) {
							drawCompass(c);
						}
					}
				} finally {
					if (c != null) {
						holder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	private void drawCompass(Canvas canvas) {
		canvas.save();

		if (current_display_bitmap != null) {
			canvas.drawBitmap(current_display_bitmap, 0, 0, null);
		}

		canvas.translate(middleX, middleY);
		//Drawing circle
		float halfWidth = scheme.getCircle().getWidth() / 2;
		float halfHeight = scheme.getCircle().getHeight() / 2;
		canvas.drawBitmap(scheme.getCircle(), -halfWidth, -halfHeight, null);

		// Drawing the needle
		canvas.rotate(-needleDirection);
		halfWidth = scheme.getNiddle().getWidth() / 2;
		halfHeight = scheme.getNiddle().getHeight() / 2;
		float offset = scheme.getNeedleCenterOffset() * screenScale;
		canvas.drawBitmap(scheme.getNiddle(), -halfWidth, -halfHeight - offset,
				mPaint);
		canvas.rotate(needleDirection);
		canvas.restore();

	}

	
	public void switchScheme() {
		Scheme new_scheme = schemeFactory.getNextScheme();
		if (new_scheme != null && new_scheme != scheme) {
			//Loading resources
			new_scheme.loadResources();
			
			new_display_bitmap = Bitmap.createScaledBitmap(new_scheme
					.getBackground(), screenWidth, screenHeight, true);

			Scheme old_scheme = scheme;
			scheme = new_scheme;
			current_display_bitmap=new_display_bitmap;
			//Freeing the memory occupied by old scheme
			old_scheme.freeMemory();
		}
	}
	

}
