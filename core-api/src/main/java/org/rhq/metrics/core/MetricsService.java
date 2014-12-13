package org.rhq.metrics.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Interface that defines the functionality of the Metrics Service.
 * @author Heiko W. Rupp
 */
public interface MetricsService {

    // For now we will use a default or fake tenant id until we get APIs in place for
    // creating tenants.
    String DEFAULT_TENANT_ID = "test";

    /** called to start the service up if needed
     * @param params from e.g. servlet context */
    void startUp(Map<String, String> params);

    /**
     * Startup with a given cassandra session
     * @param session
     */
    void startUp(Session session);

    void shutdown();

    /**
     * <p>
     * This method should be call before ever inserting any data to ensure that the tenant id is unique and to establish
     * any global configuration for data retention and for pre-computed aggregates. An exception is thrown if a tenant
     * with the same id already exists.
     * </p>
     * <p>
     * All data ia associated with a {@link org.rhq.metrics.core.Tenant tenant} via the tenant id; however, the foreign
     * key like relationship is not enforced. Data can be inserted with a non-existent tenant id. More importantly,
     * data could be inserted with a tenant id that already exists.
     * </p>
     *
     * @param tenant The {@link org.rhq.metrics.core.Tenant tenant} to create
     * @return
     * @throws org.rhq.metrics.core.TenantAlreadyExistsException
     */
    ListenableFuture<Void> createTenant(Tenant tenant);

    ListenableFuture<List<Tenant>> getTenants();

    ListenableFuture<Void> createMetric(Metric metric);

    ListenableFuture<Metric> findMetric(String tenantId, MetricType type, MetricId id);

    ListenableFuture<List<Metric>> findMetrics(String tenantId, MetricType type);

    ListenableFuture<Void> updateMetadata(Metric metric, Map<String, String> metadata, Set<String> deletions);

    ListenableFuture<Void> addNumericData(List<NumericMetric2> metrics);

    ListenableFuture<NumericMetric2> findNumericData(NumericMetric2 metric, long start, long end);

    /** Find and return raw metrics for {id} that have a timestamp between {start} and {end} */
    ListenableFuture<List<NumericData>> findData(NumericMetric2 metric, long start, long end);

    ListenableFuture<Void> addAvailabilityData(List<AvailabilityMetric> metrics);

    ListenableFuture<AvailabilityMetric> findAvailabilityData(AvailabilityMetric metric, long start, long end);

    ListenableFuture<Void> updateCounter(Counter counter);

    ListenableFuture<Void> updateCounters(Collection<Counter> counters);

    ListenableFuture<List<Counter>> findCounters(String group);

    ListenableFuture<List<Counter>> findCounters(String group, List<String> counterNames);

    /** Check if a metric with the passed {id} has been stored in the system */
    ListenableFuture<Boolean> idExists(String id);

    /** Return a list of all metric names */
    ListenableFuture<List<String>> listMetrics();

    /** Delete the metric with the passed id */
    ListenableFuture<Boolean> deleteMetric(String id);

    ListenableFuture<List<NumericData>> tagNumericData(NumericMetric2 metric, Set<String> tags, long start, long end);

    ListenableFuture<List<Availability>> tagAvailabilityData(AvailabilityMetric metric, Set<String> tags, long start,
        long end);

    ListenableFuture<List<NumericData>> tagNumericData(NumericMetric2 metric, Set<String> tags, long timestamp);

    ListenableFuture<List<Availability>> tagAvailabilityData(AvailabilityMetric metric, Set<String> tags,
        long timestamp);

    ListenableFuture<Map<MetricId, Set<NumericData>>> findNumericDataByTags(String tenantId, Set<String> tags);

    ListenableFuture<Map<MetricId, Set<Availability>>> findAvailabilityByTags(String tenantId, Set<String> tags);
}
