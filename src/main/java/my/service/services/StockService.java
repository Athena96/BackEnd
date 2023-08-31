package my.service.services;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class StockService {

    public static Double getPriceForStock(String ticker) {
        System.out.println("getPriceForStock");
        String url = "https://query2.finance.yahoo.com/v8/finance/chart/" + ticker + "?interval=1d&range=1d";
        System.out.println("url " + url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                Double price = jsonObject.getJSONObject("chart")
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getJSONObject("meta")
                        .getDouble("regularMarketPrice");
                return price;
            }
            throw new Exception("No entity found");
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
