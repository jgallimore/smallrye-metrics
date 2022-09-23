package io.smallrye.metrics.legacyapi;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.metrics.Snapshot;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.smallrye.metrics.SharedMetricRegistries;

class TimerAdapter implements org.eclipse.microprofile.metrics.Timer, MeterHolder {

    private final static int PRECISION;

    /*
     * Due to multiple Prometheus meter registries being registered to the global
     * composite meter registry with deny filters used, this can lead to a problem
     * when the composite meter is retrieving a value of the meter. It will chose
     * the "first" meter registry associated to the composite meter. This meter
     * registry may have returned a Noop meter (due it being denied). As a result,
     * querying this composite meter for a value can return a 0.
     * 
     * We keep acquire the Prometheus meter registry's meter and use it to retrieve
     * values. Can't just acquire the meter during value retrieval due to situation
     * where if this meter(holder) was removed from the MP shim, the application
     * code could still have reference to this object and can still perform a get
     * value calls.
     * 
     * We keep the global composite meter as this is what is "used" when we need to
     * remove this meter. The composite meter's object ref is used to remove from
     * the global composite registry.
     * 
     * See SharedMetricRegistries.java for more information.
     * 
     */

    /*
     * Increasing the percentile precision for timers will consume more memory.
     * This setting is "3" by default, and provided to adjust the precision to
     * your needs.
     */
    static {
        final Config config = ConfigProvider.getConfig();
        PRECISION = config.getOptionalValue("mp.metrics.smallrye.timer.precision", Integer.class).orElse(3);
    }

    Timer globalCompositeTimer;
    Timer promTimer;

    final MeterRegistry registry;

    TimerAdapter(MeterRegistry registry) {
        this.registry = registry;
    }

    public TimerAdapter register(MpMetadata metadata, MetricDescriptor descriptor, String scope) {

        ThreadLocal<Boolean> threadLocal = SharedMetricRegistries.getThreadLocal(scope);
        threadLocal.set(true);
        if (globalCompositeTimer == null || metadata.cleanDirtyMetadata()) {

            Set<Tag> tagsSet = new HashSet<Tag>();
            for (Tag t : descriptor.tags()) {
                tagsSet.add(t);
            }
            tagsSet.add(Tag.of(LegacyMetricRegistryAdapter.MP_SCOPE_TAG, scope));

            globalCompositeTimer = Timer.builder(descriptor.name())
                    .description(metadata.getDescription())
                    .tags(tagsSet)
                    .publishPercentiles(0.5, 0.75, 0.95, 0.98, 0.99, 0.999)
                    .percentilePrecision(PRECISION)
                    .register(Metrics.globalRegistry);
            /*
             * Due to registries that deny registration returning no-op and the chance of
             * the composite meter obtaining the no-oped meter, we need to acquire
             * Prometheus meter registry's copy of this meter/metric.
             * 
             * Save this and use it to retrieve values.
             */
            promTimer = registry.find(descriptor.name()).tags(tagsSet).timer();
            if (promTimer == null) {
                promTimer = globalCompositeTimer;
                // TODO: logging?
            }
        }
        threadLocal.set(false);

        return this;
    }

    public void update(long l, TimeUnit timeUnit) {
        globalCompositeTimer.record(l, timeUnit);
    }

    @Override
    public void update(Duration duration) {
        globalCompositeTimer.record(duration);
    }

    @Override
    public <T> T time(Callable<T> callable) throws Exception {
        return globalCompositeTimer.wrap(callable).call();
    }

    @Override
    public void time(Runnable runnable) {
        globalCompositeTimer.wrap(runnable).run();
    }

    @Override
    public SampleAdapter time() {
        return new SampleAdapter(globalCompositeTimer, Timer.start(Metrics.globalRegistry));
    }

    @Override
    public Duration getElapsedTime() {
        return Duration.ofNanos((long) promTimer.totalTime(TimeUnit.NANOSECONDS));
    }

    @Override
    public long getCount() {
        return promTimer.count();
    }

    @Override
    public Snapshot getSnapshot() {
        return new SnapshotAdapter(promTimer.takeSnapshot());
    }

    @Override
    public Meter getMeter() {
        return globalCompositeTimer;
    }

    public Timer.Sample start() {
        return Timer.start(registry);
    }

    public void stop(Timer.Sample sample) {
        sample.stop(globalCompositeTimer);
    }

    class SampleAdapter implements org.eclipse.microprofile.metrics.Timer.Context {
        final Timer timer;
        final Timer.Sample sample;

        SampleAdapter(Timer timer, Timer.Sample sample) {
            this.sample = sample;
            this.timer = timer;
        }

        @Override
        public long stop() {
            return sample.stop(timer);
        }

        @Override
        public void close() {
            sample.stop(timer);
        }
    }
}
