package com.clobotics;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;
import okio.Buffer;
import com.clobotics.utils.CollectionUtil;
import com.clobotics.utils.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class AuthInterceptor implements Interceptor {

    private final String appId;

    private final String appSecret;

    private List<String> excludeContentUrls;

    public void setExcludeContentUrls(List<String> excludeContentUrls) {
        this.excludeContentUrls = excludeContentUrls;
    }

    public AuthInterceptor(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request req = chain.request();
        String nonce = UUID.randomUUID().toString();
        Long timestamp = System.currentTimeMillis() / 1000;
        StringBuilder sb = new StringBuilder(req.method() + "\n");
        if (needContentMd5(req.method()) && !isExcludeContentUrl(req)) {
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            String contentMd5 = "";
            if (req.body() != null) {
               contentMd5 = Base64.getEncoder().encodeToString(messageDigest.digest(getRequestBody(req.body())));
            }
            sb.append(contentMd5).append("\n");
        }
        sb.append(timestamp).append("\n");
        sb.append(nonce).append("\n");
        sb.append(canonicalizedResource(req.url().url()));
        String signStr;
        try {
            signStr = Base64.getEncoder().encodeToString(hmac_sha256(sb.toString(), appSecret));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        req = req.newBuilder().addHeader("Nonce", nonce)
                .addHeader("Timestamp", String.valueOf(timestamp))
                .addHeader("Authorization", String.format("cbs:%s:%s", appId, signStr)).build();
        return chain.proceed(req);
    }

    private boolean isExcludeContentUrl(Request req) {
        if (CollectionUtil.isEmpty(excludeContentUrls)) {
            return false;
        }
        return excludeContentUrls.contains(req.url().toString());
    }

    private boolean needContentMd5(String method) {
        return HttpMethod.requiresRequestBody(method);
    }

    private byte[] getRequestBody(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readByteArray();
    }

    private static String canonicalizedResource(URL url) throws UnsupportedEncodingException {
        if (StringUtil.isNotEmpty(url.getQuery())) {
            String params = url.getQuery();
            Map<String, String> paramsMap = Arrays.stream(params.split("&"))
                    .collect(Collectors.toMap(item -> item.split("=")[0], item -> item.split("=")[1]));
            return url.getPath() + "?" + formatUrlMap(paramsMap);
        }
        return url.getPath();
    }

    public static String formatUrlMap(Map<String, String> paramsMap) throws UnsupportedEncodingException {
        String buff;
        List<Map.Entry<String, String>> paramsList = new ArrayList<>(paramsMap.entrySet());
        paramsList.sort(Map.Entry.comparingByKey());
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> item : paramsList) {
            if (item.getKey() != null && item.getKey().length() > 0) {
                String key = item.getKey();
                String val = item.getValue();
                String decodeVal = URLDecoder.decode(val, "utf-8");
                buf.append(key).append("=").append(decodeVal);
                buf.append("&");
            }
        }
        buff = buf.toString();
        if (!buff.isEmpty()) {
            buff = buff.substring(0, buff.length() - 1);
        }
        return buff;
    }

    private byte[] hmac_sha256(String source, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac_sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        hmac_sha256.init(secretKey);
        return hmac_sha256.doFinal(source.getBytes());
    }
}
