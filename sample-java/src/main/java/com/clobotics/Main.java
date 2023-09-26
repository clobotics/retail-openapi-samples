package com.clobotics;

import com.clobotics.dto.BaseResp;
import com.clobotics.dto.CreateUserReq;
import com.clobotics.dto.CreateUserResp;
import com.clobotics.dto.GetUserResp;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        OpenApiClient openApiClient = new OpenApiClient("https://openapi-dev.clobotics.cn",
                "a85ea925-ccbb-44ae-82f1-ee2c07213b6d", "Zzg5XDJqdTZ7M2w1PTdLKjBiImR2KyM0WzE8fklo");
        CreateUserReq userReq = new CreateUserReq();
        userReq.setUsername("tr11009");
        userReq.setPassword("tr11001");
        BaseResp<CreateUserResp> userResp = openApiClient.createUser(userReq);
        System.out.println(JsonUtil.toString(userResp));

        BaseResp<GetUserResp> getUserRespBaseDto = openApiClient.getUser("tr11009");
        System.out.println(JsonUtil.toString(getUserRespBaseDto));
    }
}
