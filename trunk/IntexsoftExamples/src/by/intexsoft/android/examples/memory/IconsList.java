package by.intexsoft.android.examples.memory;

import by.intexsoft.android.examples.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class IconsList extends BaseAdapter {
	// adapter context
	private Context context;

	private int selected_position=0; //first item is selected by default
	private int[] icons = new int[] { R.drawable.icon_apple_big,
			R.drawable.icon_coliseum_big, R.drawable.icon_intexsoft_big ,R.drawable.icon_bird_big};

	/**
	 * Constructor.
	 */
	public IconsList(Context context) {
		this.context = context;
	}

	/**
	 * Method return count of displayed types.
	 */
	@Override
	public int getCount() {
		return this.icons.length;
	}

	/**
	 * Method return selected type.
	 */
	@Override
	public Object getItem(int position) {
		// We will return id of image resource as an object, since we do not
		// need to process the selection in this example
		return icons[position];
	}

	/**
	 * Method return ID of a selected item.
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setCheckedPosition(int position) {
		selected_position=position;
	}


	@Override
	public int getItemViewType(int position) {
		if (selected_position==position) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return prepareViews(position, convertView);
	}
	
	private View prepareViews(int position,	View convertView) {

		if (convertView == null) {
			if (selected_position==position) {
				convertView = new FrameLayout(context);
				((FrameLayout) convertView).addView(new ImageView(context));
				((FrameLayout) convertView).addView(new ImageView(context));
			} else {
				convertView = new ImageView(context);
			}
		}

		if (selected_position==position) {
			FrameLayout frm = ((FrameLayout) convertView);
			// Adding thumbnail
			ImageView iview = (ImageView) frm.getChildAt(0);
			iview.setImageResource(icons[position]);
			// Adding marker
			iview = (ImageView) frm.getChildAt(1);
			iview.setImageResource(R.drawable.target_marker);

		} else {
			((ImageView) convertView).setImageResource(icons[position]);
		}
		return convertView;
	}


}
