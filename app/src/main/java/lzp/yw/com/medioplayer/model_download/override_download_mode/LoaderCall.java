package lzp.yw.com.medioplayer.model_download.override_download_mode;

/**
 * Created by user on 2016/11/8.
 * 下载完成数据回调接口
 */
public interface LoaderCall {
    void downloadResult(String filePath, String state);
}
