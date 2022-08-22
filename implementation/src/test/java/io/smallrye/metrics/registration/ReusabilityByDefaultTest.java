/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.smallrye.metrics.registration;

import static org.junit.Assert.assertEquals;

import java.time.Duration;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricFilter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.smallrye.metrics.SharedMetricRegistries;

/**
 * Verify that programmatically created metrics can be reused.
 */
public class ReusabilityByDefaultTest {

    private final MetricRegistry registry = SharedMetricRegistries.getOrCreate(MetricRegistry.APPLICATION_SCOPE);

    @After
    public void removeMetrics() {
        registry.removeMatching(MetricFilter.ALL);
    }

    static MeterRegistry rootRegistry;

    @BeforeClass
    public static void addRegistries() {
        rootRegistry = new SimpleMeterRegistry();
        Metrics.addRegistry(rootRegistry);
    }

    @AfterClass
    public static void cleanup() {
        Metrics.removeRegistry(rootRegistry);
    }

    @Test
    public void testCounter() {
        registry.counter("mycounter").inc(1);
        registry.counter("mycounter").inc(1);
        assertEquals(2, registry.counter("mycounter").getCount());
    }

    @Test
    public void testHistogram() {
        registry.histogram("myhistogram").update(5);
        registry.histogram("myhistogram").update(3);
        assertEquals(2, registry.histogram("myhistogram").getCount());
    }

    @Test
    public void testTimer() {
        Metadata metadata = Metadata.builder().withName("mytimer").build();
        registry.timer(metadata).update(Duration.ofNanos(5));
        registry.timer(metadata).update(Duration.ofNanos(7));
        assertEquals(2, registry.timer("mytimer").getCount());
    }

}
