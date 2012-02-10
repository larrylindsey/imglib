package net.imglib2.examples;

import java.io.File;

import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import ij.ImageJ;

/**
 * Here we want to copy an Image into another with a different Container one using a generic method,
 * using a LocalizingCursor and a LocalizableByDimCursor
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example2c
{
	public Example2c() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy the image
		Img< FloatType > duplicate = copyImage( image, new CellImgFactory< FloatType >( 20 ) );

		// display the copy
		ImageJFunctions.show( duplicate );
	}

	public < T extends Type< T >> Img< T > copyImage( final Img< T > input, final ImgFactory< T > imageFactory )
	{
		// create a new Image with the same dimensions
		Img< T > output = imageFactory.create( input, input.firstElement() );

		// create a cursor that automatically localizes itself on every move
		Cursor< T > cursorInput = input.localizingCursor();
		LocalizableByDimCursor< T > cursorOutput = output.createLocalizableByDimCursor();

		// iterate over the input cursor
		while ( cursorInput.hasNext())
		{
			// move input cursor forward
			cursorInput.fwd();

			// set the output cursor to the position of the input cursor
			cursorOutput.setPosition( cursorInput );

			// set the value of this pixel of the output image, every Type supports T.set( T type )
			cursorOutput.get().set( cursorInput.get() );
		}

		//. return the copy
		return output;
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2c();
	}
}
