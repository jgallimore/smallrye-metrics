package io.smallrye.metrics;

import java.util.Set;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.junit.Assert;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.smallrye.metrics.legacyapi.LegacyMetricRegistryAdapter;
import io.smallrye.metrics.setup.MPCompositeMeterRegistry;

public class TestHelper {
    public static SimpleMeterRegistry getSimpleMeterRegistry(MetricRegistries metricRegistries) {
        // check the MetricRegistry is correctly created with CDI
        final MetricRegistry applicationRegistry = metricRegistries.getApplicationRegistry();
        Assert.assertNotNull(applicationRegistry);

        Assert.assertTrue(applicationRegistry instanceof LegacyMetricRegistryAdapter);
        final LegacyMetricRegistryAdapter legacyMetricRegistryAdapter = (LegacyMetricRegistryAdapter) applicationRegistry;
        final MeterRegistry meterRegistry = legacyMetricRegistryAdapter.getPrometheusMeterRegistry(); // this method maybe needs a rename

        // Check a CompositeMeterRegistry was created - this should have a Prometheus registry and a SimpleMeterRegistry
        Assert.assertTrue(meterRegistry instanceof MPCompositeMeterRegistry);
        final MPCompositeMeterRegistry compositeMeterRegistry = (MPCompositeMeterRegistry) meterRegistry;
        final Set<MeterRegistry> registries = compositeMeterRegistry.getRegistries();

        SimpleMeterRegistry simpleRegistry = null;

        for (final MeterRegistry registry : registries) {
            if (registry instanceof SimpleMeterRegistry) {
                simpleRegistry = (SimpleMeterRegistry) registry;
                break;
            }
        }
        return simpleRegistry;
    }
}
