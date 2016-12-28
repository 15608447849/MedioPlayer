package com.wos.play.rootdir.model_universal.tool;

/**
 * Created by user on 2016/12/27.
 */

public class UnImpl {
    public static boolean func_CB(int playType){
        if (playType == 4){
            //插播未实现
            Logs.d("未实现","插播不支持");
            return true;
        }
        return false;
    }
}
