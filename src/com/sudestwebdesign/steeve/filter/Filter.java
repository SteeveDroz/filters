package com.sudestwebdesign.steeve.filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This enum contains the list of the filters that can be used, each one is
 * described individually.
 * 
 * @author Steeve Droz
 * 
 */
enum Filter {
	/**
	 * Inverts the hue of the image without changing the light. Light red
	 * becomes light cyan.
	 */
	COLOR {
		@Override
		public String getName() {
			return "_color";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			// Setting the color to the array
			int[] pixel = new int[3];
			pixel[0] = getRed(r, g, b);
			pixel[1] = getGreen(r, g, b);
			pixel[2] = getBlue(r, g, b);
			return pixel;
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}

		private int getRed(int red, int green, int blue) {
			return invert(red, green, blue);
		}

		private int getGreen(int red, int green, int blue) {
			return invert(green, blue, red);
		}

		private int getBlue(int red, int green, int blue) {
			return invert(blue, red, green);
		}

		private int invert(int first, int second, int third) {
			int max = Math.max(Math.max(first, second), third);
			int min = Math.min(Math.min(first, second), third);
			int median = (max + min) / 2;
			return Math.max(Math.min(2 * median - first, 255), 0);
		}
	},
	/**
	 * Inverts the light of the image without changing the hue. Light red
	 * becomes dark red.
	 */
	LIGHT {
		@Override
		public String getName() {
			return "_light";
		}

		@Override
		protected int[] individual(int r, int g, int b) {

			int[] pixel = COLOR.individual(r, g, b);

			return INVERT.individual(pixel[0], pixel[1], pixel[2]);
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	},
	/**
	 * Inverts the image color. Light red becomes dark cyan.
	 */
	INVERT {
		@Override
		public String getName() {
			return "_invert";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			int[] pixel = new int[3];
			pixel[0] = 255 - r;
			pixel[1] = 255 - g;
			pixel[2] = 255 - b;
			return pixel;
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	},
	/**
	 * Brushes the image so that sharp pixels become invisible.
	 */
	ANTIALIASING {
		@Override
		public String getName() {
			return "_antialiasing";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			return new int[] {r, g, b};
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			int width = imageArray.length;
			int height = imageArray[0].length;

			int[][][] out = new int[width][height][3];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
						out[x][y][0] = imageArray[x][y][0];
						out[x][y][1] = imageArray[x][y][1];
						out[x][y][2] = imageArray[x][y][2];
					}
					else {
						out[x][y][0] = (imageArray[x - 1][y][0]
								+ imageArray[x + 1][y][0]
								+ imageArray[x][y - 1][0]
								+ imageArray[x][y + 1][0] + imageArray[x][y][0]) / 5;
						out[x][y][1] = (imageArray[x - 1][y][1]
								+ imageArray[x + 1][y][1]
								+ imageArray[x][y - 1][1]
								+ imageArray[x][y + 1][1] + imageArray[x][y][1]) / 5;
						out[x][y][2] = (imageArray[x - 1][y][2]
								+ imageArray[x + 1][y][2]
								+ imageArray[x][y - 1][2]
								+ imageArray[x][y + 1][2] + imageArray[x][y][2]) / 5;
					}
				}
			}
			return out;
		}
	},
	/**
	 * Turns an image into a gray scale image, changing every color to a
	 * corresponding gray.
	 */
	GRAYSCALE {
		@Override
		public String getName() {
			return "_grayscale";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			int max = Math.max(Math.max(r, g), b);
			return new int[] {max, max, max};
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	},
	/**
	 * Turns an image into a gray scale image, unless the pixel is considered
	 * red (i.e. the red component is bigger than the two others and the green
	 * and blue are similar.
	 */
	RED {
		@Override
		public String getName() {
			return "_red";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			if (r > g && r > b && Math.abs(g - b) < 32) {
				return new int[] {r, g, b};
			}
			return GRAYSCALE.individual(r, g, b);
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	},
	/**
	 * Turns a gray scale version of the image where only the red component is
	 * kept. No red becomes black and full red becomes white.
	 */
	REDLAYER {
		@Override
		public String getName() {
			return "_redlayer";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			return new int[] {r, r, r};
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	},
	/**
	 * The source image is returned.
	 */
	NOTHING {
		@Override
		public String getName() {
			return "";
		}

		@Override
		protected int[] individual(int r, int g, int b) {
			return new int[] {r, g, b};
		}

		@Override
		protected int[][][] global(int[][][] imageArray) {
			return imageArray;
		}
	};

	/**
	 * Returns the name of the filter in a way that it can be added to the name.
	 * 
	 * @return The name of the filter
	 */
	public abstract String getName();

	/**
	 * The function applied to each pixel of the image by the filter. This is
	 * used when the knowledge of the surrounding of a pixel is not needed in
	 * order to determine the color of this one.
	 * 
	 * @param r
	 *            The red component
	 * @param g
	 *            The green component
	 * @param b
	 *            The blue component
	 * @return The color of the pixel in the modified image in the form of a
	 *         <code>int[]</code> containing <code>{red, green, blue}</code>.
	 */
	protected abstract int[] individual(int r, int g, int b);

	/**
	 * the function applied to the image. This is used when the knowledge of the
	 * surrounding of a pixel is needed in order to determine the color of this
	 * one.
	 * 
	 * @param imageArray
	 *            The image in the form of a <code>int[width][heigth][3]</code>,
	 *            each value containing a red, green or blue component for the
	 *            given pixel.
	 * @return The color of the pixel in the modified image in the form of a
	 *         <code>int[]</code> containing <code>{red, green, blue}</code>.
	 */
	protected abstract int[][][] global(int[][][] imageArray);

	/**
	 * Creates a filter based on its name.
	 * 
	 * @param name
	 *            The case insensitive name of a filter
	 * @return The corresponding filter or {@link #NOTHING} if the name
	 *         doesn't match
	 *         any filter.
	 */
	public static Filter create(String name) {
		try {
			return valueOf(name.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			StringBuilder str = new StringBuilder("Unknown filter: ").append(
					name).append(", the list of filters are: ");
			for (int i = 0; i < values().length - 1; i++) {
				str.append(values()[i].toString().toLowerCase()).append(", ");
			}
			str.append(values()[values().length - 1].toString().toLowerCase())
					.append(".");
			System.out.println(str.toString());
			return Filter.NOTHING;
		}
	}

	/**
	 * Applies the filter to the image and returns the resulting image.<br />
	 * <br />
	 * First, the {@link #individual(int, int, int)} function is applied, then
	 * the {@link #global(int[][][])} function.
	 * 
	 * @param image
	 *            The initial image to apply a filter to.
	 * @return The resulting image.
	 */
	public BufferedImage apply(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage out = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		int[][][] imageArray = new int[width][height][3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = image.getRaster().getSample(x, y, 0);
				int g = image.getRaster().getSample(x, y, 1);
				int b = image.getRaster().getSample(x, y, 2);

				imageArray[x][y] = individual(r, g, b);
			}
		}
		imageArray = global(imageArray);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = getRGB(imageArray[x][y]);
				out.setRGB(x, y, rgb);
			}
		}
		return out;
	}

	/**
	 * Transforms a color with three components in a color with one number, as
	 * usually used with Java.
	 * 
	 * @param values
	 *            The color in the form of a <code>{red, green, blue}</code>
	 *            array.
	 * @return A number representing the color. It uses {@link Color#getRGB()}.
	 */
	private static int getRGB(int[] values) {
		return new Color(values[0], values[1], values[2]).getRGB();
	}
}
