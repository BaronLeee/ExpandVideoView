package com.seewo.expandvideoview;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private ExpandVideoView mVideoView;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVideoView = (ExpandVideoView) findViewById(R.id.iVideoView);
		Uri uri = Uri.parse("content://media/external/video/media/1908");
		// String VideoPath = Environment.getExternalStorageDirectory() + File.separator + "test.mp4";
		String VideoPath = getVideoPathFromUri(uri);
		Log.v(TAG, "path is " + VideoPath);
		mVideoView.setVideoPath(VideoPath);

		mVideoView.play(0);
	}

	private String getVideoPathFromUri(Uri uri) {
		String res = null;
		String[] proj = { MediaStore.Video.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null) {
			mVideoView.restoreState();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null)
			mVideoView.storeState();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mVideoView != null) {
			mVideoView.destroy();
		}
	}

}
