package com.wos.play.rootdir.model_application.ui.Uitools;

import android.util.SparseArray;
import android.view.View;

/**
 * 万能的viewHolder
 * @author leolaurel.e.l
 */
public class BaseHolder {
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> h = (SparseArray<View>) view.getTag();
		if (h == null) {
			h = new SparseArray<>();
			view.setTag(h);
		}
		View v = h.get(id);
		if (v == null) {
			v = view.findViewById(id);
			h.put(id, v);
		}
		return (T) v;
	}

}
