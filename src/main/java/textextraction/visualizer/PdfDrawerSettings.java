package textextraction.visualizer;

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
   * The default font.
   */
  public static final PDFont DEFAULT_FONT = PDType1Font.HELVETICA;

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
}
