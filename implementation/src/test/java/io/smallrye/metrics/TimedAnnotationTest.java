package io.smallrye.metrics;

import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.metrics.annotation.Timed;
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

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.smallrye.metrics.micrometer.Backend;

@RunWith(Arquillian.class)
@Category(FunctionalTest.class)
public class TimedAnnotationTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(SimpleMeterRegistryProducer.class, TimerBusinessLogic.class)
                .addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Test
    public void test(final TimerBusinessLogic timerBusinessLogic) throws Exception {
        Assert.assertNotNull(timerBusinessLogic);

        SimpleMeterRegistry simpleRegistry = TestHelper.getSimpleMeterRegistry();

        Assert.assertNotNull(simpleRegistry);

        // check a meter is created for the @Timed bean method
        final Search search = simpleRegistry.find("io.smallrye.metrics.TimedAnnotationTest$TimerBusinessLogic.invoke");
        final Meter meter = search.meter();

        Assert.assertNotNull(meter);
        Assert.assertTrue(meter instanceof Timer);
        final Timer timer = (Timer) meter;
        //Assert.assertEquals("application", timer.getId().getTag("scope"));

        // invoke some MP Metrics Annotated methods a few times
        for (int i = 0; i < 10; i++) {
            timerBusinessLogic.invoke();
        }

        Assert.assertEquals(10, timer.count());
        Assert.assertEquals(1, timer.totalTime(TimeUnit.SECONDS), 0.1);
    }

    public static class SimpleMeterRegistryProducer {

        @Produces
        @Backend
        public MeterRegistry produce() {
            return new SimpleMeterRegistry(key -> null, Clock.SYSTEM);
        }
    }

    public static class TimerBusinessLogic {

        @Timed
        public void invoke() throws Exception {
            Thread.sleep(100); // do some work!
        }
    }
}
