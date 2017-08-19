package com.wos.play.rootdir.model_report

import android.content.Context
import com.wos.play.rootdir.model_report.impl.ButtonReport
import com.wos.play.rootdir.model_report.impl.EpaperDataReport
import com.wos.play.rootdir.model_report.impl.EpaperNewsReport
import com.wos.play.rootdir.model_report.impl.ResourceReport
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/10.
 * 用于记录终端动作，以供后台报表功能使用
 */

object ReportHelper{



    /**
     * 图片展示时间
     * @param reportId  图片ID
     * @param path 图片的相对地址
     * @param time 图片展示时长
     */
    @JvmStatic
    fun onImage(context:Context?, reportId:Int, path:String, time:Long) {
        onImage(context, 0, reportId, path, time)
    }

    /**
     * 图片展示时间
     * @param channelId 频道ID
     * @param reportId  图片ID
     * @param path 图片的相对地址
     * @param time 图片开始展示时间
     */
    @JvmStatic
    fun onImage(context:Context?, channelId:Int, reportId:Int, path:String, time:Long) {
        onEvent(context, ResourceReport(reportId, channelId, Report.TYPE_IMAGE
                , path, onTime(time), Date(time)))
    }


    /**
     * 视频播放时间以及时长
     * @param reportId  视频ID
     * @param path 视频的相对地址
     * @param time 视频开始播放时间(时间戳)
     */
    @JvmStatic
    fun onVideo(context:Context?, reportId:Int, path:String, time:Long) {
        onVideo(context, 0, reportId, path, time)
    }

    /**
     * 视频播放时间以及时长
     * @param channelId 频道ID
     * @param reportId  视频ID
     * @param path 视频的相对地址
     * @param time 视频开始播放时间(时间戳)
     */
    @JvmStatic
    fun onVideo(context:Context?, channelId:Int, reportId:Int, path:String, time:Long) {
        onEvent(context, ResourceReport(reportId, channelId, Report.TYPE_VIDEO
                , path, onTime(time), Date(time)))
    }

    /**
     * 按钮动作记录方法
     * @param channelId 频道ID
     * @param reportId  按钮组件ID
     */
    @JvmStatic
    fun onButton(context:Context?, channelId:Int, reportId:Int) {
        onEvent(context, ButtonReport(reportId, channelId))
    }

    /**
     * 电子报-数据源（报纸）动作记录方法
     * @param channelId 频道ID
     * @param path  报纸相对地址
     * @param date  哪天的报纸
     */
    @JvmStatic
    fun onEpaperData(context:Context?, channelId:Int, path:String, date:String) {
        onEvent(context, EpaperDataReport(channelId, path, date))
    }

    /**
     * 电子报-图集资讯动作记录方法
     * @param channelId 频道ID
     * @param reportId  图集资讯内容ID（包括图片、视频、Pdf、Office）
     */
    @JvmStatic
    fun onEpaperNews(context:Context?, channelId:Int, reportId:Int) {
        onEvent(context, EpaperNewsReport(reportId, channelId))
    }

    /**
     * 获取报表数据库名字
     * @param day 0 是指当天 n是指n天前(负数为n天后)
     */
    @JvmStatic
    fun getDbName(day: Int):String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1 * day)
        return "report_${SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.time)}.db"
    }

    /**
     * 获取距离开始时间到现在的正式时间，单位秒
     */
    private fun onTime(start:Long):Long = (System.currentTimeMillis() - start) / 1000


    /**
     * 记录保存数据
     */
    private fun onEvent(context:Context?, report:Report) {
        println("Context:$context, Report:$report")
        println(report.getCreateSql())
        println(report.getInsertSql())
        val db = context?.openOrCreateDatabase(getDbName(0), Context.MODE_PRIVATE, null)
        db?.execSQL(report.getCreateSql())
        db?.execSQL(report.getInsertSql())
        db?.close()
    }
}