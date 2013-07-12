package org.knime.knip.core.tools.comparators;

import java.util.Comparator;

import net.imglib2.Interval;

public class IntervalComperator implements Comparator<Interval> {

    @Override
    public int compare(final Interval o1, final Interval o2) {
        for (int d = 0; d < Math.min(o1.numDimensions(), o2.numDimensions()); d++) {
            if (o1.min(d) == o2.min(d)) {
                continue;
            }

            return (int)o1.min(d) - (int)o2.min(d);
        }

        return 0;
    }

}
