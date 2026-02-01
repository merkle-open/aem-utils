## Example usage

### Simple GET request

```java

import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientService;
import org.apache.http.client.methods.HttpGet;
//other imports...

@Component(service = ExampleWebClient.class, immediate = true)
public class ExampleWebClientImpl implements ExampleWebClient {

    @Reference
    private HttpClientService httpClientService;

    public String getJson() {
        try {
            final HttpGet httpGet = new HttpGet("http://example.com/api");
            final HttpClientResponse httpClientResponse = httpClientService.httpGet(httpGet);
            return httpClientResponse.asString();
        } catch (IOException e) {
            LOG.error("Unable to fetch JSON", e);
            return null;
        }

        return null;
    }
}


```

### Simple authenticated GET request

```java

import com.merkle.oss.aem.utils.constants.FileType;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientService;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
//other imports...

@Component(service = ExampleWebClient.class, immediate = true)
public class ExampleWebClientImpl implements ExampleWebClient {

    @Reference
    private HttpClientService httpClientService;

    public HttpClientResponse executeGet() throws IOException {
        final HttpGet httpGet = new HttpGet("http://example.com/api");
        httpGet.setHeader(HttpHeaders.ACCEPT, FileType.JSON.getMimeType());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, FileType.JSON.getMimeType());
        httpGet.setHeader(HttpClientUtil.buildBearerAuthenticationHeader("accessTokenValue"));

        final HttpClientResponse httpClientResponse = httpClientService.httpGet(httpGet);
        if (httpClientResponse.getStatusCode() != HttpStatus.SC_OK) {
            //handle failure
        }

        return httpClientResponse;
    }
}


```

### Verify POST request with TrustStore SSL Context

For requests which require Mutual TLS, self-signed certificates or etc., http client requests can be executed with the
context of
the [AEM TrustStore](https://experienceleague.adobe.com/en/docs/experience-manager-learn/foundation/security/call-internal-apis-having-private-certificate#httpclient-and-load-aem-truststore-material)

```java

import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientService;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
//other imports...

@Component(service = ExampleWebClient.class, immediate = true)
public class ExampleWebClientImpl implements ExampleWebClient {

    @Reference
    private HttpClientService httpClientService;

    public int verifyCaptcha() throws IOException {
        final HttpPost httpPost = new HttpPost("http://example.com/api");
        final List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("secret", "keyValue"));
        params.add(new BasicNameValuePair("solution", "solutionValue"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        final HttpClientResponse httpClientResponse = httpClientService.httpPostWithTrustStore(httpPost);
        return httpClientResponse.getStatusCode();
    }
    
}


```

### POST request with service user KeyStore SSL Context

For requests which require Mutual TLS, self-signed certificates or etc., http client requests can be executed with the
context of a
specific [Users KeyStore](https://experienceleague.adobe.com/en/docs/experience-manager-learn/foundation/security/mutual-tls-authentication#certificate-import).

```java

import com.merkle.oss.aem.utils.constants.FileType;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientService;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//other imports...

@Component(service = ExampleWebClient.class, immediate = true)
public class ExampleWebClientImpl implements ExampleWebClient {

    @Reference
    private HttpClientService httpClientService;

    public HttpClientResponse postValue() throws IOException {
        final HttpPost httpPost = new HttpPost("http://example.com/api");
        final StringEntity entity = new StringEntity("entityValue", StandardCharsets.UTF_8.toString());
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, FileType.JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, FileType.JSON.getMimeType());

        return httpClientService.httpPostWithKeyStore(httpPost, "keyStoreServiceUserId", "keyStorePassword");
    }
    
}


```
