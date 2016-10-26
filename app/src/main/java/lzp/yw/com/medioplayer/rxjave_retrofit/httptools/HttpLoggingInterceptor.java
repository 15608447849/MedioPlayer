package lzp.yw.com.medioplayer.rxjave_retrofit.httptools;



import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Platform;
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

    private volatile Level level = Level.BODY;

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


    /**
     * okhttp log 接口
     */
    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(message);
            }
        };
    }


    private final Logger logger;
    /*
       构造
        */
    public HttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    /**
     * 构造 2
     * @param logger  okhttp log 接口
     */
    public HttpLoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Level level = this.level;//日志等级

        Request request = chain.request();//请求
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = (level == Level.BODY);
        boolean logHeaders = (logBody || level == Level.HEADERS);

        RequestBody requestBody = request.body();
        boolean hasRequestBody = (requestBody != null);

        String requestStartMessage = request.method() + ' ' + request.url();
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        logger.log(requestStartMessage);

        if (logHeaders) {

            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)");
            } else if (request.body() instanceof MultipartBody) {
                //如果是MultipartBody，会log出一大推乱码的东东

            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    contentType.charset(UTF8);
                }

                logger.log(buffer.readString(charset));

//                logger.log(request.method() + " (" + requestBody.contentLength() + "-byte body)");
            }
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        logger.log(response.code() + ' ' + response.message() + " (" + tookMs + "ms" + ')');

        return response;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private static String protocol(Protocol protocol) {
        return protocol == Protocol.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1";
    }


}
