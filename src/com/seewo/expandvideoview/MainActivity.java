package com.seewo.expandvideoview;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;

public class MainActivity extends Activity {
	private ExpandVideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVideoView = (ExpandVideoView) findViewById(R.id.iVideoView);
		mVideoView.setVideoPath(Environment.getExternalStorageDirectory() + File.separator + "test.mp4");
		mVideoView.play(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
