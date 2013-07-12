package org.knime.knip.core.io.externalization.externalizers;

import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable16;
import net.imglib2.display.ColorTable8;
import net.imglib2.meta.ImageMetadata;

import org.knime.knip.core.data.img.ImageMetadataImpl;
import org.knime.knip.core.io.externalization.BufferedDataInputStream;
import org.knime.knip.core.io.externalization.BufferedDataOutputStream;
import org.knime.knip.core.io.externalization.Externalizer;

public class ImageMetadataExt1 implements Externalizer<ImageMetadata> {

    private enum ColorTables {
        ColorTable8, ColorTable16
    };

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Class<ImageMetadata> getType() {
        return ImageMetadata.class;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ImageMetadata read(final BufferedDataInputStream in) throws Exception {
        final ImageMetadataImpl obj = new ImageMetadataImpl();

        // Valid bits are deserialized
        obj.setValidBits(in.readInt());

        // Channel Min/Max are deserialized
        final int numChannels = in.readInt();

        for (int c = 0; c < numChannels; c++) {
            obj.setChannelMinimum(c, in.readDouble());
            obj.setChannelMaximum(c, in.readDouble());
        }

        // Colortables are deserialized
        final int numColorTables = in.readInt();
        obj.initializeColorTables(numColorTables);

        for (int t = 0; t < numColorTables; t++) {

            if (in.readBoolean()) {
                final int componentCount = in.readInt();
                final int length = in.readInt();

                switch (ColorTables.values()[in.readInt()]) {
                    case ColorTable8:
                        final byte[][] ct8 = new byte[componentCount][length];

                        for (int c = 0; c < componentCount; c++) {
                            for (int k = 0; k < length; k++) {
                                ct8[c][k] = in.readByte();
                            }
                        }

                        obj.setColorTable(new ColorTable8(ct8), t);
                        break;
                    case ColorTable16:
                        final short[][] ct16 = new short[componentCount][length];

                        for (int c = 0; c < componentCount; c++) {
                            for (int k = 0; k < length; k++) {
                                ct16[c][k] = in.readShort();
                            }
                        }

                        obj.setColorTable(new ColorTable16(ct16), t);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Fatal error! Unknown ColorTable in ImageMetadataExt1.java! Please contact Administrators!");
                }

            }
        }

        return obj;
    }

    @Override
    public void write(final BufferedDataOutputStream out, final ImageMetadata obj) throws Exception {

        // Valid bits
        out.writeInt(obj.getValidBits());

        // Channels are serialized
        final int numChannels = obj.getCompositeChannelCount();
        out.writeInt(numChannels);

        for (int c = 0; c < numChannels; c++) {
            out.writeDouble(obj.getChannelMinimum(c));
            out.writeDouble(obj.getChannelMaximum(c));
        }

        // Color Tables are serialized
        final int numTables = obj.getColorTableCount();

        out.writeInt(numTables);

        for (int t = 0; t < numTables; t++) {
            final ColorTable table = obj.getColorTable(t);
            out.writeBoolean(table != null);

            if (table != null) {

                out.writeInt(table.getComponentCount());
                out.writeInt(table.getLength());
                if (table instanceof ColorTable8) {
                    out.writeInt(ColorTables.ColorTable8.ordinal());
                    for (int c = 0; c < table.getComponentCount(); c++) {
                        for (int k = 0; k < table.getLength(); k++) {
                            out.writeByte(((ColorTable8)table).getNative(c, k));
                        }
                    }
                } else if (table instanceof ColorTable16) {
                    out.writeInt(ColorTables.ColorTable16.ordinal());
                    for (int c = 0; c < table.getComponentCount(); c++) {
                        for (int k = 0; k < table.getLength(); k++) {
                            out.writeShort(((ColorTable16)table).getNative(c, k));
                        }
                    }
                }
            }
        }

    }
}
