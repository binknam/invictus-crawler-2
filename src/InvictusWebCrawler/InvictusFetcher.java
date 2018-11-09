package InvictusWebCrawler;

import edu.uci.ics.crawler4j.fetcher.SniPoolingHttpClientConnectionManager;
import edu.uci.ics.crawler4j.fetcher.SniSSLConnectionSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;

public class InvictusFetcher {
  public static int timeout = 60000;
  public static String userAgent = "Invictus Crawler";

  protected CloseableHttpClient httpClient;
  protected PoolingHttpClientConnectionManager connectionManager;


  public InvictusFetcher() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    RequestConfig requestConfig = RequestConfig.custom()
        .setExpectContinueEnabled(false)
        .setCookieSpec(CookieSpecs.STANDARD)
        .setRedirectsEnabled(false)
        .setSocketTimeout(timeout)
        .setConnectTimeout(timeout)
        .build();

    RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
    connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);

    SSLContext sslContext =
        SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
          @Override
          public boolean isTrusted(final X509Certificate[] chain, String authType) {
            return true;
          }
        }).build();

    SSLConnectionSocketFactory sslsf =
        new SniSSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    connRegistryBuilder.register("https", sslsf);

    Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();


    connectionManager = new SniPoolingHttpClientConnectionManager(connRegistry);
    connectionManager.setMaxTotal(60000);
    connectionManager.setDefaultMaxPerRoute(60000);

    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder.setDefaultRequestConfig(requestConfig);
    clientBuilder.setConnectionManager(connectionManager);
    clientBuilder.setUserAgent(userAgent);
    Collection<BasicHeader> defaultHeaders = new HashSet<BasicHeader>();
    clientBuilder.setDefaultHeaders(defaultHeaders);
    httpClient = clientBuilder.build();
  }

  public BufferedReader getBufferedReaderFromUrl(String url) throws IOException {
    HttpGet request = new HttpGet(url);
    HttpResponse response = execute(request);
    if (response.getStatusLine().getStatusCode() > 299 || response.getStatusLine().getStatusCode() < 200) {
      throw new IOException();
    }

    InputStream is = response.getEntity().getContent();

    return new BufferedReader(new InputStreamReader(is, "UTF-8"));
  }

  public HttpResponse execute(HttpGet request) throws IOException {
    return httpClient.execute(request);
  }

}
