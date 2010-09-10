package by.intexsoft.android.examples.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Scheme {
	private Bitmap background, circle, niddle;

	private int needleCenterOffset=0;
	private Context context;
	 
	 protected int background_id,circle_id, needle_id, grouped_icon_addon_id, icon_addon_id,
		group_switcher_id;
	public Scheme(Context mContext) {
		this.context=mContext;
	}

	public Bitmap getBackground() {
		return background;
	}


	public Bitmap getCircle() {
		return circle;
	}


	public Bitmap getNiddle() {
		return niddle;
	}

	public void setNeedleCenterOffset(int needleCenterOffset) {
		this.needleCenterOffset = needleCenterOffset;
	}

	public int getNeedleCenterOffset() {
		return needleCenterOffset;
	}

	private Bitmap getImage(int id) {
		return BitmapFactory.decodeResource(context.getResources(), id);
	}
	
	public void loadResources() {
		if (this.background==null) {
			this.background=getImage(this.background_id);
			this.circle=getImage(this.circle_id);
			this.niddle=getImage(this.needle_id);
		}
	}
	
	public void freeMemory() {
		this.background=null;
		this.circle=null;
		this.niddle=null;
		System.gc();
	}

}
