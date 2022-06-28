package io.smallrye.metrics;

import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class TestHelper {
    public static SimpleMeterRegistry getSimpleMeterRegistry() {
        // check the MetricRegistry is correctly created with CDI
        final Set<MeterRegistry> registries = Metrics.globalRegistry.getRegistries();

        // Check a CompositeMeterRegistry was created - this should have a Prometheus registry and a SimpleMeterRegistry
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
