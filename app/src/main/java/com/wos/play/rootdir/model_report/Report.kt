package com.wos.play.rootdir.model_report

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by leolaurel.e.l on 2017/8/11.
 * @param reportId   数据唯一ID
 * @param channelId  数据所属频道
 * @param eventTime  数据的发生时间
 */
abstract class Report(val reportId:Int, val channelId: Int, private val eventTime: Date) {

    companion object {
        val TYPE_IMAGE:Int = 0
        val TYPE_VIDEO:Int = 1
    }

    /**  创表语句 */
    abstract fun getCreateSql(): String
    /**  插入语句 */
    abstract fun getInsertSql(): String

    fun format():String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(eventTime)
}
