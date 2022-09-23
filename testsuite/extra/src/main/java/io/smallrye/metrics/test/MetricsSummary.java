/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.metrics.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryScope;

@ApplicationScoped
public class MetricsSummary {

    @Inject

    @RegistryScope(scope = MetricRegistry.BASE_SCOPE)
    private MetricRegistry baseMetrics;

    @Inject
    @RegistryScope(scope = MetricRegistry.VENDOR_SCOPE)
    private MetricRegistry vendorMetrics;

    @Inject
    @RegistryScope(scope = MetricRegistry.APPLICATION_SCOPE)
    private MetricRegistry appMetrics;

    @Inject
    private MetricRegistry defaultMetrics;

    public MetricRegistry getBaseMetrics() {
        return baseMetrics;
    }

    public MetricRegistry getVendorMetrics() {
        return vendorMetrics;
    }

    public MetricRegistry getAppMetrics() {
        return appMetrics;
    }

    public MetricRegistry getDefaultMetrics() {
        return defaultMetrics;
    }

}
