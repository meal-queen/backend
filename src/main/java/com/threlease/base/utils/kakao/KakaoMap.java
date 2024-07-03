package com.threlease.base.utils.kakao;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.threlease.base.utils.Failable;
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
                    .addHeader("Authorization", "KakaoAK " + this.restApi)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful())
                return Failable.error("해당 사업자의 위치를 가져오는데 실패하였습니다.");

            Gson gson = new Gson();

            KakaoResponse data = gson.fromJson(response.body().string(), KakaoResponse.class);

            if (data.getDocuments() != null && !data.getDocuments().isEmpty()) {
                return Failable.success(data.getDocuments().get(0).getRoad_address());
            } else {
                return Failable.error("주소 데이터가 비어있습니다.");
            }
        } catch (IOException e) {
            return Failable.error(e.getMessage());
        }
    }
}
