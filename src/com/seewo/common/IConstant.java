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
package com.seewo.common;

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
