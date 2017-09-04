package common;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by hx-pc on 17-5-25.
 */
public class HttpClientUtil {

    private static final Gson gson = new GsonBuilder().create();

    public static  <T> T get(String url, Map<String, Object> param, Class<T> responseClass) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = getUriBuild(url);
        if (param != null) {
            for (Map.Entry entry : param.entrySet()) {
                uriBuilder = uriBuilder.setParameter(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return doRequest(responseClass, httpGet);
    }

    public static <T> T post(String url, Map<String, Object> param, Class<T> responseClass) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = getUriBuild(url);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        if (param != null) {
            StringEntity stringEntity = new StringEntity(gson.toJson(param), Charset.defaultCharset());
            stringEntity.setContentType("application/json;charset=UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            httpPost.setEntity(stringEntity);
        }
        return doRequest(responseClass, httpPost);
    }

    public static <T> T delete(String url, Map<String, Object> param, Class<T> responseClass) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = getUriBuild(url);
        if (param != null) {
            for (Map.Entry entry : param.entrySet()) {
                uriBuilder = uriBuilder.setParameter(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        return doRequest(responseClass, httpDelete);
    }

    public static <T> T put(String url, Map<String, Object> param, Class<T> responseClass) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = getUriBuild(url);
        HttpPut httpPut = new HttpPut(uriBuilder.build());
        if (param != null) {
            StringEntity stringEntity = new StringEntity(gson.toJson(param), Charset.defaultCharset());
            stringEntity.setContentType("application/json;charset=UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            httpPut.setEntity(stringEntity);
        }
        return doRequest(responseClass, httpPut);
    }

    private static <T> T doRequest(Class<T> responseClass, HttpRequestBase httpRequestBase) throws IOException, URISyntaxException {
        CloseableHttpClient httpclient = getHttpClient();
        httpRequestBase = buildHeader(httpRequestBase);
        return getResponse(responseClass, httpclient, httpRequestBase);
    }

    private static HttpRequestBase buildHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.addHeader("Content-Type","application/json;charset=UTF-8");
        return httpRequestBase;
    }

    private static CloseableHttpClient getHttpClient() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(1000)
                .setSocketTimeout(120*1000)
                .build();
        return HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig).build();
    }

    private static <T> T getResponse(Class<T> responseClass, CloseableHttpClient httpclient, HttpRequestBase httpRequestBase) throws IOException {
        CloseableHttpResponse response = httpclient.execute(httpRequestBase);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        EntityUtils.consume(entity);
        response.addHeader("Content-Type","application/json;charset=UTF-8");
        response.close();
        return gson.fromJson(body, responseClass);
    }

    private static URIBuilder getUriBuild(String url) throws URISyntaxException {
        return new URIBuilder().setScheme("http").setHost("139.196.171.215").setPort(6060).setPath(url);
    }
}
