package com.gelecegiyazanlar.androidoverlaycontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public final class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		Intent overlayIntent = new Intent();
		overlayIntent.setClass(this, OverlayService.class);
		this.startService(overlayIntent);
		finish();
	}
}