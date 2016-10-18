package report;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

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
