package com.wos.play.rootdir.model_application.viewlayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.CVideoView;
import com.wos.play.rootdir.model_download.entity.TaskFactory;
import com.wos.play.rootdir.model_download.kernel.DownloadBroad;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_monitor.tools.Stools;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ShellUtils;

import static com.wos.play.rootdir.R.id.main_layout;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.e("MainActivity","活动层-------------onCreate----------------------");
        setContentView(R.layout.activity_main);
        setIsOnBack(false);
        setStopOnDestroy(false);
        registerBroad(1);
        sendMsgCommServer("online", null); // 通知通信服务上线
    }

    @Override
    protected void receiveService(String result, String result1) {
        Logs.i("MainActivity","接收广播：result->"+result);
        Logs.i("MainActivity","接收广播：result1->"+result1);
        if(CMD_INFO.UIRE.equals(result)){//重启播放器
            unInitUI();
            sendMsgCommServer("offline", null); //通知通信服务 发送下线指令
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        if(CMD_INFO.SCRN.equals(result)) {//实时截图，定时截图
            catchScreen(result1);
            if (result1.contains("real_")) {
                uploadServer(result1);//发送任务到文件上传
            } else {
                String remotePath = "/Android/" + SystemInfos.get().getTerminalNo() + "/screen/";//设置 远程文件目录
                String ftpUrl = getFTPUrls(remotePath + result1.substring(result1.lastIndexOf("/")+1));//设置响应服务器
                uploadFTP(result1, remotePath);//上传
                sendMsgCommServer("pointTimeScreen", ftpUrl);// 通知服务器,定时截图上传地址
            }
        }
    }

    /**
     * ftp_url -> [ CAPT:10000406（终端号）,1483786140221（日期）,
     *              ftp://ftp:FTPmedia@172.16.0.17:21/ShotcutPic-10000406/1483786140221.jpg（ftp地址）]
     * @param str
     * @return
     */
    private String getFTPUrls(String str) {
        return "CAPT:" + SystemInfos.get().getTerminalNo()+","
                + TimeOperator.dateToStamp()+","
                +"ftp://"+ SystemInfos.get().getFtpUser()+":"+ SystemInfos.get().getFtpPass()+"@"+ SystemInfos.get().getFtpAddress()+":"+ SystemInfos.get().getFtpPort()+str;
    }

    /**
     * 生成图片
     * @param imagePath
     */
    private void catchScreen(String imagePath) {
        try {
            String cmd = "screencap -p " + imagePath;
            ShellUtils.CommandResult result = ShellUtils.execCommand(cmd,true,true);
            Logs.d("MainActivity", "result.result :::: - " + result.result);
            if (result.result == 0){
                Logs.d("linux 命令(screenCap -p) 截屏完成 - "+imagePath);
            }
            File image = new File(imagePath);
            if (image.exists()){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds=true;
                FileInputStream fis = new FileInputStream(image);
                BitmapFactory.decodeStream(fis,null,options);
                Logs.e("命令(screenCap -p) 截屏大小： " + options.outWidth*options.outHeight);
                if (options.outWidth*options.outHeight > 10) return;
            }
            if (cn.trinea.android.common.util.FileUtils.isFileExist(imagePath)){
                cn.trinea.android.common.util.FileUtils.deleteFile(imagePath);
            }
        } catch (FileNotFoundException e) {
            Logs.e("命令行截屏文件无效 : "+e.getMessage() );
        }
        Bitmap bitmap = null;
        FileOutputStream fos = null;
        try {
            View view = this.getWindow().getDecorView();//设置主屏界面，作为一个控件
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);//提高绘图速度
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            List<View> vList = getAllChildViews(view);
            Bitmap videoImage;
            //视频截图
            for (View z : vList) {
                if (z instanceof CVideoView) {
                    CVideoView cvv = (CVideoView) z;
                    videoImage =  cvv.getCurrentFrame();
                    if (videoImage != null) {
                        Rect rect = new Rect();
                        Point point = new Point();
                        z.getGlobalVisibleRect(rect, point);//获取view在屏幕上的位置
                        bitmap = composeImage(point.x, point.y, bitmap, videoImage);//绘制视频截图
                    }
                }
            }
            if (bitmap!=null) {
                File file = new File(imagePath);//保存新的位图到本地路径
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.flush();
            }
        } catch(Exception e) {
            cn.trinea.android.common.util.FileUtils.deleteFile(imagePath);
            e.printStackTrace();
        } finally {
            try {
                if (bitmap!=null) bitmap.recycle();
                if (fos!=null) fos.close();
                Logs.e("MainActivity: " + imagePath);
            } catch (IOException e) {

            }
        }
    }

    /**
     * 获取布局上的所有子view存入集合
     * @param view
     * @return
     */
    private List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<>();
        getSubView(view,allChildren);
        return allChildren;
    }

    /**
     * @param vg
     * @param list
     */
    private void getSubView(View vg,List<View> list) {
        if (vg instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) vg;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof ViewGroup) {
                    getSubView(viewchild,list);
                } else {//视图 添加
                    list.add(viewchild);
                    list.addAll(getAllChildViews(viewchild));
                }
            }
        }
    }

    /**
     * 根据旧图的宽高做一个画布，把子图画到原图上，返回新图。
     * @param left
     * @param top
     * @param bitmap old
     * @param videoImage child
     * @return
     */
    public static Bitmap composeImage(int left, int top, Bitmap bitmap, Bitmap videoImage) {
        if (bitmap != null && videoImage != null ) {
            // 获取原图宽高
            int sw = bitmap.getWidth();
            int sh = bitmap.getHeight();

            // 根据原图宽高创建新位图对象
            Bitmap newb = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
            // 根据新位图创建一个等大小的画布
            Canvas cv = new Canvas(newb);

            // 在画布上做新图
            cv.drawBitmap(bitmap, 0, 0, null);
            cv.drawBitmap(videoImage, left, top, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
            return newb;
        }
        return null;
    }

    /**
     * 文件上传接口 -> http://配置的IP地址:端口号/terminal/jpgUpload?terminalId=终端号
     */
    private final String uploadUrl = "http://"+ SystemInfos.get().getServerip()+":"+ SystemInfos.get().getServerport()+"/terminal/jpgUpload?terminalId="+ SystemInfos.get().getTerminalNo();

    /**
     * 发送截图到服务器
     * @param localPath
     */
    private void uploadServer(String localPath) {
        sendTaskToLocalServer(TaskFactory.gnrTaskUploadHTTP(uploadUrl,localPath));
    }

    /**
     * 上传任务中心
     * @param task
     */
    private synchronized void sendTaskToLocalServer(Task task) {
        if (task!=null){
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(task);//创建任务
            if (getApplicationContext()!=null){
                Intent intent = new Intent();
                intent.setAction(DownloadBroad.ACTION);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(DownloadBroad.PARAM1, tasks);
                intent.putExtras(bundle);
                getApplicationContext().sendBroadcast(intent);//发送广播
            }
        }
    }

    /**
     * 上传ftp服务器
     * @param LocalFilePath
     * @param remoteFilePath
     */
    private void uploadFTP(String LocalFilePath,String remoteFilePath) {
        sendTaskToLocalServer(TaskFactory.gnrTaskUploadFTP(LocalFilePath,remoteFilePath));
    }


    @Override
    protected void onStart() {
        super.onStart();
        Logs.e("MainActivity","活动层-------------onStart----------------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
        Logs.e("MainActivity","活动层-------------onResume----------------------");

    }
    @Override
    protected void onPause() {
        super.onPause();
        Logs.e("MainActivity","活动层-------------onPause----------------------");

    }
    @Override
    protected void onStop() {
        Logs.e("MainActivity","活动层-------------onStop----------------------");
        boolean flag = Stools.isRunningForeground(getApplicationContext(), WatchServer.activityList);
        if (!flag){
            unInitUI();
            sendMsgCommServer("offline", null); //通知通信服务 发送下线指令
            finish(); // 关闭Act 避免下次进来无法启动onCreate
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.e("MainActivity","活动层-------------onDestroy----------------------");
    }

    //获取activity的底层layout
    @Override
    public ViewGroup getActivityLayout() {
        return (AbsoluteLayout) findViewById(main_layout);
    }
}
