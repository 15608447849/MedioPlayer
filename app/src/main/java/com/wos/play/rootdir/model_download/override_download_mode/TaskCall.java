package com.wos.play.rootdir.model_download.override_download_mode;

import android.os.Parcelable;

/**
 * Created by user on 2016/11/8.
 * 下载完成数据回调接口
 */

public interface TaskCall extends Parcelable{

    void downloadResult(Task task);
}
