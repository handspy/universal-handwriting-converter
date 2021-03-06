package pt.up.hs.uhc.base;

/**
 * Base constants.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Constants {
    public static final int WRITE_DOUBLE_PRECISION = 3;
    public static final String WRITE_DOUBLE_FORMAT = "%." + WRITE_DOUBLE_PRECISION + "f";

    public static final double EPSILON = 1e-3;

    public static final double DOT_PER_INCH = 72D;
    public static final double DOT_PER_INCH_TO_INCH = 1D / DOT_PER_INCH;
    public static final double INCHES_TO_MM_FACTOR = 25.4D;

    public static final double PIXEL_TO_DOT_FACTOR = 600D / 72D / 56D;
    public static final double ANOTO_TO_MM_FACTOR = 1D / 3.3D / 8D;
}
