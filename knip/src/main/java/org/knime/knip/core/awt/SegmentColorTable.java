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
 *   24 Feb 2011 (hornm): created
 */
package org.knime.knip.core.awt;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.apache.mahout.math.map.OpenIntIntHashMap;

/**
 * 
 * @author hornm, University of Konstanz
 */
public final class SegmentColorTable {

    // Color definitions for Hilite
    public static final Color HILITED = Color.ORANGE;

    public static final int HILITED_RGB = HILITED.getRGB();

    public static final Color HILITED_SELECTED = new Color(255, 240, 204);

    public static final Color STANDARD = new Color(240, 240, 240);

    public static final Color SELECTED = new Color(179, 168, 143);

    public static final Color NOTSELECTED = Color.LIGHT_GRAY;

    public static final int NOTSELECTED_RGB = NOTSELECTED.getRGB();

    /** color that is used for bounding boxes e.g. for label bbs. */
    private static Color m_boundingBoxColor = Color.YELLOW;

    /**
     * counts from 0 on the incarnation nr of the color table (i.e. the color table is reset and newly build =>
     * colorMapNr++)
     */
    private static int m_colorMapNr;

    // Fast HashMap implementation
    private static OpenIntIntHashMap m_colorTable = new OpenIntIntHashMap();

    /**
     * Assigns a color to the given id.
     * 
     * @param segmentId
     * @return the color
     */
    public static <L extends Comparable<L>> int getColor(final L label) {
        return getColor(label, 255);
    }

    public static int getTransparentRGBA(final int rgb, final int transparency) {
        return (transparency << 24) | (rgb & 0x00FFFFFF);
    }

    public static Color getBoundingBoxColor() {
        return m_boundingBoxColor;
    }

    /**
     * @return {@link SegmentColorTable#m_colorMapNr}
     */
    public static int getColorMapNr() {
        return m_colorMapNr;
    }

    /**
     * resets the color table such that the label colors can be assigned again. Increases the ColorMapNr to indicate the
     * change.
     */
    public static void resetColorMap() {
        m_colorTable.clear();
        m_colorMapNr++;
    }

    /**
     * Assigns a color to the given id.
     * 
     * @param segmentId
     * @return the color
     */
    public static <L extends Comparable<L>> int getColor(final L label, final int transparency) {

        final int hashCode = label.hashCode();
        int res = m_colorTable.get(hashCode);
        if (res == 0) {
            res = randomColor();
            m_colorTable.put(hashCode, res);

        }
        return getTransparentRGBA(res, transparency);
    }

    /**
     * Assigns a color to the given id.
     * 
     * @param segmentId
     * @return the color
     */
    public static <L extends Comparable<L>> int getColor(final List<L> labels, final int transparency) {

        double totalRes = 0;
        for (int i = 0; i < labels.size(); i++) {
            totalRes += (double)getColor(labels.get(i)) / (double)labels.size();
        }

        return getTransparentRGBA((int)totalRes, transparency);
    }

    /**
     * Assigns a color to the given id.
     * 
     * @param segmentId
     * @return the color
     */
    public static <L extends Comparable<L>> int getColor(final List<L> labels) {

        return getColor(labels, 255);
    }

    public static <L extends Comparable<L>> void resetColor(final L o) {
        m_colorTable.put(o.hashCode(), randomColor());

    }

    public static int randomColor() {
        final Random rand = new Random();
        int col = rand.nextInt(255);
        col = col << 8;
        col |= rand.nextInt(255);
        col = col << 8;
        col |= rand.nextInt(255);

        if (col == 0) {
            col = randomColor();
        }

        return col;

    }

    public static <L extends Comparable<L>> void setColor(final L l, int r, final int g, final int b) {
        r = r << 8;
        r |= g;
        r = r << 8;
        r |= b;

        m_colorTable.put(l.hashCode(), r);
    }

    public static <L extends Comparable<L>> void setColor(final L l, final int col) {

        m_colorTable.put(l.hashCode(), col);
    }

    public static void setBoundingBoxColor(final Color color) {
        m_boundingBoxColor = color;
    }

}
