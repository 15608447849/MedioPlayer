package com.wos.play.rootdir.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import static com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils.computeSampleSize;

/****
 * 这里你要明白几个方法执行的流程： 首先ImageView是继承自View的子类.
 *
 * onLayout方法：是一个回调方法.该方法会在在View中的layout方法中执行，在执行layout方法前面会首先执行setFrame方法.
 * layout方法：
 * setFrame方法：判断我们的View是否发生变化，如果发生变化，那么将最新的l，t，r，b传递给View，然后刷新进行动态更新UI并且返回ture.没有变化返回false.
 * invalidate方法：用于刷新当前控件,
 */
public class DragImageView extends MeImageView {


	private int screen_W, screen_H;// 可见屏幕的宽高度-控件宽高

	private int bitmap_W, bitmap_H;// 当前图片宽高

	private int MAX_W, MAX_H, MIN_W, MIN_H;// 极限值 - 最大最小

	private int current_Top, current_Right, current_Bottom, current_Left;// 当前图片上下左右坐标

	private int start_Top = -1, start_Right = -1, start_Bottom = -1,start_Left = -1;// 初始化默认位置.

	private int start_touch_x, start_touch_y, current_touch_x, current_touch_y;// 触摸位置

	private float before_touch_point_len, after_touch_point_len;// 两触点距离

	private float scale_scale;// 缩放比例

	/**
	 * 模式
	 * NONE无
	 * DRAG拖拽
	 * ZOOM缩放
	 */
	private enum MODE {
		NONE, DRAG, ZOOM
	}
	private MODE mode = MODE.NONE;// 默认模式

	private boolean isControl_V = false;// 垂直监控
	private boolean isControl_H = false;// 水平监控
	private Handler handler = new Handler();

	/** 构造方法 **/
	public DragImageView(Context context) {
		super(context);

	}
	public DragImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/** 可见宽度 **/
	public void setScreen_W(int screen_W) {
		this.screen_W = screen_W;
	}

	/** 可见高度 **/
	public void setScreen_H(int screen_H) {
		this.screen_H = screen_H;
	}

	/***
	 * 设置显示图片
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bm!=null){

			/** 获取图片宽高 **/
			bitmap_W = bm.getWidth();
			bitmap_H = bm.getHeight();
			Log.i(TAG,"图片大小:"+bitmap_W+" "+bitmap_H);
			MAX_W = bitmap_W * 4;
			MAX_H = bitmap_H * 4;
			MIN_W = bitmap_W / 4;
			MIN_H = bitmap_H / 4;
			bitmap = bm;
		}

		super.setImageBitmap(bm);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (bitmap!=null){
//			canvas.drawBitmap(bitmap,0,0,null);
//		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.screen_H = this.getMeasuredHeight();
		this.screen_W = this.getMeasuredWidth();
		Log.i(TAG,"onMeasure size : ("+screen_W+"-"+screen_H+")");


	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (start_Top == -1) {
			start_Top = top;
			start_Left = left;
			start_Bottom = bottom;
			start_Right = right;
		}
	}


	/***
	 * touch 事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/** 处理单点、多点触摸 **/
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			onTouchDown(event);
			break;
		// 多点触摸
		case MotionEvent.ACTION_POINTER_DOWN:
			onPointerDown(event);
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			mode = MODE.NONE;
			break;

		// 多点松开
		case MotionEvent.ACTION_POINTER_UP:
			mode = MODE.NONE;
			break;
		}

		return true;
	}

	/** 按下 **/
	void onTouchDown(MotionEvent event) {
		mode = MODE.DRAG;

		current_touch_x = (int) event.getRawX();
		current_touch_y = (int) event.getRawY();

		start_touch_x = (int) event.getX();
		start_touch_y = current_touch_y - this.getTop();
	}

	/** 两个手指 只能放大缩小 **/
	void onPointerDown(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			mode = MODE.ZOOM;
			before_touch_point_len = getDistance(event);// 获取两点的距离
		}
	}


	/** 移动的处理 **/
	void onTouchMove(MotionEvent event) {
		int left = 0, top = 0, right = 0, bottom = 0;
		/** 处理拖动 **/
		if (mode == MODE.DRAG) {

			/** 在这里要进行判断处理，防止在drag时候越界 **/

			/** 获取相应的l，t,r ,b **/
			left = current_touch_x - start_touch_x;
			right = current_touch_x + this.getWidth() - start_touch_x;
			top = current_touch_y - start_touch_y;
			bottom = current_touch_y - start_touch_y + this.getHeight();

			/** 水平进行判断 **/
			if (isControl_H) {
				if (left >= 0) {
					left = 0;
					right = this.getWidth();
				}
				if (right <= screen_W) {
					left = screen_W - this.getWidth();
					right = screen_W;
				}
			} else {
				left = this.getLeft();
				right = this.getRight();
			}
			/** 垂直判断 **/
			if (isControl_V) {
				if (top >= 0) {
					top = 0;
					bottom = this.getHeight();
				}

				if (bottom <= screen_H) {
					top = screen_H - this.getHeight();
					bottom = screen_H;
				}
			} else {
				top = this.getTop();
				bottom = this.getBottom();
			}
			if (isControl_H || isControl_V)
				this.setPosition(left, top, right, bottom);

			current_touch_x = (int) event.getRawX();
			current_touch_y = (int) event.getRawY();

		}
		/** 处理缩放 **/
		else if (mode == MODE.ZOOM) {
			after_touch_point_len = getDistance(event);// 获取两点的距离
			Log.e(TAG,"缩放长度:"+after_touch_point_len);
			float gapLenght = after_touch_point_len - before_touch_point_len;// 变化的长度
			Log.e(TAG,"缩放长度变化:"+gapLenght);
			if (Math.abs(gapLenght) > 5f) {
				scale_scale = after_touch_point_len / before_touch_point_len;// 求的缩放的比例
				Log.e(TAG,"缩放比例:"+scale_scale);
				setScale(scale_scale);
				before_touch_point_len = after_touch_point_len;
			}
		}

	}

	/** 获取两点的距离 **/
	private float getDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return (float) Math.sqrt(x * x + y * y);
	}

	/** 实现处理拖动 **/
	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

	/** 处理缩放 **/
	void setScale(float scale) {
		int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;// 获取缩放水平距离
		int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;// 获取缩放垂直距离

		// 放大
		if (scale > 1 && screen_W <= MAX_W && screen_H <= MAX_W) {
			current_Left = this.getLeft() - disX;
			current_Top = this.getTop() - disY;
			current_Right = this.getRight() + disX;
			current_Bottom = this.getBottom() + disY;

			if (current_Top <= 0 && current_Bottom >= screen_H) {
				isControl_V = true;// 开启垂直监控
			} else {
				isControl_V = false;
			}
			if (current_Left <= 0 && current_Right >= screen_W) {
				isControl_H = true;// 开启水平监控
			} else {
				isControl_H = false;
			}
			setFrame(current_Left, current_Top, current_Right, current_Bottom);
		}
		// 缩小
		else if (scale < 1 && screen_W >= MIN_W && screen_H >= MIN_H) { //this.getWidth()
			current_Left = this.getLeft() + disX;
			current_Top = this.getTop() + disY;
			current_Right = this.getRight() - disX;
			current_Bottom = this.getBottom() - disY;

			// 上边越界
			if (isControl_V && current_Top > 0) {
				current_Top = 0;
				current_Bottom = this.getBottom() - 2 * disY;
				if (current_Bottom < screen_H) {
					current_Bottom = screen_H;
					isControl_V = false;// 关闭垂直监听
				}
			}
			// 下边越界
			if (isControl_V && current_Bottom < screen_H) {
				current_Bottom = screen_H;
				current_Top = this.getTop() + 2 * disY;
				if (current_Top > 0) {
					current_Top = 0;
					isControl_V = false;// 关闭垂直监听
				}
			}

			// 左边越界
			if (isControl_H && current_Left >= 0) {
				current_Left = 0;
				current_Right = this.getRight() - 2 * disX;
				if (current_Right <= screen_W) {
					current_Right = screen_W;
					isControl_H = false;// 关闭
				}
			}
			// 右边越界
			if (isControl_H && current_Right <= screen_W) {
				current_Right = screen_W;
				current_Left = this.getLeft() + 2 * disX;
				if (current_Left >= 0) {
					current_Left = 0;
					isControl_H = false;// 关闭
				}
			}
//
//			if (isControl_H || isControl_V) {
//				this.setFrame(current_Left, current_Top, current_Right,
//						current_Bottom);
//			} else {
//				this.setFrame(current_Left, current_Top, current_Right,
//						current_Bottom);
//			}
			/***
			 * 在这里要进行缩放处理
			 */
			setFrame(current_Left, current_Top, current_Right, current_Bottom);
		}
	}

	private Bitmap bitmap;
	public void clearBitmap(){
		if (bitmap!=null){
			bitmap.isRecycled();
			bitmap = null;
			setImageBitmap(null);
			System.gc();
		}
	}
	@Override
	public void setImagePath(final String path) {
		if (screen_H==0 || screen_W==0){
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {

					setImagePath(path);
				}
			},100);
		} else{
			clearBitmap();
			//获取图片大小
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path,opts);
				Log.e(TAG,"bitmap 大小:"+opts.outWidth+" "+opts.outHeight);
				int bw = opts.outWidth;
				int bh = opts.outHeight;
				int scale = computeSampleSize(opts, -1, screen_W*screen_H); //计算采样率
				Log.e(TAG,"bitmap 采样率计算结果:"+scale);
				opts.inSampleSize = scale/4;
				opts.inJustDecodeBounds = false;
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;//Bitmap.Config，默认为ARGB_8888
				opts.inPurgeable = true;//当存储Pixel的内存空间在系统内存不足时是否可以被回收
				opts.inInputShareable = true;//inPurgeable为true情况下才生效，是否可以共享一个InputStream
				opts.inDither = false;//是否抖动，默认为false
				opts.inTempStorage = new byte[12 * 1024]; //解码时的临时空间，建议16*1024
				int displayDpi = getResources().getDisplayMetrics().densityDpi;//像素密度
				Log.e(TAG,"像素密度:"+displayDpi);
				opts.inScreenDensity = displayDpi;
				opts.inScaled = true;
				opts.inPreferQualityOverSpeed = false;
//				Rect rect = new Rect();
//				rect.set(0,0,bw,bh);
//				rect = new Rect(bw/2 - screen_W, bh/2 - screen_H, bw/2 +screen_W+1, bh/2+screen_H+1);
//				Log.e(TAG,"矩形大小:"+rect.toString());
//				BitmapRegionDecoder bitmapRegionDecoder =
//                        BitmapRegionDecoder.newInstance(path, false);
//				bitmap = bitmapRegionDecoder.decodeRegion(rect, opts);

				setImageBitmap(BitmapFactory.decodeFile(path,opts));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
