package com.sudestwebdesign.steeve.filter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This is the main class that uses the filters.<br />
 * <br />
 * Use: pass as parameters an image and any number of filter names that can be
 * repeated and that will be applied in sequence.<br />
 * <br />
 * The source image won't be replaced unless only the name of the image is
 * given. In that case, the source image will be replaced by itself, unchanged.<br />
 * <br />
 * Another image will be created, its name depends of the filters in use. For
 * example, if the command is
 * <code>FilterMaker imageName.jpg invert grayscale</code>, the name of the
 * image will be <code>imageName_invert_grayscale.jpg</code>.
 * 
 * @author Steeve Droz
 * 
 */
public class FilterMaker {
	/**
	 * The main method.
	 * 
	 * @param args
	 *            The name of a valid JPG image followed by any number of filter
	 *            names.
	 */
	public static void main(String[] args) {
		try {
			// Creates a buffered image from the source or throws an exception
			// if it doesn't exist.
			BufferedImage image = ImageIO.read(new File(args[0]));

			// For now, the result image is merely a copy of the source image.
			BufferedImage resultImage = image;

			// The name of the image will be composed with the original name
			// along with the filter names.
			String originalName = args[0].replaceAll(".jpg$", "");
			StringBuilder name = new StringBuilder(originalName);

			// Apply each filter
			for (int i = 1; i < args.length; i++) {
				// If the name of the filter isn't found, a message is displayed
				// and the filter is not applied.
				Filter filter = Filter.create(args[i]);

				resultImage = filter.apply(resultImage);
				name.append(filter.getName());
			}

			File file = new File(name.toString() + ".jpg");
			ImageIO.write(resultImage, "jpg", file);
		}
		catch (IOException e) {
			System.out.println("The image can't be found.");
			e.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Syntax error, the correct syntax is: java "
					+ FilterMaker.class.getSimpleName()
					+ " <image> [<filter1> [<filter2> [...]]]");
			e.printStackTrace();
		}
	}
}
