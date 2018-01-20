package com.baron.expandvideoview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.baron.common.ExpandVideoViewUtil;
import com.baron.expandvideoview.R;

/**
 * @file ExpandVideoView.java
 * @author Lixiang
 * @brief
 * @date 2015-1-6 create
 * 
 */
public class ExpandVideoView extends LinearLayout implements OnClickListener, IUpdateListener {
	private static final String TAG = "ExpandVideoView";
	private static final int UPDATE_INTERVAL = 500;
	private static final int CHANGED_STEP = 3000;

	private static final String DEFAULT_SCREEN_SHOT_DEST_PATH =
			Environment.getExternalStorageDirectory() + File.separator + "ExpandVideoView" + File.separator + "ScreenShot" + File.separator;

	private static final int SWITCH_PLAY_STATE = 1;
	private static final int BACK = 2;
	private static final int FORWARD = 3;
	private static final int SCREEN_SHOT = 4;
	private static final int DEFAULT_ACTION = -1;

	private static final int SCREEN_BTN_ENABLE = -2;
	private static final int SCREEN_BTN_DISABLED = -3;

	private static final int SWITCH_TO_PLAY = -4;
	private static final int SWITCH_TO_PAUSE = -5;

	private static final int SCREEN_SHOT_SUCCESS = -1;
	private static final int UPDATE_CUR_TIME = 0;

	private Context mContext;;
	private TextView mCurrentTimeView;
	private TextView mTotalTimeView;
	private SeekBar mPlayProgress;
	private Button mBackBtn;
	private Button mSwitchPlayStateBtn;
	private Button mForwardBtn;
	private Button mScreenShotBtn;
	private MediaMetadataRetriever mMediaMetadataRetriever;
	private VideoView mVideoView;
	private UpdatePlayProgress mUpdateProgress;
	private static Bundle mStateBundle;

	private String mVideoPath;
	private boolean isPlaying = false;
	private int mVideoTotalLength;
	private int mCurrentPosition;
	private String mScreenShotDesPath = DEFAULT_SCREEN_SHOT_DEST_PATH;
	private boolean isRunning = true;

	private int mAction = DEFAULT_ACTION;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SCREEN_SHOT_SUCCESS) {
				Toast.makeText(mContext, R.string.ev_save_screen_image_successful, Toast.LENGTH_SHORT).show();

			} else if (msg.what == SCREEN_BTN_ENABLE) {
				mScreenShotBtn.setEnabled(true);
			} else if (msg.what == SCREEN_BTN_DISABLED) {
				mScreenShotBtn.setEnabled(false);
			} else if (msg.what == SWITCH_TO_PLAY) {
				mSwitchPlayStateBtn.setText(R.string.ev_video_play);
			} else if (msg.what == SWITCH_TO_PAUSE) {
				mSwitchPlayStateBtn.setText(R.string.ev_video_pause);
			}
			else if (msg.what == UPDATE_CUR_TIME) {
				mCurrentTimeView.setText(ExpandVideoViewUtil.getStandardTime(mCurrentPosition / 1000));
			}
		}

	};

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			if (mVideoView != null) {
				mVideoView.seekTo(progress);
				if (!isPlaying) {
					mHandler.sendEmptyMessage(SCREEN_BTN_ENABLE);
				}

			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		}
	};
	MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.v(TAG, "play end");
			mVideoView.pause();
			isPlaying = false;
			mHandler.sendEmptyMessage(SWITCH_TO_PLAY);
			mCurrentPosition = 0;
			mHandler.sendEmptyMessage(SCREEN_BTN_ENABLE);
		}
	};

	public ExpandVideoView(Context context) {
		this(context, null);
	}

	public ExpandVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.videoview, this, true);
		mContext = context;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.v(TAG, "---->onDetachedFtromWindow");
	}

	@Override
	protected void onFinishInflate() {
		Log.v(TAG, "---->onFinishInflate");
		initView();
		initData();
	}

	public void initView() {
		mCurrentTimeView = (TextView) findViewById(R.id.tv_current_play_time);
		mTotalTimeView = (TextView) findViewById(R.id.tv_total_pley_time);
		mPlayProgress = (SeekBar) findViewById(R.id.play_seek_bar);
		mBackBtn = (Button) findViewById(R.id.btn_back);
		mSwitchPlayStateBtn = (Button) findViewById(R.id.btn_switch);
		mForwardBtn = (Button) findViewById(R.id.btn_forward);
		mScreenShotBtn = (Button) findViewById(R.id.btn_shot_screen);
		mVideoView = (VideoView) findViewById(R.id.video_view);
	}

	public void initData() {
		mMediaMetadataRetriever = new MediaMetadataRetriever();
		mStateBundle = new Bundle();
		initListener();
		notifySwitchBtnToChange();

		new Thread(new ActionListener()).start();
	}

	public void initListener() {
		mBackBtn.setOnClickListener(this);
		mForwardBtn.setOnClickListener(this);
		mSwitchPlayStateBtn.setOnClickListener(this);
		mScreenShotBtn.setOnClickListener(this);
		mPlayProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
		mVideoView.setOnCompletionListener(mCompletionListener);
	}

	private void notifySwitchBtnToChange() {
		if (isPlaying) {
			mHandler.sendEmptyMessage(SWITCH_TO_PAUSE);
		} else {
			mHandler.sendEmptyMessage(SWITCH_TO_PLAY);
		}
	}

	public void setVideoPath(String path) {
		Log.v(TAG, "set path to:" + path);
		if (path != null) {
			mVideoPath = path;
			mVideoTotalLength = getVideoTimeLegth(mVideoPath);
			String duration = ExpandVideoViewUtil.getStandardTime(mVideoTotalLength);
			mPlayProgress.setMax(mVideoTotalLength * 1000);
			Log.v(TAG, "video length：" + duration);
			mTotalTimeView.setText(duration);

		} else {
			Log.e(TAG, "Invalid path");
		}
	}

	// 返回的时长的单位：秒
	public int getVideoTimeLegth(String path) {
		if (null == mMediaMetadataRetriever) {
			mMediaMetadataRetriever = new MediaMetadataRetriever();
		}
		mMediaMetadataRetriever.setDataSource(path);
		int len =
				Integer.valueOf(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		return len / 1000;
	}

	/**
	 * 开始进行播放
	 * 
	 * @param playPoint 播放开始的时间点，单位：秒
	 */
	public void play(int playPoint) {
		if (mVideoPath == null || !new File(mVideoPath).exists()) {
			Log.e(TAG, "there is no valid video source");
			return;
		}
		mCurrentPosition = playPoint * 1000;
		Log.v(TAG, "the video path:" + mVideoPath);
		mVideoView.setVideoPath(mVideoPath);
		Log.v(TAG, "start play");
		mVideoView.start();

		mVideoView.seekTo(mCurrentPosition);
		isPlaying = true;
		notifySwitchBtnToChange();
		mUpdateProgress = new UpdatePlayProgress();
		mUpdateProgress.setmUpdateListener(this);
		new Thread(mUpdateProgress).start();
	}

	public void switchPlayState() {
		if (isPlaying) {
			mVideoView.pause();
			isPlaying = false;
		} else {
			mVideoView.start();
			isPlaying = true;
		}
	}

	/**
	 * 设置截图的存储路径，如果没有设置，默认保存在 /storage/sdcard0/ExpandVideoView/ScreenShot/目录下
	 * 
	 * @param desPath 目标存储路径
	 */
	public void setScreenShotSavePath(String desPath) {
		mScreenShotDesPath = desPath;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_switch) {
			mAction = SWITCH_PLAY_STATE;
		} else if (v.getId() == R.id.btn_back) {
			mAction = BACK;
		} else if (v.getId() == R.id.btn_forward) {
			mAction = FORWARD;
		} else if (v.getId() == R.id.btn_shot_screen) {
			mAction = SCREEN_SHOT;
		}

	}

	private class ActionListener implements Runnable {

		@Override
		public void run() {
			Log.v(TAG, "ActionListener Thread start");
			while (isRunning) {

				if (mAction == SWITCH_PLAY_STATE) {
					Log.v(TAG, "click Switch button");
					switchPlayOrPause();
					mAction = DEFAULT_ACTION;
				} else if (mAction == BACK) {
					Log.v(TAG, "click Back button");
					quickPlay(BACK);
					mAction = DEFAULT_ACTION;
				} else if (mAction == FORWARD) {
					Log.v(TAG, "click Forward button");
					quickPlay(FORWARD);
					mAction = DEFAULT_ACTION;
				} else if (mAction == SCREEN_SHOT) {
					screenShot();
					mAction = DEFAULT_ACTION;
				}
			}
			Log.v(TAG, "ActionListener Thread stop");
		}

	}

	private void switchPlayOrPause() {
		if (isPlaying) {
			mVideoView.pause();
			mHandler.sendEmptyMessage(SCREEN_BTN_ENABLE);
			isPlaying = false;
		} else {
			mVideoView.start();
			mHandler.sendEmptyMessage(SCREEN_BTN_DISABLED);
			isPlaying = true;
		}
		notifySwitchBtnToChange();
	}

	private void quickPlay(int flag) {
		int next = 0;
		switch (flag) {
		case BACK:
			next = mCurrentPosition - CHANGED_STEP;
			if (next < 0) {
				next = 0;
			}

			break;
		case FORWARD:
			next = mCurrentPosition + CHANGED_STEP;
			if (next > mVideoTotalLength * 1000) {
				return;
			}
			break;
		default:
			break;
		}
		if (!isPlaying) {
			mHandler.sendEmptyMessage(SCREEN_BTN_ENABLE);
		}
		mVideoView.seekTo(next);
		mPlayProgress.setProgress(next);
		mCurrentPosition = next;
		mHandler.sendEmptyMessage(UPDATE_CUR_TIME);
	}

	public void screenShot() {
		mHandler.sendEmptyMessage(SCREEN_BTN_DISABLED);
		if (null != mVideoPath) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					final Bitmap bitmap =
							mMediaMetadataRetriever.getFrameAtTime(mVideoView.getCurrentPosition() * 1000,
									MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
					saveShotImage(bitmap);

				}
			}).start();

		} else {
			Log.e(TAG, "mCurrentPath is null");
		}
	}

	private void saveShotImage(Bitmap pBitmap) {
		Log.v(TAG, "bitmap.size():" + pBitmap.getByteCount() / 1024);
		BufferedOutputStream lBos = null;
		try {
			lBos = new BufferedOutputStream(new FileOutputStream(getShotImagePath()));
			if (!pBitmap.isRecycled())
				pBitmap.compress(Bitmap.CompressFormat.PNG, 100, lBos);
			lBos.flush();
			mHandler.sendEmptyMessage(SCREEN_SHOT_SUCCESS);
			Log.v(TAG, "file save succeed");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "file save failed");
		} finally {
			if (null != lBos) {
				try {
					lBos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	private String getShotImagePath() {
		File file = new File(mScreenShotDesPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return mScreenShotDesPath + UUID.randomUUID().toString() + ".png";
	}

	/**
	 * 保存当前播放状态，在Activity onPause回调函数中执行该动作
	 */
	public void storeState() {
		int stopTime = mVideoView.getCurrentPosition();
		Log.v(TAG, "stopTime:" + stopTime);
		mStateBundle.putInt("stopTime", stopTime);
		mStateBundle.putBoolean("isPlaying", isPlaying);
	}

	/**
	 * 恢复之前的播放状态，在Activity onResume回调函数中执行该动作
	 */
	public void restoreState() {
		if (mStateBundle.isEmpty()) {
			Log.v(TAG, "doesn't save last states");
			return;
		}
		int startTime = mStateBundle.getInt("stopTime");
		boolean isPlaying = mStateBundle.getBoolean("isPlaying");
		if (mVideoView != null) {
			mVideoView.seekTo(startTime);
			if (isPlaying) {
				mVideoView.start();
			} else {
				mVideoView.pause();
			}
		}
	}

	public void destroy() {
		isRunning = false;
	}

	@Override
	public void onUpdated() {
		mPlayProgress.setProgress(mCurrentPosition);

		mHandler.sendEmptyMessage(0);
	}

	private class UpdatePlayProgress implements Runnable {

		IUpdateListener mUpdateListener;

		public void setmUpdateListener(IUpdateListener mUpdateListener) {
			this.mUpdateListener = mUpdateListener;
		}

		@Override
		public void run() {
			Log.v(TAG, "update thread start");
			while (isRunning) {
				if (isPlaying) {
					mCurrentPosition = mVideoView.getCurrentPosition();
					mUpdateListener.onUpdated();
				}
				try {
					Thread.sleep(UPDATE_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
