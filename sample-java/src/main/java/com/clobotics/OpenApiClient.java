package com.clobotics;

import com.clobotics.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenApiClient {

    public static final String UPLOAD_IMAGE_URL = "/op/upload";

    private final OkHttpUtils okHttpUtils;

    private final String host;

    private List<String> excludeContentUrls;

    public void setExcludeContentUrls(List<String> excludeContentUrls) {
        this.excludeContentUrls = excludeContentUrls;
    }

    public OpenApiClient(String host, String appId, String appSecret) {
        excludeContentUrls = new ArrayList<>();
        excludeContentUrls.add(UPLOAD_IMAGE_URL);
        this.okHttpUtils = new OkHttpUtils(appId, appSecret, excludeContentUrls);
        this.host = host;
    }

    public BaseResp<CreateUserResp> createUser(CreateUserReq req) throws IOException {
        String url = "/user";
        String resultStr = okHttpUtils.postJson(host + url, req);
        return JsonUtil.toObj(resultStr, new TypeReference<BaseResp<CreateUserResp>>() {});
    }

    public BaseResp<GetUserResp> getUser(String username) throws IOException {
        String url = "/user?name=http%3A%2F%2Fwww.baidu.com&username=" + username;
        String resultStr = okHttpUtils.get(host + url);
        return JsonUtil.toObj(resultStr, new TypeReference<BaseResp<GetUserResp>>() {});
    }

    public BaseResp<UploadResp> uploadFile(File file, Map<String, String> datas) throws IOException {
        String url = "/op/upload";
        String resultStr = okHttpUtils.formData(host + url, datas, file);
        return JsonUtil.toObj(resultStr, new TypeReference<BaseResp<UploadResp>>() {});
    }
}
