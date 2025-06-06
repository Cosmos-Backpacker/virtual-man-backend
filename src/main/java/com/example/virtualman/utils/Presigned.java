package com.example.virtualman.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.time.Instant;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;


@Component
public class Presigned {

    @Value("${Presigned.appKey}")
    private String appKey;

    @Value("${Presigned.accessToken}")
    private String accessToken;

    public static String GenSignature(String signingContent, String accessToken) {
        try {
            // 计算 HMAC-SHA256 值
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(accessToken.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hashInBase64 = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(signingContent.getBytes(StandardCharsets.UTF_8)));

            // URL encode
            String encodeSign = URLEncoder.encode(hashInBase64, StandardCharsets.UTF_8.toString());

            // 拼接签名
            String signature = "&signature=" + encodeSign;

            return signature;
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String GenReqURL(String baseURL) {
        String accessToken = this.accessToken;  //网址：https://xiaowei.cloud.tencent.com/ivh/#/asserts_management
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        // 按字典序拼接待计算签名的字符串
        String signingContent = "appkey=" + this.appKey + "&timestamp=" + timestamp;

        // 计算签名
        String signature = GenSignature(signingContent, accessToken);

        // 拼接访问接口的完整URL
        return baseURL + "?" + signingContent + signature;
    }

    public String getReqUrl() {
        // 请求接口的基础url
        String baseUrl = "https://gw.tvs.qq.com/v2/ivh/videomaker/broadcastservice/videomake";
        String url = GenReqURL(baseUrl);

        return url;
    }

    public String getReqProgressUrl() {
        // 请求接口的基础url
        String baseUrl = "https://gw.tvs.qq.com/v2/ivh/videomaker/broadcastservice/getprogress";
        String url = GenReqURL(baseUrl);
        // 使用示例时间戳输出应当如下:
        // Example 1:https://api.example.com/v2/ivh/example_uri?appkey=example_appkey&timestamp=1717639699&signature=aCNWYzZdplxWVo%2BJsqzZc9%2BJ9XrwWWITfX3eQpsLVno%3D
        System.out.println("请求url为:" + url);
        return url;
    }


}
