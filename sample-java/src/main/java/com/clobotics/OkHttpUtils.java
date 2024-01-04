package com.clobotics;

import com.clobotics.utils.CollectionUtil;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

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

    public String postJson(String url, Object body) throws IOException {
        String jsonBody = JsonUtil.toString(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder request = new Request.Builder().post(requestBody).url(url);
        try (Response response = okHttpClient.newCall(request.build()).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

    public String get(String url) throws IOException {
        Request.Builder request = new Request.Builder().get().url(url);
        try (Response response = okHttpClient.newCall(request.build()).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

    public String formData(String url, Map<String, String> datas, File file) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (CollectionUtil.isNotEmpty(datas)) {
            for (Map.Entry<String, String> entry : datas.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = builder.setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request.Builder request = new Request.Builder().post(body).url(url);
        try (Response response = okHttpClient.newCall(request.build()).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
