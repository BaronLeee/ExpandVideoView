package com.baron.common;

import java.io.File;

import android.os.Environment;

/**
 * @file IConstant.java
 * @author Lixiang
 * @brief
 * @date 2015-1-7 create
 * 
 */
public interface IConstant {
	static final String APP_ROOT_PATH =
			Environment.getExternalStorageDirectory().toString() + File.separator + "VideoShot" + File.separator;
	static final String SAVE_PATH = APP_ROOT_PATH + File.separator;
}
