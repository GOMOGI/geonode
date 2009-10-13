/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geonode.process;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.Parameter;
import org.geotools.feature.NameImpl;
import org.geotools.process.Process;
import org.geotools.process.impl.SingleProcessFactory;
import org.geotools.text.Text;
import org.opengis.util.InternationalString;

import com.vividsolutions.jts.geom.Geometry;

/**
 * <p>
 * Inputs:
 * <ul>
 * <li>{@code radius}: {@link Integer}. Buffer radius around the input geometry to create the
 * intersection query geometry to match the {@code datalayers} with.
 * <li>{@code geometry}: {@link Geometry},
 * <li>{@code datalayers:} {@link List List&lt;String&gt;}, names of the layers to intersect with
 * the buffered input geometry
 * </ul>
 * </p>
 */
public class HazardStatisticsFactory extends SingleProcessFactory {

    private static final String PROCESS_NAMESPACE = "hazard";

    private static final String PROCESS_NAME = "Statistics";

    /** Geometry for operation. Required. */
    public static final Parameter<Geometry> GEOMERTY = new Parameter<Geometry>("geometry",
            Geometry.class, Text.text("Geometry"), Text.text("Geometry to buffer"));

    /** Buffer amount. Required. */
    public static final Parameter<Double> RADIUS = new Parameter<Double>("radius", Double.class,
            Text.text("Buffer Amount"), Text.text("Amount to buffer the geometry by"));

    /** Layer names. Required. Cardinality is 1..* */
    public static final Parameter<AbstractGridCoverage2DReader> DATALAYER = new Parameter<AbstractGridCoverage2DReader>(
            "datalayer", AbstractGridCoverage2DReader.class, Text.text("Data layers"), Text
                    .text("List of datalayers to query"), true, 1, -1, null, null);

    @SuppressWarnings("unchecked")
    public static final Parameter<FeatureSource> POLITICAL_LAYER = new Parameter<FeatureSource>(
            "politicalLayer", FeatureSource.class, Text.text("Political layer"), Text
                    .text("Layer to return political info about"), false, 0, 1, null, null);

    /** Layer names. Required. Cardinality is 0..* */
    public static final Parameter<String> POLITICAL_LAYER_ATTRIBUTES = new Parameter<String>(
            "politicalLayerAttributes", String.class, Text.text("Political layer attributes"),
            Text.text("Attributes to return from the political layer for intersecting features"),
            false, 0, -1, null, null);

    /**
     * Map used for getParameterInfo; used to describe operation requirements for user interface
     * creation.
     */
    private static final Map<String, Parameter<?>> prameterInfo = new TreeMap<String, Parameter<?>>();
    static {
        prameterInfo.put(GEOMERTY.key, GEOMERTY);
        prameterInfo.put(RADIUS.key, RADIUS);
        prameterInfo.put(DATALAYER.key, DATALAYER);
    }

    @SuppressWarnings("unchecked")
    public static final Parameter<Map> RESULT_STATISTICS = new Parameter<Map>("statistics",
            Map.class, Text.text("Statistics"), Text
                    .text("Aggregate result of datalayer statistics"));

    @SuppressWarnings("unchecked")
    public static final Parameter<Map> RESULT_POLITICAL = new Parameter<Map>("political",
            Map.class, Text.text("Political"), Text
                    .text("Political subdivision names where the staistics lie"));

    public static final Parameter<Geometry> RESULT_BUFER = new Parameter<Geometry>("buffer",
            Geometry.class, Text.text("Buffer"), Text
                    .text("The area the statistics are generated for"));

    /**
     * Map used to describe operation results.
     */
    static final Map<String, Parameter<?>> resultInfo = new TreeMap<String, Parameter<?>>();

    static {
        resultInfo.put(RESULT_STATISTICS.key, RESULT_STATISTICS);
        resultInfo.put(RESULT_POLITICAL.key, RESULT_POLITICAL);
        resultInfo.put(RESULT_BUFER.key, RESULT_BUFER);
    }

    public HazardStatisticsFactory() {
        super(new NameImpl(PROCESS_NAMESPACE, PROCESS_NAME));
    }

    @Override
    public InternationalString getDescription() {
        return Text
                .text("Intersect given data layers with given geometry and buffer radius and produce per data layer statistics");
    }

    @Override
    public Map<String, Parameter<?>> getParameterInfo() {
        return Collections.unmodifiableMap(prameterInfo);
    }

    @Override
    public Map<String, Parameter<?>> getResultInfo(Map<String, Object> parameters)
            throws IllegalArgumentException {
        return Collections.unmodifiableMap(resultInfo);
    }

    @Override
    public InternationalString getTitle() {
        // please note that this is a title for display purposes only
        // finding an specific implementation by name is not possible
        return Text.text("Hazard Statistics");
    }

    /**
     * @return {@code true}
     */
    @Override
    public boolean supportsProgress() {
        return true;
    }

    @Override
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public Process create() throws IllegalArgumentException {
        return new HazardStatistics(this);
    }
}