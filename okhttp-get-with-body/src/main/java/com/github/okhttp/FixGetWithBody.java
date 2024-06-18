package com.github.okhttp;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Field;

public class FixGetWithBody {

    public static final String HEAD_KEY = "GetWithBody";

    public static final String HEAD_VALUE = "true";


    public static class GetRequestBody extends okhttp3.Request.Builder {

        /**
         * 实现get请求带body的方法，先通过原有post方法传递body参数，然后通过反射修改method字段绕过校验
         *
         * @param body
         * @return Builder
         **/
        public okhttp3.Request.Builder get(RequestBody body) {
            this.post(body);
            try {
                Field field = okhttp3.Request.Builder.class.getDeclaredField("method");
                field.setAccessible(true);
                field.set(this, HttpMethod.GET.name());
            } catch (Exception e) {
            }
            return this;
        }
    }

    public static EventListener getEventListener() {
        return new CustomEventListenerImpl();
    }

    public static class CustomEventListenerImpl extends EventListener {
        /**
         * 发送完成后需要将method修改回post，否则后续请求会校验失败
         *
         * @param call
         * @param request
         * @return void
         **/
        public void requestHeadersEnd(Call call, Request request) {
            if (HEAD_VALUE.equals(request.header(HEAD_KEY))) {
                try {
                    Field field = Request.class.getDeclaredField("method");
                    field.setAccessible(true);
                    field.set(request, HttpMethod.POST.name());
                } catch (Exception ignored) {
                }
            }
        }
    }
}
