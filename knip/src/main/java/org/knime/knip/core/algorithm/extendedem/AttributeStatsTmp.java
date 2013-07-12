package org.knime.knip.core.algorithm.extendedem;

class AttributeStatsTmp {

    protected int[] m_nominalCounts;

    protected double[] m_nominalWeights;

    protected int m_totalCount = 0;

    protected int m_missingCount = 0;

    protected int m_uniqueCount = 0;

    protected int m_intCount = 0;

    protected int m_realCount = 0;

    protected int m_distinctCount = 0;

    protected StatsTmp m_numericStats;

    protected double m_small = 1e-6;

    public boolean eq(final double a, final double b) {

        return ((a - b) < m_small) && ((b - a) < m_small);
    }

    protected void addDistinct(final double value, final int count, final double weight) {

        if (count > 0) {
            if (count == 1) {
                m_uniqueCount++;
            }
            if (eq(value, ((int)value))) {
                m_intCount += count;
            } else {
                m_realCount += count;
            }
            if (m_nominalCounts != null) {
                m_nominalCounts[(int)value] = count;
                m_nominalWeights[(int)value] = weight;
            }
            if (m_numericStats != null) {
                m_numericStats.add(value, weight);
                m_numericStats.calculateDerived();
            }
        }
        m_distinctCount++;
    }

}