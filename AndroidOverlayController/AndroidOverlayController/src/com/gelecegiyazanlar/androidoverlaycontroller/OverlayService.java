package com.gelecegiyazanlar.androidoverlaycontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;

public final class OverlayService extends Service {
	
	private static final int LayoutParamFlags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH				
			| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD 
			| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
	
	private LayoutInflater inflater;
	private Display mDisplay;
	private View layoutView; 
	private WindowManager windowManager;
	private WindowManager.LayoutParams params;
	private View.OnTouchListener touchListener;
	private View.OnClickListener clickListener;
	
	private DisplayMetrics calculateDisplayMetrics() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mDisplay.getMetrics(mDisplayMetrics);
		return mDisplayMetrics;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
				LayoutParamFlags,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		mDisplay = windowManager.getDefaultDisplay();
		inflater = LayoutInflater.from(this);
		layoutView = inflater.inflate(R.layout.activity_main, null);
		windowManager.addView(layoutView, params); 
		
		final ImageButton button = (ImageButton) layoutView.findViewById(R.id.toggle);  
		clickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://gelecegiyazanlar.turkcell.com.tr"));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
							 		Intent.FLAG_ACTIVITY_SINGLE_TOP |
									Intent.FLAG_ACTIVITY_CLEAR_TOP);
					OverlayService.this.startActivity(intent);
				} catch (Exception ex) {
				}
			}
		};

		touchListener = new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;
			private long downTime;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downTime = SystemClock.elapsedRealtime();
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP:
					long currentTime = SystemClock.elapsedRealtime();
					if (currentTime - downTime < 200) {
						v.performClick();
					} else {
						updateViewLocation();
					}
					return true;
				case MotionEvent.ACTION_MOVE:
					params.x = initialX + (int) (event.getRawX() - initialTouchX);
					params.y = initialY + (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(layoutView, params);
					return true;
				}
				return false;
			}

			private void updateViewLocation() {
				DisplayMetrics metrics = calculateDisplayMetrics();
				int width = metrics.widthPixels / 2;
				if (params.x >= width)
					params.x = (width * 2) - 10;
				else if (params.x <= width)
					params.x = 10;
				windowManager.updateViewLayout(layoutView, params);
			}
		};
		button.setOnClickListener(clickListener);
		layoutView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent arg1) {
				return false;
			}
		});
		button.setOnTouchListener(touchListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		windowManager.removeView(layoutView);
	}
}