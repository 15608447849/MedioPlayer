package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lzp.yw.com.medioplayer.R;

public class LEDView {


	private static final String FONT_DIGITAL_7 = "fonts" + File.separator+ "digital-7.ttf";
	private static final int REFRESH_DELAY = 500;//刷新延时秒数
	private final Handler mHandler = new Handler();
	private final Runnable mTimeRefresher = new Runnable() {

		@Override
		public void run() {
			exeing();
			mHandler.postDelayed(this, REFRESH_DELAY);
		}
	};


	private TextView timeView;
	private TextView houseView;
	private TextView weekView;
	public LEDView(Context context, ViewGroup vp) {
		init(context,vp);
		initTime();
	}

	SimpleDateFormat df1 = null;
	SimpleDateFormat df2 = null;
	private void initTime() {
		df1 = new SimpleDateFormat("yyyy-MM-dd");//年月日
		df2 = new SimpleDateFormat("HH:mm:ss");//时分秒
	}
	private void init(Context context,ViewGroup layout) {
		View view = LayoutInflater.from(context).inflate(R.layout.ledview, null);
		timeView = (TextView) view.findViewById(R.id.ledview_clock_ydt);
		houseView = (TextView) view.findViewById(R.id.ledview_clock_hms);
		weekView = (TextView)view.findViewById(R.id.ledview_clock_week);
		AssetManager assets = context.getAssets();
		Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
		timeView.setTypeface(font);// 设置字体
		houseView.setTypeface(font);// 设置字体
		layout.addView(view);
	}

	public void start() {
		timeView.setText(getYesr());
		weekView.setText(getWeekOfDate(new Date()));
		mHandler.post(mTimeRefresher);
	}
	public void stop() {
		mHandler.removeCallbacks(mTimeRefresher);
	}
	private void exeing(){
		houseView.setText(getHouse());
	}
	//获取当前年月日
	private String getYesr() {
		return df1.format(new Date());
	}
	//获取当前时分秒
	private String getHouse() {
		return df2.format(new Date());
	}
	private final String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private String getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}
}
