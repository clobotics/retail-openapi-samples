package com.clobotics;

import com.clobotics.dto.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        OpenApiClient openApiClient = new OpenApiClient("https://openapi-dev.clobotics.cn",
                "<appId>", "<appSecret>");
        List<String> excludeContentUrls = new ArrayList<>();
        excludeContentUrls.add("/op/upload");
        openApiClient.setExcludeContentUrls(excludeContentUrls);
        CreateUserReq userReq = new CreateUserReq();
        userReq.setUsername("tr11009");
        userReq.setPassword("tr11001");
        BaseResp<CreateUserResp> userResp = openApiClient.createUser(userReq);
        System.out.println(JsonUtil.toString(userResp));

        BaseResp<GetUserResp> getUserRespBaseDto = openApiClient.getUser("tr11009");
        System.out.println(JsonUtil.toString(getUserRespBaseDto));
        File file = new File(System.getProperty("user.dir") + "/shelf.png");
        Map<String, String> datas = new HashMap<>();
        datas.put("watermark", "{" +
                "  \"data\": [" +
                "    \"Task Name: Zengcheng Store 1\"," +
                "    \"Store Name: Zengcheng Store 1\"," +
                "    \"Store Code: zz_001\"," +
                "    \"Sales Representative: Xiaoming\"," +
                "    \"Time: 2023-07-19 16:30:00\"," +
                "    \"Store Address: xxxxx, Zengcheng District, Guangzhou City\"" +
                "  ]," +
                "  \"separator\": \"auto\"" +
                "}");
        System.out.println(System.getProperty("user.dir"));
        BaseResp<UploadResp> uploadRespBaseDto = openApiClient.uploadFile(file, datas);
        System.out.println(JsonUtil.toString(uploadRespBaseDto));
    }
}
