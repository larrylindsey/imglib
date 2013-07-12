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
 *   8 Jun 2010 (hornm): created
 */
package org.knime.knip.core.awt;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class Transparency {
    @SuppressWarnings("static-access")
    public static Image makeColorTransparent(final Image im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to
            // opaque
            private final int m_markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == m_markerRGB) {
                    // Mark the alpha bits as zero -
                    // transparent
                    return 0x00FFFFFF & rgb;
                }
                // nothing to do
                return rgb;
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().getDefaultToolkit().createImage(ip);
    }

    @SuppressWarnings("static-access")
    public static Image makeColorTransparent(final Image im, final Color color, final int rest) {
        final ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to
            // opaque
            private final int m_markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(final int x, final int y, final int rgb) {

                if ((rgb | 0xFF000000) == m_markerRGB) {
                    // Mark the alpha bits as zero -
                    // transparent
                    return 0x00FFFFFF & rgb;
                }

                return (rest << 24) | (rgb & 0x00FFFFFF);
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().getDefaultToolkit().createImage(ip);
    }

}
