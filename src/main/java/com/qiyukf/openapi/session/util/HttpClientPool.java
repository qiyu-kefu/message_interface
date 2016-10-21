package com.qiyukf.openapi.session.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;

/**
 * Created by zhoujianghua on 2016/10/20.
 */
public class HttpClientPool {

    private static final int FORWARD_FILE_STEP = 128 * 1024;
    private ThreadLocal<byte[]> forwardArray = new ThreadLocal<>();

    private CloseableHttpClient client = client();

    public static HttpClientPool getInstance() {
        return InstanceHolder.instance;
    }

    public String post(String url, String content) throws IOException {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = client.execute(post);
            return readResponse(response);
        } finally {
            post.releaseConnection();
        }
    }

    public JSONObject postJson(String url, String content) throws IOException {
        return JSONObject.parseObject(post(url, content));
    }

    public String get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);

            return readResponse(response);
        } finally {
            get.releaseConnection();
        }
    }

    public String uploadFile(String url, String path) throws IOException {
        HttpPost post = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody fileBody = new FileBody(new File(path)); //image should be a String
            builder.addPart("file", fileBody);
            post.setEntity(builder.build());

            CloseableHttpResponse response = client.execute(post);
            return readResponse(response);
        } finally {
            post.releaseConnection();
        }
    }

    public byte[] downloadFile(String url, int timeout) throws IOException {
        HttpGet get = new HttpGet(url);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).build();
            get.setConfig(requestConfig);

            HttpResponse response = client.execute(get);
            StatusLine sl = response.getStatusLine();

            if (sl.getStatusCode() == HttpStatus.SC_OK) {
                return readResponseContent(response);
            } else {
                EntityUtils.consume(response.getEntity());
                return null;
            }
        } finally {
            get.releaseConnection();
        }
    }

    private String readResponse(HttpResponse response) throws IOException {
        InputStream is = null;
        try {
            is = response.getEntity().getContent();
            return StringUtil.isToString(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] readResponseContent(HttpResponse response) throws IOException {
        int length = FORWARD_FILE_STEP;
        Header lenHeader = response.getFirstHeader("Content-Length");
        if (lenHeader != null) {
            length = Integer.parseInt(lenHeader.getValue());
        }

        InputStream input = response.getEntity().getContent();
        try {
            ByteBuffer buffer = ByteBuffer.allocate(length);
            int offset = 0, step = 0;
            // 先直接读一遍
            while (offset < length && (step = input.read(buffer.array(), offset, length - offset)) >= 0) {
                offset += step;
            }
            buffer.position(offset);
            if (buffer.remaining() == 0) {
                // 再看还有没有
                byte[] tmp = forwardArray.get();
                if (tmp == null) {
                    tmp = new byte[FORWARD_FILE_STEP];
                    forwardArray.set(tmp);
                }
                int len = 0;
                while ((len = input.read(tmp, 0, FORWARD_FILE_STEP)) > 0) {
                    int increment = (len == FORWARD_FILE_STEP ? FORWARD_FILE_STEP * 4 : len);
                    ensureCapacity(buffer, len, increment);
                    buffer.put(tmp, 0, len);
                }
            }
            if (buffer.remaining() == 0) {
                return buffer.array();
            } else {
                byte[] tmp = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, tmp, 0, buffer.position());
                return tmp;
            }
        } finally {
            try {
                input.close();
            } catch (Exception ignored) {

            }
        }
    }

    private static ByteBuffer ensureCapacity(ByteBuffer buffer, int requirement, int increment) throws BufferOverflowException {
        if (buffer.remaining() >= requirement) {
            return buffer;
        }

        int newCapacity = buffer.capacity() + increment;

        ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static CloseableHttpClient client() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(100);
        cm.setMaxTotal(400);

        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        cm.setDefaultConnectionConfig(connectionConfig);

        return HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }

    private static class InstanceHolder {
        private static HttpClientPool instance = new HttpClientPool();
    }
}
