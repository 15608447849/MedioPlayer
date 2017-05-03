package com.wos.play.rootdir.model_application.ui.ComponentLibrary.weather;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.tool.PinYinUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LEDView {


	private static final String FONT_DIGITAL_7 = "fonts" + File.separator+ "digital-7.ttf";
	private static final int REFRESH_DELAY = 1000;//时间刷新延时秒数
	private static final int TEXT_DELAY = 5 * 1000;//时间刷新延时秒数

	private final Handler mHandler = new Handler();
	private final Runnable mTimeRefresher = new Runnable() {

		@Override
		public void run() {
			exeing();
			mHandler.postDelayed(this, REFRESH_DELAY);
		}
	};
	private final Runnable mStringRefresher = new Runnable() {

		@Override
		public void run() {
			texttans();
			mHandler.postDelayed(this, TEXT_DELAY);
		}
	};



//	private TextView timeView;
//	private TextView houseView;
//	private TextView weekView;
	private ImageView image;
	private AutoTextView mTextView02;
	public LEDView(Context context, ViewGroup vp) {
		init(context,vp);
		initTime();
	}
	private ViewGroup layout;
	View view = null;
	SimpleDateFormat df1 = null;
	SimpleDateFormat df2 = null;
	private void initTime() {
		df1 = new SimpleDateFormat("yyyy年MM月dd日");//年月日
		df2 = new SimpleDateFormat("当前时间:HH时mm分");//时分秒
	}
	private void init(Context context,ViewGroup vp) {
		layout = vp;
		view = LayoutInflater.from(context).inflate(R.layout.ledview, null);
//		timeView = (TextView) view.findViewById(R.id.ledview_clock_ydt);
//		houseView = (TextView) view.findViewById(R.id.ledview_clock_hms);
//		weekView = (TextView)view.findViewById(R.id.ledview_clock_week);
		AssetManager assets = context.getAssets();
		Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
//		timeView.setTypeface(font);// 设置字体
//		houseView.setTypeface(font);// 设置字体
		image = (ImageView)view.findViewById(R.id.show_icon);
		mTextView02 = (AutoTextView) view.findViewById(R.id.switcher02);
		layout.addView(view);
	}

	public void startTime() {
//		timeView.setText(getYesr());
//		weekView.setText(getWeekOfDate(new Date()));
//		mHandler.post(mTimeRefresher);
	}
	public void startText(){
		mHandler.post(mStringRefresher);
	}

	public void stop() {
		layout.removeView(view);
		view = null;
		layout = null;
		strings.clear();
		strings = null;
		mHandler.removeCallbacks(mTimeRefresher);
		mHandler.removeCallbacks(mStringRefresher);

	}
	//计算时间
	private void exeing(){
		//houseView.setText(getHouse());
	}
	//获取当前年月日
	private String getYesr() {
		return df1.format(new Date());
	}
	//获取当前时分秒
	private String getHouse() {
		return df2.format(new Date());
	}
	//星期
	private final String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	//获取星期
	private String getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}
	//内容列表
	private List<String> strings = null;
	/**
	 *
	 * @param val 城市,类型,温度,风力
	 */
	public void setValue(String[] val){
//		System.out.println("- - - - - - - - setValue() - - - - - - ");
		if (strings==null){
			strings = new ArrayList<>();
		}else{
			strings.clear();
		}

		for (int index = 0;index < val.length ;index++){
			strings.add(val[index]);
		}

		//根据类型 -> 获取bitmap
		image.setImageBitmap(tanslateTypeToBitmap(val[1]));
	}

	private int currentIndex = 0;
	private void texttans() {
		if (strings==null) strings = new ArrayList<>();

		if (currentIndex >= strings.size()){
			if (currentIndex==strings.size()){
				mTextView02.setText(getYesr());
				currentIndex++;
			}else{
				if (currentIndex == (strings.size()+1)){
					mTextView02.setText(getWeekOfDate(new Date()));
					currentIndex++;
				}else{
					if (currentIndex == (strings.size()+2)){
						mTextView02.setText(getHouse());
						currentIndex=0;
					}
				}

			}

		}else{
			mTextView02.setText(strings.get(currentIndex));
			currentIndex++;
		}


	}


	//根据类型 得到 bitmap
	private Bitmap tanslateTypeToBitmap(String type) {
		String path = null;
		// 1. 获取 类型的拼音
		path = PinYinUtils.getPingYin(type);
		// 2. 判断早晚
		path = UiTools.getDateSx()+path;
		// 去对应文件夹 获取 bitmap
		path = UiTools.getWeatherIconPath()+path+".png";
//		System.out.println("bitmap path -:"+path);
		if (UiTools.fileIsExt(path)){
//			System.out.println("bitmap 存在 -");
			return ImageUtils.getBitmap(path);
		}
//		System.out.println("bitmap 不存在 -");
		return null;
	}




}
