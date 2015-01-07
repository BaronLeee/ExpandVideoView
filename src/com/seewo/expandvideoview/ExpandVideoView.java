/**
 * Seewo Software
 * Copyright (c) 2008 - 2015 Seewo, Inc. All rights reserved.
 *
 * All software, firmware and related documentation herein ("Seewo Software") are
 * intellectual property of Seewo, Inc. ("Seewo") and protected by
 * law, including, but not limited to, copyright law and international treaties.
 * Any use, modification, reproduction, retransmission, or republication of all
 * or part of Seewo Software is expressly prohibited, unless prior written
 * permission has been granted by Seewo.
 *
 * By accessing, browsing and/or using Seewo Software, you acknowledge that you
 * have read, understood, and agreed, to be bounded by below terms ("Terms") and to
 * comply with all applicable laws and regulations:
 *
 * 1. Seewo shall retain any and all right, ownership and interest to Seewo
 * Software and any modification/derivatives thereof.  No right, ownership,
 * or interest to Seewo Software and any modification/derivatives thereof is
 * transferred to you under Terms.
 *
 * 2. You understand that Seewo Software might include, incorporate or be supplied
 * together with third party’s software and the use of Seewo Software may require
 * additional licenses from third parties.  Therefore, you hereby agree it is your
 * sole responsibility to separately obtain any and all third party right and
 * license necessary for your use of such third party’s software.
 *
 * 3. Seewo Software and any modification/derivatives thereof shall be deemed as
 * Seewo’s confidential information and you agree to keep Seewo’s confidential
 * information in strictest confidence and not disclose to any third party.
 *
 * 4. Seewo Software is provided on an "AS IS" basis without warranties of any kind.
 * Any warranties are hereby expressly disclaimed by Seewo, including without
 * limitation, any warranties of merchantability, non-infringement of intellectual
 * property rights, fitness for a particular purpose, error free and in conformity
 * with any international standard.  You agree to waive any claim against Seewo for
 * any loss, damage, cost or expense that you may incur related to your use of Seewo
 * Software.  In no event shall Seewo be liable for any direct, indirect, incidental
 * or consequential damages, including without limitation, lost of profit or revenues,
 * lost or damage of data, and unauthorized system use.  You agree that this Section 4
 * shall still apply without being affected even if Seewo Software has been modified
 * by Seewo in accordance with your request or instruction for your use, except
 * otherwise agreed by both parties in writing.
 *
 * 5. If requested, Seewo may from time to time provide technical supports or
 * services in relation with Seewo Software to you for your use of Seewo Software
 * in conjunction with your or your customer’s product ("Services").  You understand
 * and agree that, except otherwise agreed by both parties in writing, Services are
 * provided on an "AS IS" basis and the warranty disclaimer set forth in Section 4
 * above shall apply.
 *
 * 6. Nothing contained herein shall be construed as by implication, estoppels or
 * otherwise: (a) conferring any license or right to use Seewo name, trademark,
 * service mark, symbol or any other identification; (b) obligating Seewo or any
 * of its affiliates to furnish any person, including without limitation, you and
 * your customers, any assistance of any kind whatsoever, or any information; or
 * (c) conferring any license or right under any intellectual property right.
 *
 * 7. These terms shall be governed by and construed in accordance with the laws
 * of Taiwan, R.O.C., excluding its conflict of law rules.  Any and all dispute
 * arising out hereof or related hereto shall be finally settled by arbitration
 * referred to the Chinese Arbitration Association, Taipei in accordance with
 * the ROC Arbitration Law and the Arbitration Rules of the Association by three (3)
 * arbitrators appointed in accordance with the said Rules.  The place of
 * arbitration shall be in Taipei, Taiwan and the language shall be English.
 * The arbitration award shall be final and binding to both parties.
 */
package com.seewo.expandvideoview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
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

import com.seewo.common.CommonUtil;

/**
 * @file ExpandVideoView.java
 * @author Lixiang
 * @brief
 * @date 2015-1-6 create
 * 
 */
public class ExpandVideoView extends LinearLayout implements OnClickListener {
	private static final String TAG = "ExpandVideoView";
	private static final int UPDATE_INTERVAL = 100;
	private static final int CHANGED_STEP = 3000;
	private static final int BACK = 1;
	private static final int FORWARD = -1;
	private static final String DEFAULT_SCREEN_SHOT_DEST_PATH =
			Environment.getExternalStorageDirectory() + File.separator + "ExpandVideoView" + File.separator + "ScreenShot" + File.separator;

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

	private static Bundle mStateBundle;

	private String mVideoPath;
	private boolean isPlaying = false;
	private int mVideoTotalLength;
	private int mCurrentPosition;
	private String mScreenShotDesPath = DEFAULT_SCREEN_SHOT_DEST_PATH;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == -1) {
				Toast.makeText(mContext, "保存截图成功", Toast.LENGTH_SHORT).show();

			} else {
				mCurrentPosition = msg.what;
				mPlayProgress.setProgress(mCurrentPosition);
				mCurrentTimeView.setText(CommonUtil.getStandardTime(mCurrentPosition / 1000));

			}

		}

	};

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			if (mVideoView != null) {
				mVideoView.seekTo(progress);
				mScreenShotBtn.setEnabled(true);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
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
	protected void onFinishInflate() {
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
	}

	public void initListener() {
		mBackBtn.setOnClickListener(this);
		mForwardBtn.setOnClickListener(this);
		mSwitchPlayStateBtn.setOnClickListener(this);
		mScreenShotBtn.setOnClickListener(this);
		mPlayProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
	}

	private void notifySwitchBtnToChange() {
		if (mVideoView.isPlaying()) {
			mSwitchPlayStateBtn.setText("暂停");
		} else {
			mSwitchPlayStateBtn.setText("播放");
		}
	}

	public void setVideoPath(String path) {
		Log.v(TAG, "set path to:" + path);
		if (path != null) {
			mVideoPath = path;
			mVideoTotalLength = getVideoTimeLegth(mVideoPath);
			String duration = CommonUtil.getStandardTime(mVideoTotalLength);
			mPlayProgress.setMax(mVideoTotalLength * 1000);
			Log.v(TAG, "视频长度：" + duration);
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

	public void play(int playPoint) {
		if (mVideoPath == null || !new File(mVideoPath).exists()) {
			Log.e(TAG, "there is no valid video source");
			return;
		}
		mCurrentPosition = playPoint;
		Log.v(TAG, "the video path:" + mVideoPath);
		mVideoView.setVideoPath(mVideoPath);
		Log.v(TAG, "start play");
		mVideoView.start();
		mSwitchPlayStateBtn.setText("暂停");
		mVideoView.seekTo(playPoint);
		mHandler.post(updatePlayProgress);
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

	public void setScreenShotSavePath(String desPath) {
		mScreenShotDesPath = desPath;
	}

	private Runnable updatePlayProgress = new Runnable() {
		int currentPosition;

		@Override
		public void run() {
			if (mVideoView.isPlaying()) {
				currentPosition = mVideoView.getCurrentPosition();
				mHandler.sendEmptyMessage(currentPosition);
			}
			mHandler.postDelayed(updatePlayProgress, UPDATE_INTERVAL);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_switch:
			switchPlayOrPause();
			break;
		case R.id.btn_back:
			quickPlay(BACK);
			break;
		case R.id.btn_forward:
			quickPlay(FORWARD);
			break;
		case R.id.btn_shot_screen:
			screenShot();
			break;
		default:
			break;
		}
	}

	private void switchPlayOrPause() {
		if (mVideoView.isPlaying()) {
			mVideoView.pause();
			mScreenShotBtn.setEnabled(true);
		} else {
			mVideoView.start();
			mScreenShotBtn.setEnabled(false);
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
		if (!mVideoView.isPlaying()) {
			mScreenShotBtn.setEnabled(true);
		}
		mVideoView.seekTo(next);
		mPlayProgress.setProgress(next);
		mCurrentPosition = next;
	}

	public void screenShot() {
		mScreenShotBtn.setEnabled(false);
		if (null != mVideoPath) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					// mMediaMetadataRetriever.setDataSource(mVideoPath);
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
			mHandler.sendEmptyMessage(-1);
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

	public void storeState() {
		int stopTime = mVideoView.getCurrentPosition();
		Log.v(TAG, "stopTime:" + stopTime);
		mStateBundle.putInt("stopTime", stopTime);
		mStateBundle.putBoolean("isPlaying", mVideoView.isPlaying());
	}

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
		mHandler.removeCallbacks(updatePlayProgress);
	}
}
