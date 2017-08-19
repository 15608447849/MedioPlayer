package com.wos.play.rootdir.model_report.impl

import com.wos.play.rootdir.model_report.Report
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/11.
 * 电子报-图集资讯点击动作
 */
class EpaperNewsReport(reportId:Int, channelId: Int, eventTime: Date = Date())
            : Report(reportId, channelId, eventTime) {

    override fun getCreateSql() :String = "CREATE TABLE IF NOT EXISTS NEWS_STATISTICS" +
            " (CHANNEL_ID INTEGER, NEWS_ID INTEGER, NEWS_CLICKTIME VARCHAR);"

    override fun getInsertSql() :String = "INSERT INTO NEWS_STATISTICS VALUES (" +
            "$channelId, $reportId, '${format()}');"

}