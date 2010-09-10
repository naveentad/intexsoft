package by.intexsoft.android.examples.memory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import by.intexsoft.android.examples.R;

import android.content.Context;

public class SchemeFactory {
	private Context mContext;
	private List<Scheme> schemes = new LinkedList<Scheme>();

	private static SchemeFactory factory = null;

	private SchemeFactory(Context mContext) {
		this.mContext = mContext;
		init();
	}

	private void init() {
		Scheme scheme;

		for (int i = 0; i < 10; i++) {
			scheme= new Scheme(mContext);
			scheme.setNeedleCenterOffset(27);
			scheme.circle_id = R.drawable.cosmozoo_circle2;
			scheme.needle_id = R.drawable.cosmozoo_arrow2;
			scheme.background_id = R.drawable.cosmo_bg_320x480_2;
			schemes.add(scheme);

			scheme = new Scheme(mContext);
			scheme.setNeedleCenterOffset(0);
			scheme.circle_id = R.drawable.romantic_circle;
			scheme.needle_id = R.drawable.romantic_arrow;
			scheme.background_id = R.drawable.romantic_bg;
			schemes.add(scheme);
		}
	}

	public Scheme getNextScheme() {
		Collections.rotate(schemes, 1);
		return schemes.get(1);
	}

	public static SchemeFactory getInstance(Context mContext) {
		if (factory == null) {
			factory = new SchemeFactory(mContext);
		}
		return factory;
	}

}
