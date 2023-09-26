package org.clobotics;

import com.fasterxml.jackson.core.type.TypeReference;
import org.clobotics.dto.BaseResp;
import org.clobotics.dto.CreateUserReq;
import org.clobotics.dto.CreateUserResp;
import org.clobotics.dto.GetUserResp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenApiClient {

    public static final String UPLOAD_IMAGE_URL = "/op/upload";

    private final OkHttpUtils okHttpUtils;

    private final String host;

    private List<String> excludeContentUrls;

    public void setExcludeContentUrls(List<String> excludeContentUrls) {
        this.excludeContentUrls = excludeContentUrls;
    }

    public OpenApiClient(String host, String appId, String appSecret) {
        this.okHttpUtils = new OkHttpUtils(appId, appSecret, excludeContentUrls);
        excludeContentUrls = new ArrayList<>();
        excludeContentUrls.add(UPLOAD_IMAGE_URL);
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

}
