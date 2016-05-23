package report;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

/**
 * Created by ldb on 2016/5/12.
 */
public class histogram {

    final MetricRegistry metrics = new MetricRegistry();
    private void start(){
        /*Metric.Config
                .WithHttpEndpoint("http://localhost:1234/metrics/")
                .WithAllCounters()
                .WithInternalMetrics()
                .WithReporting(config => config.WithConsoleReport(TimeSpan.FromSeconds(30));*/
    }
}
