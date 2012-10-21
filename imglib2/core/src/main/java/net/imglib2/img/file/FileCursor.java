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

package net.imglib2.img.file;

import net.imglib2.AbstractCursorInt;
import net.imglib2.Cursor;
import net.imglib2.util.IntervalIndexer;

/**
 * {@link Cursor} on a {@link FileImg}.
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
final public class FileCursor<T extends ExternalizableType<T>> extends
		AbstractCursorInt<T> {
	private int i;
	final private int maxNumPixels;

	final private FileImg<T> container;

	protected FileCursor(final FileCursor<T> cursor) {
		super(cursor.numDimensions());

		container = cursor.container;
		this.maxNumPixels = cursor.maxNumPixels;

		i = cursor.i;
	}

	public FileCursor(final FileImg<T> container) {
		super(container.numDimensions());

		this.container = container;
		this.maxNumPixels = (int) container.size() - 1;

		reset();
	}

	@Override
	public T get() {
		return container.get(i);
	}

	public void set(final T t) {
		container.set(t, i);
	}

	@Override
	public FileCursor<T> copy() {
		return new FileCursor<T>(this);
	}

	@Override
	public FileCursor<T> copyCursor() {
		return copy();
	}

	@Override
	public boolean hasNext() {
		return i < maxNumPixels;
	}

	@Override
	public void jumpFwd(final long steps) {
		i += steps;
	}

	@Override
	public void fwd() {
		++i;
	}

	@Override
	public void reset() {
		i = -1;
	}

	@Override
	public void localize(final int[] position) {
		IntervalIndexer.indexToPosition(i, container.dim, position);
	}

	@Override
	public int getIntPosition(final int d) {
		return IntervalIndexer.indexToPosition(i, container.dim,
				container.step, d);
	}
}
