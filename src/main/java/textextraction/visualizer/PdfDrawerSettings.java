package textextraction.visualizer;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Some settings required by the PDF drawer.
 *
 * @author Claudius Korzen
 */
public class PdfDrawerSettings {
  /**
   * The default border width of a rectangle.
   */
  public static final float DEFAULT_RECT_BORDER_WIDTH = 1f;

  /**
   * The default border color of a rectangle.
   */
  public static final Color DEFAULT_RECT_BORDER_COLOR = Color.BLACK;

  /**
   * The default opacity of the border of a rectangle.
   */
  public static final float DEFAULT_RECT_BORDER_OPACITY = 1f;

  /**
   * The default filling color of a rectangle.
   */
  public static final Color DEFAULT_RECT_FILLING_COLOR = null;

  /**
   * The default opacity of the filling of a rectangle.
   */
  public static final float DEFAULT_RECT_FILLING_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default width of a line.
   */
  public static final float DEFAULT_LINE_WIDTH = 1f;

  /**
   * The default color of a line.
   */
  public static final Color DEFAULT_LINE_COLOR = Color.BLACK;

  /**
   * The default opacity of a line.
   */
  public static final float DEFAULT_LINE_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default border width of a circle.
   */
  public static final float DEFAULT_CIRCLE_BORDER_WIDTH = 1f;

  /**
   * The default border color of a circle.
   */
  public static final Color DEFAULT_CIRCLE_BORDER_COLOR = Color.BLACK;

  /**
   * The default opacity of the border of a circle.
   */
  public static final float DEFAULT_CIRCLE_BORDER_OPACITY = 1f;

  /**
   * The default filling color of a circle.
   */
  public static final Color DEFAULT_CIRCLE_FILLING_COLOR = null;

  /**
   * The default opacity of the filling of a circle.
   */
  public static final float DEFAULT_CIRCLE_FILLING_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default font.
   */
  public static final PDFont DEFAULT_TEXT_FONT = PDType1Font.HELVETICA;

  /**
   * The available fonts.
   */
  public static final Map<String, PDFont> FONTS = new HashMap<String, PDFont>();

  static {
    FONTS.put("times-roman", PDType1Font.TIMES_ROMAN);
    FONTS.put("times-bold", PDType1Font.TIMES_BOLD);
    FONTS.put("times-italic", PDType1Font.TIMES_ITALIC);
    FONTS.put("times-bolditalic", PDType1Font.TIMES_BOLD_ITALIC);
    FONTS.put("helvetica", PDType1Font.HELVETICA);
    FONTS.put("helvetica-bold", PDType1Font.HELVETICA_BOLD);
    FONTS.put("helvetica-oblique", PDType1Font.HELVETICA_OBLIQUE);
    FONTS.put("helvetica-boldoblique", PDType1Font.HELVETICA_BOLD_OBLIQUE);
    FONTS.put("courier", PDType1Font.COURIER);
    FONTS.put("courier-bold", PDType1Font.COURIER_BOLD);
    FONTS.put("courier-oblique", PDType1Font.COURIER_OBLIQUE);
    FONTS.put("courier-boldoblique", PDType1Font.COURIER_BOLD_OBLIQUE);
    FONTS.put("symbol", PDType1Font.SYMBOL);
    FONTS.put("zapfdingbats", PDType1Font.ZAPF_DINGBATS);
  }

  /**
   * The default font size.
   */
  public static final float DEFAULT_TEXT_FONT_SIZE = 12f;

  /**
   * The default font color.
   */
  public static final Color DEFAULT_TEXT_FONT_COLOR = Color.BLACK;

  /**
   * The default font size.
   */
  public static final float DEFAULT_TEXT_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The string used in a field of an instruction in an instruction file to reference a null value.
   */
  public static final String INSTRUCTION_NULL_IDENTIFIER = "-";
}
