package com.wos.play.rootdir.model_report;


/**
 * Created by Administrator on 2017/8/17.
 */

public class Test {

    public static void main(String[] args) {
        ReportHelper.onButton(null, 1, 5);
        ReportHelper.onEpaperNews(null, 1, 8);
        ReportHelper.onEpaperData(null, 1, "epaper/2/", "2017-12-20");
        ReportHelper.onImage(null, 1, 12, "epaper/132145789812.png", 10 );
        ReportHelper.onVideo(null, 1, 35, "epaper/132145789812.mp4", 1000 );
    }
}
