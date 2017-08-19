package com.wos.play.rootdir.model_report.impl

import com.wos.play.rootdir.model_report.Report
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/11.
 * 电子报-数据源点击动作
 */
class EpaperDataReport(channelId: Int, val path:String, val date: String
                       , eventTime: Date = Date()): Report(0, channelId, eventTime) {

    override fun getCreateSql() :String = "CREATE TABLE IF NOT EXISTS EPAPER_STATISTICS" +
            " (CHANNEL_ID INTEGER, EPAPER_SOURCEPATH VARCHAR, EPAPER_DAY VARCHAR" +
            ", EPAPER_CLICKTIME VARCHAR);"

    override fun getInsertSql() :String = "INSERT INTO EPAPER_STATISTICS VALUES ($channelId" +
            ", '$path', '$date', '${format()}');"

}