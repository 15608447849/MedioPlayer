package com.wos.play.rootdir.model_universal.httpconnect;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.wos.play.rootdir.model_universal.tool.Logs;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by user on 2016/10/26.
 * lzp
 * okhtto log 拦截器
 *
 *  使用 :
 *      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
            Log.i("RxJava", message);
            }
            });
         OkHttpClient client = new OkHttpClient.Builder()
         //log请求参数
         .addInterceptor(interceptor)
         .build();
 */
public class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String TAG = "HttpLoggingInterceptor";


    public enum Level {
        /**
         * No logs.
         */
        NONE,

        /**
         * Logs request and response lines.日志请求和响应
         * Example:{ 例子
         * @code
         * POST /greeting HTTP/1.1 (3-byte body)
         * HTTP/1.1 200 OK (22ms, 6-byte body)
         * }
         */
        BASIC,

        /**
         * Logs request and response lines and their respective headers.  日志请求和响应线和各自的标题。
         * Example:{
         * @code
         * POST /greeting HTTP/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * END POST
         *
         * HTTP/1.1 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * END HTTP
         * }
         */
        HEADERS,

        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * 日志请求和响应线和各自的标题和正文(如果存在)。
         *
         * Example:
         * {@code
         *  POST /greeting HTTP/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         *
         *  END GET
         *
         * HTTP/1.1 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         *
         * END HTTP
         * }
         */
        BODY

    }


    private volatile Level level = Level.BODY;
    /**
     * Change the level at which this interceptor logs.
     * 改变该拦截器记录的水平。
     *
     */
    public HttpLoggingInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {


        Request request = chain.request();//请求
        RequestBody requestBody = request.body();

        Level level = this.level;//日志等级
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = (level == Level.BODY);

        boolean logHeaders = (logBody || level == Level.HEADERS);

        boolean hasRequestBody = (requestBody != null);

        String requestStartMessage = request.method() + ' ' + request.url();

        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "- byte body)";
        }
        Logs.i(TAG,requestStartMessage);


        if (logHeaders) {

            if (!logBody || !hasRequestBody) {
                Logs.i(TAG,"END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Logs.i(TAG,"END " + request.method() + " (encoded body omitted)");
            } else if (request.body() instanceof MultipartBody) {
                //如果是MultipartBody，会log出一大推乱码的东东
                Logs.i(TAG, "request.body() instanceof MultipartBody = "  + request.method());
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    contentType.charset(UTF8);
                }
                Logs.i(TAG,buffer.readString(charset));
            }
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        Logs.i(response.code() + " - " + response.message() + "-  (用时 :" + tookMs + "ms" + ')');
       // Logs.d("服务器返回值:\n"+response.body().contentType() +" ; length: "+ response.body().contentLength() +"\n "+response.body().source().readString(UTF8));

        return response;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}
