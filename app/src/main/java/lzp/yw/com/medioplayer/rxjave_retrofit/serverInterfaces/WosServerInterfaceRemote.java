package lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by user on 2016/10/27.
 * test
 */
public interface WosServerInterfaceRemote {

    /**
     * 上线
     * get
     * http://192.168.6.14:9000/
     * terminal/heartBeat?cmd=ONLI:10000002  参数
     *
     * 返回值:
     * VOLU:10
     SYTI:2016-10-27 12:16:35
     SHDO:false
     UPSC:http://192.168.6.14:9000/terminal/1/schedule
     */
    @GET( "terminal/heartBeat?")
    Observable<String> ONFI(@Query("cmd") String test);
}
