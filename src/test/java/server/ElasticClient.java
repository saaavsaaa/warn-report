//package server;
//
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.message.BasicHeader;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.junit.Test;
//
//import java.io.IOException;
//
//public class ElasticClient {
//
//    @Test
//    public void get() throws IOException {
//        RestClientBuilder builder = RestClient.builder(
//                new HttpHost("sl010a-analysisdb1", 9200, "https"),
//                new HttpHost("sl010a-analysisdb2", 9200, "https"),
//                new HttpHost("sl010a-analysisdb3", 9200, "https")
//        );
//        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", "Basic YWRtaW46YWRtaW4=")};
//        builder.setDefaultHeaders(defaultHeaders);
//        RestClient restClient = builder.build();
//        RestHighLevelClient client = new RestHighLevelClient(restClient);
//
//        GetRequest getRequest = new GetRequest("test-index", "test-all", "26269");
//        GetResponse getResponse = client.get(getRequest);
//        System.out.println(getResponse.getSourceAsString());
//    }
//}
