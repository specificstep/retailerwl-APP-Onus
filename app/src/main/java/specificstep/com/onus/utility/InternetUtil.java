package specificstep.com.onus.utility;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class InternetUtil {

    public static String getServer_Data(String url) throws Exception {
        Log.i("InternetUtil", "URL : " + url);
        String server_data = "-1";
        DefaultHttpClient client = new DefaultHttpClient();
        URI uri = new URI(url);
        HttpGet method = new HttpGet(uri);
        HttpResponse res = client.execute(method);
        InputStream is = res.getEntity().getContent();
        server_data = generateString(is);
        Log.i("InternetUtil", "RESPONSE : " + server_data);
        return server_data;
    }

    // Post Response
    public static synchronized String getUrlData(String url, String parameter[], String parameterValues[])
            throws Exception {

        String response = "";
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        Log.i("InternetUtil", "URL : " + url);
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < parameterValues.length; i++) {
            Log.i("InternetUtil", parameter[i] + " : " + parameterValues[i]);
            nameValuePairs.add(new BasicNameValuePair(parameter[i], parameterValues[i]));
        }
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
        httpPost.setEntity(urlEncodedFormEntity);
        HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
        InputStream inputStream = httpResponse.getEntity().getContent();
        response = generateString(inputStream);
        Log.i("InternetUtil", "RESPONSE : " + response);

        return response;
    }

    private static String generateString(InputStream stream) {
        try {
            StringBuffer sb = new StringBuffer();
            int cur;
            while ((cur = stream.read()) != -1) {
                sb.append((char) cur);
            }
            return String.valueOf(sb);
        }
        catch (Exception e) {
            Log.e("InternetUtil", "In generateString() " + e.getMessage());
            e.printStackTrace();
            return "0";
        }
    }
}
