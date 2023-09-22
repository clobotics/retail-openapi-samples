package org.clobotics;

import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    private Map<String, String> headerMap;
    private final OkHttpClient okHttpClient;


    public OkHttpUtils(String appId, String appSecret, List<String> excludeContentUrls) {
        AuthInterceptor interceptor = new AuthInterceptor(appId, appSecret);
        interceptor.setExcludeContentUrls(excludeContentUrls);
        okHttpClient = new OkHttpClient.Builder()
                //Set up according to your business volume
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }




    public OkHttpUtils addHeader(String key, String value) {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>(16);
        }
        headerMap.put(key, value);
        return this;
    }

    public String postJson(String url, Object body) throws IOException {
        String jsonBody = JsonUtil.toString(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder request = new Request.Builder().post(requestBody).url(url);
       if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Response response = okHttpClient.newCall(request.build()).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    public String get(String url) throws IOException {
        Request.Builder request = new Request.Builder().get().url(url);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Response response = okHttpClient.newCall(request.build()).execute();
        return Objects.requireNonNull(response.body()).string();
    }
}
