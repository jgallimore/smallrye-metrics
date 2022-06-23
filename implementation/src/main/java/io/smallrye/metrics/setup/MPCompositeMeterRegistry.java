package io.smallrye.metrics.setup;

import org.eclipse.microprofile.metrics.MetricRegistry;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

public class MPCompositeMeterRegistry extends CompositeMeterRegistry {
    private final MetricRegistry.Type registryType;

    public MPCompositeMeterRegistry(final MetricRegistry.Type registryType) {
        super(Clock.SYSTEM);
        this.registryType = registryType;
    }

    public MPCompositeMeterRegistry(final Clock clock, final MetricRegistry.Type registryType) {
        super(clock);
        this.registryType = registryType;
    }

    public MPCompositeMeterRegistry(final Clock clock, final Iterable<MeterRegistry> registries,
            final MetricRegistry.Type registryType) {
        super(clock, registries);
        this.registryType = registryType;
    }

    public MPCompositeMeterRegistry(final Iterable<MeterRegistry> registries, final MetricRegistry.Type registryType) {
        super(Clock.SYSTEM, registries);
        this.registryType = registryType;
    }

    public MetricRegistry.Type getType() {
        return registryType;
    }

}
