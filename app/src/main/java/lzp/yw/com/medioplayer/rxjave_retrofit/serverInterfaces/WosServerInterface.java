package lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces;

import java.util.List;
import java.util.Map;

import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.WosResult;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ScheduleBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.content_gallary.GallaryBean;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by user on 2016/10/26.
 * json
 */
public interface WosServerInterface{

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

    /**
     * 获取所有排期
     * UPSC:
     * http://172.16.0.17:9000/terminal/32/schedule
     * 返回值对象
     *  cmd_upsc包下面的 List<ScheduleBean>
     */
    @GET("terminal/{id}/schedule")
    Observable<List<ScheduleBean>> UPSC(@Path("id") String id);

    /**
     * 获取图集
     * http://172.16.0.17:9000
     *   /epaper/dyannews/page?stairId=103&categoryId=-1&sortBy=allSorts+asc,upDate+desc&filter
     *   //=Base64
     */
   // @GET("{path}")
//    Observable<GallaryBean> getGallaryEntity(@Path(value="path",encoded=true) String path);
    @GET()
    Observable<GallaryBean> getGallaryEntity(@Url String url);




























}
