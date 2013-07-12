/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   20 Sep 2010 (hornm): created
 */
package org.knime.knip.core.features.fd;

import net.imglib2.IterableInterval;
import net.imglib2.type.logic.BitType;

import org.knime.knip.core.algorithm.InplaceFFT;
import org.knime.knip.core.data.algebra.Complex;
import org.knime.knip.core.data.labeling.Signature;
import org.knime.knip.core.features.FeatureSet;
import org.knime.knip.core.features.FeatureTargetListener;
import org.knime.knip.core.features.ObjectCalcAndCache;
import org.knime.knip.core.features.SharesObjects;

/**
 * 
 * @author dietzc, University of Konstanz
 */
public class FDCentralDistanceFeatureSet implements FeatureSet, SharesObjects {

    /*
     * Complex array containing the current signature as complex values
     */
    private final Complex[] m_complexSignature;

    /*
     * The Fourier Transformed Signature
     */
    private Complex[] m_transformed;

    private final int m_numAngles;

    private Signature m_signature;

    private final double[] m_descriptor;

    private ObjectCalcAndCache m_ocac;

    /**
     * @param numAngles
     */
    public FDCentralDistanceFeatureSet(final int numAngles) {
        m_numAngles = numAngles;
        m_complexSignature = new Complex[numAngles];
        m_transformed = new Complex[numAngles];
        m_descriptor = new double[numAngles];

    }

    @FeatureTargetListener
    public void iiUpdated(final IterableInterval<BitType> interval) {
        m_signature = m_ocac.signature(interval, m_numAngles);
        for (int x = 0; x < m_signature.length(); x++) {
            m_complexSignature[x] = new Complex(m_signature.getPosAt(x), 0);
        }

        m_transformed = InplaceFFT.fft(m_complexSignature);
        final double dcMagnitude = m_transformed[0].getMagnitude();
        for (int t = 1; t < (m_transformed.length / 2); t++) {
            m_descriptor[t - 1] = (m_transformed[t].getMagnitude() / dcMagnitude);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double value(final int id) {
        return m_descriptor[id];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name(final int id) {
        return "FD:CentralDistance [" + id + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numFeatures() {
        return m_numAngles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String featureSetId() {
        return "FD Central Distance Feature Factory";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(final int id) {
        // nothing to do here

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getSharedObjectClasses() {
        return new Class[]{ObjectCalcAndCache.class};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSharedObjectInstances(final Object[] instances) {
        m_ocac = (ObjectCalcAndCache)instances[0];

    }

}
