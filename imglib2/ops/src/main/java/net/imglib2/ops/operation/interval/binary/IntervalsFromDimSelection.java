/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.interval.binary;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.ops.img.BinaryObjectFactory;
import net.imglib2.ops.operation.BinaryOutputOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.IterateUnaryOperation;

/**
 * Merges an interval array
 * 
 * {@link IntervalwiseUnaryManipulation}, {@link IterateUnaryOperation},
 * {@link IterateUnaryOperation} or {@link IterativeBinaryImgTransformation}.
 * 
 * @author Christian Dietz (University of Konstanz)
 */
public class IntervalsFromDimSelection implements
		BinaryOutputOperation<int[], Interval[], Interval[]> {

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public Interval[] compute(int[] selectedDims, Interval[] incomingIntervals,
			Interval[] resIntervals) {

		int offset = 0;
		for (int i = 0; i < incomingIntervals.length; i++) {

			long[] min = new long[incomingIntervals[i].numDimensions()];
			long[] pointCtr = new long[incomingIntervals[i].numDimensions()];
			long[] srcDims = new long[incomingIntervals[i].numDimensions()];

			incomingIntervals[i].min(min);
			incomingIntervals[i].max(pointCtr);
			incomingIntervals[i].dimensions(srcDims);

			long[] max = pointCtr.clone();

			int[] unselectedDims = getUnselectedDimIndices(selectedDims,
					srcDims.length);

			long[] indicators = new long[unselectedDims.length];
			Interval interval = new FinalInterval(min, pointCtr);

			for (int j = indicators.length - 1; j > -1; j--) {
				indicators[j] = 1;
				if (j < indicators.length - 1)
					indicators[j] = (srcDims[unselectedDims[j + 1]])
							* indicators[j + 1];
			}

			for (int u : unselectedDims) {
				pointCtr[u] = -1;
			}

			for (int n = 0; n < getNumIterationSteps(selectedDims,
					incomingIntervals[i]); n++) {
				max = pointCtr.clone();

				for (int j = 0; j < indicators.length; j++) {
					if (n % indicators[j] == 0)
						pointCtr[unselectedDims[j]]++;

					if (srcDims[unselectedDims[j]] == pointCtr[unselectedDims[j]])
						pointCtr[unselectedDims[j]] = 0;
				}

				for (int u : unselectedDims) {
					max[u] = pointCtr[u] + min[u];
					min[u] = max[u];
				}

				resIntervals[offset + n] = new FinalInterval(min, max);
				interval.min(min);
			}
		}
		return resIntervals;
	}

	/**
	 * @param dims
	 * @return
	 */
	private final static synchronized int getNumIterationSteps(
			int[] selectedDims, Interval interval) {

		long[] dims = new long[interval.numDimensions()];
		interval.dimensions(dims);

		int[] unselectedDims = getUnselectedDimIndices(selectedDims,
				dims.length);
		int steps = 1;
		for (int i = 0; i < unselectedDims.length; i++) {
			steps *= dims[unselectedDims[i]];
		}

		return steps;
	}

	/**
	 * @return
	 */
	private final static synchronized int[] getUnselectedDimIndices(
			int[] selectedDims, int numDims) {
		final boolean[] tmp = new boolean[numDims];
		int i;
		for (i = 0; i < selectedDims.length; i++) {
			if (selectedDims[i] >= numDims) {
				break;
			}
			tmp[selectedDims[i]] = true;
		}

		int[] res = new int[numDims - i];

		int j = 0;
		for (int k = 0; j < res.length; k++) {
			if (k >= tmp.length || !tmp[k]) {
				res[j++] = k;
			}
		}
		return res;

	}

	@Override
	public BinaryOutputOperation<int[], Interval[], Interval[]> copy() {
		return new IntervalsFromDimSelection();
	}

	@Override
	public BinaryObjectFactory<int[], Interval[], Interval[]> bufferFactory() {
		return new BinaryObjectFactory<int[], Interval[], Interval[]>() {

			@Override
			public Interval[] instantiate(final int[] op0, final Interval[] op1) {

				int totalSteps = 0;
				for (int i = 0; i < op1.length; i++) {
					totalSteps += getNumIterationSteps(op0, op1[i]);
				}
				return new Interval[totalSteps];
			}
		};
	}

}
