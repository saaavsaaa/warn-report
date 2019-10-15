package server;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;

public class ElasticClient {

    @Test
    public void get() throws IOException {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("sl010a-analysisdb1", 9200, "https"),
                new HttpHost("sl010a-analysisdb2", 9200, "https"),
                new HttpHost("sl010a-analysisdb3", 9200, "https")
        );

        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", "Basic YWRtaW46YWRtaW4=")};
        builder.setDefaultHeaders(defaultHeaders);
        RestClient restClient = builder.build();
        RestHighLevelClient client = new RestHighLevelClient(restClient);

        GetRequest getRequest = new GetRequest("test-index", "test-all", "26269");
        GetResponse getResponse = client.get(getRequest);
        System.out.println(getResponse.getSourceAsString());
    }

    @Test
    public void search() throws IOException {

        String elasticsearchHost = "sl010a-analysisdb1:9200,sl010a-analysisdb2:9200,sl010a-analysisdb3:9200";
        String[] elasticHosts = elasticsearchHost.split(",");
        HttpHost[] httpHosts = new HttpHost[elasticHosts.length];
        for(int i = 0; i < elasticHosts.length; i++) {
            String ip = elasticHosts[i].split(":")[0];
            String port = elasticHosts[i].split(":")[1];
            httpHosts[i] = new HttpHost(ip, Integer.parseInt(port), "https");
        }
        RestClient restClient = RestClient.builder(httpHosts)
                .setDefaultHeaders(new Header[] {new BasicHeader("Authorization", "Basic Q049ZGVtb3VzZXI6Q049ZGVtb3VzZXI=")})
                .build();
        RestHighLevelClient client = new RestHighLevelClient(restClient);

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        System.out.println(searchResponse.getTotalShards());
    }
}
