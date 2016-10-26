package lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces;

import java.util.Map;

import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.WosResult;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by user on 2016/10/26.
 */
public interface WosServerInterface {

    /**
     * 申请终端
     * {"version":"18.7.3",
     "code":"999",
     "mac":"D0-92-91-AD-3F-F9",
     "screenResolutionWidth":"1024",
     "screenResolutionHeight":"1024"}
     * @return
     */

    @GET( "terminal/apply")
    Observable<WosResult> getTerminal(@QueryMap Map<String, String> map);

}
