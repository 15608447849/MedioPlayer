package lzp.yw.com.medioplayer.model_universal.httpconnect;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by user on 2016/11/2.
 */

public interface HttpServerInterface {


    /**
     * 申请终端
     * 参数
     * {"version":"18.7.3",
     "code":"999",
     "mac":"D0-92-91-AD-3F-F9",
     "screenResolutionWidth":"1024",
     "screenResolutionHeight":"1024"
     }
     * @return
     */
    @GET("")
    Observable<String> getTerminalId(@Url String url);

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
    @GET("")
    Observable<String> sendOnfi(@Url String url);












}
