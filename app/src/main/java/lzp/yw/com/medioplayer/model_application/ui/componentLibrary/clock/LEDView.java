package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.clock;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lzp.yw.com.medioplayer.R;

public class LEDView extends LinearLayout {


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
	public LEDView(Context context) {
		super(context);
		initTime();
		init(context);
	}

	SimpleDateFormat df1 = null;
	SimpleDateFormat df2 = null;
	private void initTime() {
		df1 = new SimpleDateFormat("yyyy-MM-dd");//年月日
		df2 = new SimpleDateFormat("HH:mm:ss");//时分秒
	}
	private void init(Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.ledview, this);
		timeView = (TextView) view.findViewById(R.id.ledview_clock_ydt);
		houseView = (TextView) view.findViewById(R.id.ledview_clock_hms);
		AssetManager assets = context.getAssets();
		final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
		timeView.setTypeface(font);// 设置字体
		houseView.setTypeface(font);// 设置字体
	}

	public void start() {
		timeView.setText(getYesr());
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
	public String getHouse() {
		return df2.format(new Date());
	}
}
