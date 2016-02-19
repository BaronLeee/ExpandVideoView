package com.baron.common;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/**
 * @file VideoFileUtil.java
 * @author Lixiang
 * @brief
 * @date 2015-1-7 create
 * 
 */
public class ExpandVideoViewUtil {
	public static String getStandardTime(int second) {
		int min = second / 60;
		int sec = second % 60;
		String minTemp, secTemp;

		if (min < 10) {
			minTemp = "0" + String.valueOf(min);
		} else {
			minTemp = String.valueOf(min);
		}
		if (sec < 10) {
			secTemp = "0" + String.valueOf(sec);
		} else {
			secTemp = String.valueOf(sec);
		}

		return minTemp + ":" + secTemp;
	}

	public static Bitmap createVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		return bitmap;
	}

}
