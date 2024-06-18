package com.github;

import com.github.okhttp.FixGetWithBody;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetWithBodyTests {

    @LocalServerPort
    private int port;

    private String url;

    private OkHttpClient client;

    @BeforeEach
    public void setUp() {
        url = "http://localhost:" + port + "/hello";
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .followRedirects(false)
                // 添加我们自定义的listener
                .eventListener(FixGetWithBody.getEventListener())
                .build();
        client.dispatcher().setMaxRequestsPerHost(1000);
        client.dispatcher().setMaxRequests(1000);
    }

    /**
     * 正常发送，直接抛出异常method GET must not have a request body
     */
    @Test
    public void testGetWithBodyThrowException() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> send(url, HttpMethod.GET, createBody()));
        assertEquals("method GET must not have a request body.", exception.getMessage());
    }

    /**
     * 修复后的发送
     */
    @Test
    public void testGetWithBodyFix() {
        Response response;
        try {
            response = sendFix(url, HttpMethod.GET, createBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(response.code(), 200);
    }

    /**
     * 正常发送请求
     *
     * @param url
     * @param method
     * @param body
     * @return Response
     **/
    private Response send(String url, HttpMethod method, RequestBody body) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .method(method.name(), body)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * 修复后的发送方法，如果是GET请求带body，则使用自定义的FixGetWithBody，并添加header标记
     *
     * @param url
     * @param method
     * @param body
     * @return Response
     **/
    private Response sendFix(String url, HttpMethod method, RequestBody body) throws Exception {
        Headers.Builder headerBuilder = new Headers.Builder();
        Request.Builder requestBuilder;
        if (StringUtils.equalsAnyIgnoreCase("GET", method.name()) && body != null) {
            requestBuilder = new FixGetWithBody.GetRequestBody().get(body);
            headerBuilder.add(FixGetWithBody.HEAD_KEY, FixGetWithBody.HEAD_VALUE);
        } else {
            requestBuilder = new Request.Builder().method(method.name(), body);
        }
        Request request = requestBuilder.url(url).headers(headerBuilder.build()).build();
        return client.newCall(request).execute();
    }


    private RequestBody createBody() {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = """
                {
                    "name": "张三"
                }
                """;
        return RequestBody.create(mediaType, json);
    }
}
