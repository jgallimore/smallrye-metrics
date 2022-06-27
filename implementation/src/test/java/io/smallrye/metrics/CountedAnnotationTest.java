package io.smallrye.metrics;

import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.smallrye.metrics.micrometer.Backend;

@Category(FunctionalTest.class)
@RunWith(Arquillian.class)
public class CountedAnnotationTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(SimpleMeterRegistryProducer.class, CountedBusinessLogic.class)
                .addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Test
    public void test(final MetricRegistries metricRegistries, final CountedBusinessLogic countedBusinessLogic)
            throws Exception {
        Assert.assertNotNull(metricRegistries);
        Assert.assertNotNull(countedBusinessLogic);

        SimpleMeterRegistry simpleRegistry = TestHelper.getSimpleMeterRegistry(metricRegistries);

        Assert.assertNotNull(simpleRegistry);

        // check a meter is created for the @Timed bean method
        final Search search = simpleRegistry.find("io.smallrye.metrics.CountedAnnotationTest$CountedBusinessLogic.invoke");
        final Meter meter = search.meter();

        Assert.assertNotNull(meter);
        Assert.assertTrue(meter instanceof Counter);
        final Counter counter = (Counter) meter;
        Assert.assertEquals("application", counter.getId().getTag("scope"));

        // invoke some MP Metrics Annotated methods a few times
        for (int i = 0; i < 10; i++) {
            countedBusinessLogic.invoke();
        }

        Assert.assertEquals(10, counter.count(), 0.0);
    }

    public static class SimpleMeterRegistryProducer {

        @Produces
        @Backend
        public MeterRegistry produce() {
            return new SimpleMeterRegistry(key -> null, Clock.SYSTEM);
        }
    }

    public static class CountedBusinessLogic {

        @Counted
        public void invoke() throws Exception {
        }
    }
}
