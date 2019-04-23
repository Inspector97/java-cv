package usr.afast.image;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RunTest {
    private static final String URL = "http://localhost:8080/doMagic";
    private static final String IMAGE_PATH = "E:\\test_images\\coke\\coke.jpg";

    public static void main(String[] args) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(URL);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("image", IMAGE_PATH));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        try {
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            String answer = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            System.out.println(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
