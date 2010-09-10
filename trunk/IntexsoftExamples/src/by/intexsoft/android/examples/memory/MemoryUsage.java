package by.intexsoft.android.examples.memory;

import by.intexsoft.android.examples.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class MemoryUsage extends Activity {

	public static long SLEEP_TIME = 25; //animation sleep time
	float needle_direction_angle_increment=1.5f; //animation angle increment

	
	private AnimateArrow arrowanimation;
	private CompassSurfaceView compassView;
	private IconsList imageAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memory);
		ViewGroup viewGroup = (ViewGroup) findViewById(R.id.layoutAnimatedCompass);
		compassView = new CompassSurfaceView(this);
		viewGroup.addView((CompassSurfaceView) compassView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		final Button button = (Button) findViewById(R.id.switch_scheme);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				compassView.switchScheme();
			}
		});

		
		final GridView gridview = (GridView) findViewById(R.id.selectIconView);
		imageAdapter = new IconsList(this.getApplicationContext());
		gridview.setAdapter(imageAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parent, View v, int position, long id)
			{
				imageAdapter.setCheckedPosition(position);
				gridview.setAdapter(imageAdapter);
			};
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (arrowanimation == null) {
			arrowanimation = new AnimateArrow();
			arrowanimation.start();
		}
	}

	@Override
	protected void onPause() {
		if (arrowanimation != null) {
			arrowanimation.finish();
			try {
				arrowanimation.join(MemoryUsage.SLEEP_TIME * 2);
			} catch (InterruptedException e) {
			}
			arrowanimation = null;
		}
		super.onPause();
	}
	
	private class AnimateArrow extends Thread {
		private boolean running = true;
		
		private float needleDirection=0;

		public void finish() {
			this.running = false;
		}

		@Override
		public void run() {
			while (running) {

				//Calculating new needle direction
				needleDirection-=needle_direction_angle_increment;
				compassView.setNeedleDirection(needleDirection);

				//Repainting
				compassView.render();

				//Sleeping
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}