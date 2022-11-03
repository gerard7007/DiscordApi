package fr.gerard.discord.api;

import fr.gerard.discord.util.Utils;
import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Discord extends DiscordApi {

    private static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient.Builder()
            .addInterceptor(BrotliInterceptor.INSTANCE)
            .build();

    public static class Builder {

        private ExecutorService executor;
        private OkHttpClient httpClient;
        private Headers headers;
        private String userAgent;
        private String properties;
        private String cookie;
        private String fingerprint;
        private String token;

        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder htttpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder properties(String properties) {
            this.properties = properties;
            return this;
        }

        public Builder cookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public Builder fingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Discord build() throws IOException {
            if (executor == null) executor = Executors.newCachedThreadPool();
            if (httpClient == null) httpClient = DEFAULT_CLIENT;
            if (userAgent == null) userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:105.0) Gecko/20100101 Firefox/105.0";

            if (properties == null) {
                String os = "Windows";
                String browser = "Firefox";
                String systemLocale = "en-US";
                String browserVersion = "105.0";
                String osVersion = "10";
                int buildNumber = 149043;

                properties = Base64.getEncoder().encodeToString(("{" +
                        "\"os\":\"" + os + "\"," +
                        "\"browser\":\"" + browser + "\"," +
                        "\"device\":\"\"," +
                        "\"system_locale\":\"" + systemLocale + "\"," +
                        "\"browser_user_agent\":\"" + userAgent + "\"," +
                        "\"browser_version\":\"" + browserVersion + "\"," +
                        "\"os_version\":\"" + osVersion + "\"," +
                        "\"referrer\":\"\",\"referring_domain\":\"\",\"referrer_current\":\"\",\"referring_domain_current\":\"\"," +
                        "\"release_channel\":\"stable\"," +
                        "\"client_build_number\":" + buildNumber + "," +
                        "\"client_event_source\":null" +
                        "}"
                ).getBytes(StandardCharsets.UTF_8));
            }

            if (headers == null) {
                headers = new Headers.Builder()
                        .set("Accept", "*/*")
                        // .set("Accept-Encoding", "gzip, deflate, br")
                        .set("Accept-Language", "en-US,en;q=0.5")
                        .set("Cache-Control", "no-cache")
                        .set("Connection", "keep-alive")
                        .set("DNT", "1")
                        .set("Host", "discord.com")
                        .set("Pragma", "no-cache")
                        .set("Referer", "https://discord.com/login")
                        .set("Sec-Fetch-Dest", "empty")
                        .set("Sec-Fetch-Mode", "cors")
                        .set("Sec-Fetch-Site", "same-origin")
                        .set("User-Agent", userAgent)
                        .set("X-Debug-Options", "bugReporterEnabled")
                        .set("X-Discord-Locale", "en-US")
                        .set("X-Super-Properties", properties)
                        .build();
            }

            if (fingerprint == null) {
                Request request = new Request.Builder().url("https://discord.com/api/v9/experiments?with_guild_experiments=true")
                        .headers(headers.newBuilder()
                                .set("X-Context-Properties", "eyJsb2NhdGlvbiI6IkxvZ2luIn0=")
                                .build()
                        ).get().build();

                try (Response response = httpClient.newCall(request).execute()) {
                    cookie = Utils.formatCookie(response.headers("set-cookie"));
                    headers = headers.newBuilder().set("Cookie", cookie).set("TE", "trailers").build();

                    try (ResponseBody responseBody = response.body()) {
                        JSONObject json = parseJson(responseBody);
                        fingerprint = json.getString("fingerprint");
                    }
                }
            }

            Discord discord = new Discord();
            discord.setExecutor(executor);
            discord.setHttpClient(httpClient);
            discord.setHeaders(headers);
            discord.setUserAgent(userAgent);
            discord.setProperties(properties);
            discord.setCookie(cookie);
            discord.setFingerprint(fingerprint);
            discord.setToken(token);
            return discord;
        }

    }
}
