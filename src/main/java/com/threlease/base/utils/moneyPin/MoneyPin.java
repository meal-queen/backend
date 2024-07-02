package com.threlease.base.utils.moneyPin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.StringUtility;
import com.threlease.base.utils.moneyPin.response.BizBaseInfo;
import com.threlease.base.utils.moneyPin.response.BizBaseInfoResponse;
import com.threlease.base.utils.moneyPin.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MoneyPin {
    @Value("${moneypin:client_id}")
    private String client_id;

    @Value("${moneypin:client_secret:}")
    private String client_secret;

    public Failable<Token, String> generate_token() {
        OkHttpClient client = new OkHttpClient();

        try {
            MediaType mediaType = MediaType.parse("application/json");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("grantType", "ClientCredentials");
            jsonObject.addProperty("clientId", this.client_id);
            jsonObject.addProperty("clientSecret", this.client_secret);

            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());

            Request request = new Request.Builder()
                    .url("https://api.moneypin.biz/bizno/v1/auth/token")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            Gson gson = new Gson();

            TokenResponse token = gson.fromJson(response.body().string(), TokenResponse.class);

            return Failable.success(
                    Token.builder()
                        .token(token.getToken())
                        .createdAt(System.currentTimeMillis())
                    .build()
            );
        } catch (IOException e) {
            return Failable.error(e.getMessage());
        }
    }

    public Failable<BizBaseInfo, String> getBizBaseInfo(String biz) {
        Failable<Token, String> token = generate_token();

        if (token.isError())
            return Failable.error(token.getError());

        OkHttpClient client = new OkHttpClient();

        try {
            MediaType mediaType = MediaType.parse("application/json");
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("bizNoList", biz);

            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());

            Request request = new Request.Builder()
                    .url("https://api.moneypin.biz/bizno/v1/biz/info/base")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer" + token.getValue().getToken())
                    .build();

            Response response = client.newCall(request).execute();

            Gson gson = new Gson();

            List<BizBaseInfoResponse> data = List.of(gson.fromJson(response.body().string(), BizBaseInfoResponse[].class));

            if (data.isEmpty())
                return Failable.error("Not Found Biz");

            if (data.get(0).getError() != null)
                return Failable.error(data.get(0).getError());

            return Failable.success(data.get(0).getInfo());
        } catch (IOException e) {
            return Failable.error(e.getMessage());
        }
    }
}
