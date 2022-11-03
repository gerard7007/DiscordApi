package fr.gerard.discord.api;

import com.sun.istack.internal.NotNull;
import fr.gerard.discord.exceptions.CloudflareException;
import kotlin.Pair;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class DiscordApi implements AutoCloseable {

    protected ExecutorService executor;
    protected OkHttpClient httpClient;
    protected Headers headers;
    protected String userAgent;
    protected String properties;
    protected String cookie;
    protected String fingerprint;
    protected String token;

    protected static JSONObject parseJson(ResponseBody body) throws IOException {
        if (body == null) {
            throw new NullPointerException("Response body is null");
        }

        String str = body.string();

        if (!str.startsWith("{") || !str.endsWith("}")) {
            if (str.contains("Cloudflare")) {
                throw new CloudflareException();
            } else {
                throw new RuntimeException("Unexpected response \"" + str + "\"");
            }
        }

        return new JSONObject(str);
    }

    protected Pair<Integer, JSONObject> patch(@NotNull JSONObject payload) throws IOException {
        RequestBody requestBody = RequestBody.create(payload.toString().getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder().url("https://discord.com/api/v9/auth/login")
                .headers(headers.newBuilder()
                        .set("Content-Length", requestBody.contentLength() + "")
                        .set("Content-Type", "application/json")
                        .set("X-Fingerprint", fingerprint)
                        .build()
                ).patch(requestBody).build();

        try (Response response = httpClient.newCall(request).execute(); ResponseBody responseBody = response.body()) {
            return new Pair<>(response.code(), parseJson(responseBody));
        }
    }

    public Pair<Integer, JSONObject> check(@NotNull String token) throws IOException {
        Request request = new Request.Builder().url("https://discord.com/api/v9/users/@me")
                .headers(headers.newBuilder()
                        .set("Authorization", token)
                        .set("X-Fingerprint", fingerprint)
                        .build()
                ).get().build();

        try (Response response = httpClient.newCall(request).execute(); ResponseBody responseBody = response.body()) {
            return new Pair<>(response.code(), parseJson(responseBody));
        }
    }

    @Override
    public void close() {
        executor = null;
        httpClient = null;
        headers = null;
        userAgent = null;
        properties = null;
        cookie = null;
        fingerprint = null;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
