package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import ij.ImageJ;
import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.container.array.ArrayContainerFactory;

/**
 * Perform a gaussian convolution using fourier convolution
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example8
{
	public Example8()
	{
		// open with ImgOpener using an ArrayContainer
		Img< FloatType > image = ImgOpener.openLOCIFloatType( "DrosophilaWing.tif", new ArrayContainerFactory() );
		Img< FloatType > kernel = ImgOpener.openLOCIFloatType( "kernelGauss.tif", new ArrayContainerFactory() );

		// normalize the kernel
		NormalizeImageFloat< FloatType > normImage = new NormalizeImageFloat< FloatType >( kernel );

		if ( !normImage.checkInput() || !normImage.process() )
		{
			System.out.println( "Cannot normalize kernel: " + normImage.getErrorMessage() );
			return;
		}

		kernel.close();
		kernel = normImage.getResult();

		// display all
		ImageJFunctions.copyToImagePlus( kernel ).show().setTitle( "kernel" );

		ImageJFunctions.copyToImagePlus( image ).show();

		// compute fourier convolution
		FourierConvolution< FloatType, FloatType > fourierConvolution = new FourierConvolution< FloatType, FloatType >( image, kernel );

		if ( !fourierConvolution.checkInput() || !fourierConvolution.process() )
		{
			System.out.println( "Cannot compute fourier convolution: " + fourierConvolution.getErrorMessage() );
			return;
		}

		Img< FloatType > convolved = fourierConvolution.getResult();

		final String name = "(" + fourierConvolution.getProcessingTime() + " ms) Convolution of " + image.getName();
		ImageJFunctions.copyToImagePlus( convolved ).show().setTitle( name );

	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example8();
	}
}