package com.threlease.base.utils.kakao;

import com.squareup.okhttp.*;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.StringUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KakaoMap {
    @Value("${kakao.rest}")
    private String restApi;

    public Failable<Address, String> addressToPos(String address) {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url("https://dapi.kakao.com/v2/local/search/address.json?query=" + address)
                    .addHeader("Authorization", "KAKAOAK " + this.restApi)
                    .build();

            Response response = client.newCall(request).execute();
            KakaoResponse data = StringUtility.stringToClass(response.body().string(), KakaoResponse.class);

            return Failable.success(data.getDocuments().get(0).getRoad_address());
        } catch (IOException e) {
            return Failable.error(e.getMessage());
        }
    }
}
