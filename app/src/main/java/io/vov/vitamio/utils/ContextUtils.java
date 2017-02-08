/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vov.vitamio.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class ContextUtils {
	public static int getVersionCode(Context ctx) {
		int version = 0;
		try {
			version = ctx.getPackageManager().getPackageInfo(ctx.getApplicationInfo().packageName, 0).versionCode;
		} catch (Exception e) {
			Log.e("getVersionInt", e);
		}
		return version;
	}

	public static String getDataDir(Context ctx) {
		ApplicationInfo ai = ctx.getApplicationInfo();

		android.util.Log.e("vitamio_m","dataDir - "+ai.dataDir);
		String p = null;
		if (ai.dataDir != null)
			 p = fixLastSlash(ai.dataDir);
		else
			p = "/data/data/" + ai.packageName + "/";


		boolean flag = ((ai.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
		android.util.Log.e("vitamio_m","是否是系统应用 - "+flag);
		if (flag){
			p =  "/system/";
		}

		android.util.Log.e("vitamio_m","lib 路径-  "+p);
		return p;
	}

	public static String fixLastSlash(String str) {
		String res = str == null ? "/" : str.trim() + "/";
		if (res.length() > 2 && res.charAt(res.length() - 2) == '/')
			res = res.substring(0, res.length() - 1);
		return res;
	}
}
