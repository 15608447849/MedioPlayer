package com.wos.play.rootdir.model_report.impl

import com.wos.play.rootdir.model_report.Report
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/11.
 * 电子报-图集资讯点击动作
 */
class ResourceReport(reportId:Int, channelId:Int, val type:Int, val path:String, val time:Long = 0
                     , eventTime: Date = Date()): Report(reportId, channelId, eventTime) {

    override fun getCreateSql() :String = "CREATE TABLE IF NOT EXISTS RESOURCE_STATISTICS" +
            " (CHANNEL_ID INTEGER, RESOURCE__ID INTEGER, RESOURCE_TYPE INTEGER" +
            ", RESOURCE_PATH VARCHAR, RESOURCE_STARTTIME VARCHAR, RESOURCE_PLAYTIME INTEGER);"

    override fun getInsertSql() :String = "INSERT INTO RESOURCE_STATISTICS VALUES ($channelId" +
            ", $reportId, $type, '$path', '${format()}', $time);"

}