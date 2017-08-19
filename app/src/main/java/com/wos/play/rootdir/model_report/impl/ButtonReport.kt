package com.wos.play.rootdir.model_report.impl

import com.wos.play.rootdir.model_report.Report
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/11.
 * 按钮点击动作
 */
class ButtonReport(reportId:Int, channelId: Int, eventTime: Date = Date())
         : Report(reportId, channelId, eventTime) {

    override fun getCreateSql() :String = "CREATE TABLE IF NOT EXISTS BUTTON_STATISTICS" +
            " (CHANNEL_ID INTEGER, BUTTON_ID INTEGER, BUTTON_CLICKTIME);"

    override fun getInsertSql() :String = "INSERT INTO BUTTON_STATISTICS VALUES ($channelId" +
            ", $reportId, '${format()}');"

}
