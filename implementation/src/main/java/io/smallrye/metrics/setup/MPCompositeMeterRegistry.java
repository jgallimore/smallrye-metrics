package io.smallrye.metrics.setup;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.logging.Logger;

import org.eclipse.microprofile.metrics.MetricRegistry;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;

public class MPCompositeMeterRegistry extends CompositeMeterRegistry {

    private static final Logger LOGGER = Logger.getLogger(MPCompositeMeterRegistry.class.getName());

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

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig,
            PauseDetector pauseDetector) {
        LOGGER.info("newTimer");
        return super.newTimer(id, distributionStatisticConfig, pauseDetector);
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig,
            double scale) {
        LOGGER.info("newDistributionSummary");
        return super.newDistributionSummary(id, distributionStatisticConfig, scale);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        LOGGER.info("newCounter");
        return super.newCounter(id);
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        LOGGER.info("newLongTaskTimer");
        return super.newLongTaskTimer(id, distributionStatisticConfig);
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction) {
        LOGGER.info("newGauge");
        return super.newGauge(id, obj, valueFunction);
    }

    @Override
    protected <T> TimeGauge newTimeGauge(Meter.Id id, T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction) {
        LOGGER.info("newTimeGauge");
        return super.newTimeGauge(id, obj, valueFunctionUnit, valueFunction);
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        LOGGER.info("newFunctionTimer");
        return super.newFunctionTimer(id, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        LOGGER.info("newFunctionCounter");
        return super.newFunctionCounter(id, obj, countFunction);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return super.getBaseTimeUnit();
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return super.defaultHistogramConfig();
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        LOGGER.info("newTimer");
        return super.newMeter(id, type, measurements);
    }

    @Override
    public CompositeMeterRegistry add(MeterRegistry registry) {
        return super.add(registry);
    }

    @Override
    public CompositeMeterRegistry remove(MeterRegistry registry) {
        return super.remove(registry);
    }

    @Override
    public Set<MeterRegistry> getRegistries() {
        return super.getRegistries();
    }

    @Override
    public void close() {
        super.close();
    }
}
