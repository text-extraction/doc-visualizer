package textextraction.visualizer;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import textextraction.common.models.Rectangle;

/**
 * A class to draw geometric shapes (rectangles and circles) and texts to an existing PDF file.
 *
 * @author Claudius Korzen.
 */
public class PdfDrawer {
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

  /**
   * The logger.
   */
  protected static final Logger LOG = LogManager.getLogger(PdfDrawer.class);

  /**
   * The path to the input PDF file to which the drawings should be added.
   */
  protected Path pdf;

  /**
   * The input PDF file in a PdfBox-specific representation.
   */
  protected PDDocument pdDoc;

  /**
   * The streams of the pages of the input PDF file.
   */
  protected List<PDPageContentStream> streams;

  // ==============================================================================================

  /**
   * Creates a new PDF drawer that can be used to add drawings to the given PDF file.
   *
   * @param pdf The path to the input PDF file to which the drawings should be added.
   *
   * @throws IllegalArgumentException If there is no path to an input PDF file given.
   * @throws IllegalStateException    If there is an error on reading or loading the PDF file.
   */
  public PdfDrawer(Path pdf) throws IllegalArgumentException, IllegalStateException {
    this.pdf = pdf;
    this.streams = new ArrayList<>();

    if (pdf == null) {
      throw new IllegalArgumentException("No input PDF file given.");
    }

    // Check if the PDF file is readable.
    if (!Files.isReadable(pdf)) {
      String msg = String.format("The PDF file '%s' doesn't exist or isn't readable", pdf);
      throw new IllegalArgumentException(msg);
    }

    // Try to load the PDF file.
    try {
      this.pdDoc = PDDocument.load(pdf.toFile());
    } catch (IOException e) {
      String msg = String.format("Error on loading the PDF file '%s'.", pdf);
      throw new IllegalArgumentException(msg, e);
    }

    this.streams = loadPdfPageStreams(this.pdDoc);
  }

  /**
   * Loads the page streams of the given PDF document.
   *
   * @param pdf The PDF document to process.
   *
   * @return The page streams of the given PDF document.
   */
  protected List<PDPageContentStream> loadPdfPageStreams(PDDocument pdf) {
    List<PDPageContentStream> streams = new ArrayList<>();

    PDDocumentCatalog catalog = pdf.getDocumentCatalog();
    if (catalog == null) {
      String msg = String.format("The PDF file '%s' doesn't provide a document catalog.", pdf);
      throw new IllegalStateException(msg);
    }

    PDPageTree pages = catalog.getPages();
    if (pages == null || pages.getCount() == 0) {
      String msg = String.format("The PDF file '%s' doesn't contain any pages.", pdf);
      throw new IllegalStateException(msg);
    }

    // Iterate through the pages and load each into a stream.
    for (int i = 1; i < pages.getCount() + 1; i++) {
      try {
        streams.add(new PDPageContentStream(pdDoc, pages.get(i - 1), APPEND, true));
      } catch (IOException e) {
        String msg = String.format("Couldn't load page #%d of the PDF file '%s'.", i, pdf);
        throw new IllegalStateException(msg, e);
      }
    }

    return streams;
  }

  /**
   * Closes each of the given page content streams.
   *
   * @param streams The streams to close.
   */
  protected void closePdfPageStreams(List<PDPageContentStream> streams) {
    for (PDPageContentStream stream : streams) {
      try {
        stream.close();
      } catch (IOException e) {
        throw new IllegalStateException("Error on closing the page streams of the PDF.", e);
      }
    }
  }

  // ==============================================================================================

  /**
   * Draws a rectangle into the PDF file.
   *
   * @param pageNum        The page number of the page on which the rectangle should be drawn.
   * @param rect           The rectangle to draw.
   * @param borderWidth    The border width.
   * @param borderColor    The border color. If null, no border will be drawn.
   * @param borderOpacity  The border opacity; a value between 0 (= invisible) and 1 (= visible).
   * @param fillingColor   The filling color; if null, no filling will be drawn.
   * @param fillingOpacity The filling opacity; a value between 0 (= invisible) and 1 (= visible).
   *
   * @throws IllegalArgumentException If the validation of one of the arguments fails.
   * @throws IllegalStateException    If there is an error on drawing the rectangle.
   */
  public void drawRectangle(int pageNum, Rectangle rect, float borderWidth, Color borderColor,
          float borderOpacity, Color fillingColor, float fillingOpacity)
          throws IllegalArgumentException, IllegalStateException {
    drawRectangle(pageNum, rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY(),
            borderWidth, borderColor, borderOpacity, fillingColor, fillingOpacity);
  }

  /**
   * Draws a rectangle into the PDF file.
   *
   * @param pageNum        The page number of the page on which the rectangle should be drawn.
   * @param minX           The minimum x-coordinate of the rectangle to draw.
   * @param minY           The minimum y-coordinate of the rectangle to draw.
   * @param maxX           The maximum x-coordinate of the rectangle to draw.
   * @param maxY           The maximum y-coordinate of the rectangle to draw.
   * @param borderWidth    The border width.
   * @param borderColor    The border color. If null, no border will be drawn.
   * @param borderOpacity  The border opacity; a value between 0 (= invisible) and 1 (= visible).
   * @param fillingColor   The filling color; if null, no filling will be drawn.
   * @param fillingOpacity The filling opacity; a value between 0 (= invisible) and 1 (= visible).
   *
   * @throws IllegalArgumentException If the validation of one of the arguments fails.
   * @throws IllegalStateException    If there is an error on drawing the rectangle.
   */
  public void drawRectangle(int pageNum, float minX, float minY, float maxX, float maxY,
          float borderWidth, Color borderColor, float borderOpacity, Color fillingColor,
          float fillingOpacity) throws IllegalArgumentException, IllegalStateException {
    // Validate the page number.
    if (pageNum < 1) {
      throw new IllegalArgumentException("Page number must be > 0.");
    }

    // Validate the page number.
    if (pageNum > this.streams.size()) {
      String msg = String.format("Page number must be < %d.", this.streams.size() + 1);
      throw new IllegalArgumentException(msg);
    }

    // Validate the border width.
    if (borderWidth < 0) {
      throw new IllegalArgumentException("Border width must be >= 0.");
    }

    // Validate the border opacity.
    if (borderOpacity < 0f || borderOpacity > 1f) {
      throw new IllegalArgumentException("Border opacity must be a value between 0 and 1.");
    }

    // Validate the filling opacity.
    if (fillingOpacity < 0f || fillingOpacity > 1f) {
      throw new IllegalArgumentException("Filling opacity must be a value between 0 and 1.");
    }

    // Load the stream of the page.
    PDPageContentStream stream = this.streams.get(pageNum - 1);
    if (stream == null) {
      throw new IllegalStateException(String.format("Couldn't load page #%d.", pageNum));
    }

    // If width and/or height is 0, the lines are not visible.
    float width = Math.max(borderWidth, maxX - minX);
    float height = Math.max(borderWidth, maxY - minY);

    if (fillingColor != null) {
      // Try to draw the filling.
      try {
        // Set the opacity of the filling.
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(fillingOpacity);
        stream.setGraphicsStateParameters(graphicsState);
        stream.setNonStrokingColor(fillingColor);
        stream.addRect(minX, minY, width, height);
        stream.fill();
      } catch (IOException e) {
        throw new IllegalStateException("Error on drawing the filling of the rectangle.", e);
      }
    }

    if (borderColor != null) {
      // Try to draw the border.
      try {
        // Set the opacity of the border.
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setStrokingAlphaConstant(borderOpacity);
        stream.setGraphicsStateParameters(graphicsState);
        stream.addRect(minX, minY, width, height);
        stream.setStrokingColor(borderColor);
        stream.setLineWidth(borderWidth);
        stream.stroke();
      } catch (IOException e) {
        throw new IllegalStateException("Error on drawing the border of the rectangle.", e);
      }
    }
  }

  // ==============================================================================================

  /**
   * Draws a line into the PDF file.
   *
   * @param pageNum The page number of the page on which the line should be drawn.
   * @param x0      The x-coordinate of the start point of the line.
   * @param y0      The y-coordinate of the start point of the line.
   * @param x1      The x-coordinate of the end point of the line.
   * @param y1      The y-coordinate of the end point of the line.
   * @param width   The line width.
   * @param color   The line color.
   * @param opacity The line opacity; a value between 0 (= invisible) and 1 (= visible).
   *
   * @throws IllegalArgumentException If the validation of one of the arguments fails.
   * @throws IllegalStateException    If there is an error on drawing the line.
   */
  public void drawLine(int pageNum, float x0, float y0, float x1, float y1, float width,
          Color color, float opacity) throws IllegalArgumentException, IllegalStateException {
    // Validate the page number.
    if (pageNum < 1) {
      throw new IllegalArgumentException("Page number must be > 0.");
    }

    // Validate the page number.
    if (pageNum > this.streams.size()) {
      String msg = String.format("Page number must be < %d.", this.streams.size() + 1);
      throw new IllegalArgumentException(msg);
    }

    // Validate the width.
    if (width < 0) {
      throw new IllegalArgumentException("The line width must be >= 0.");
    }

    // Validate the opacity.
    if (opacity < 0f || opacity > 1f) {
      throw new IllegalArgumentException("The line opacity must be a value between 0 and 1.");
    }

    // Validate the color.
    if (color == null) {
      throw new IllegalArgumentException("No color specified.");
    }

    // Load the stream of the page.
    PDPageContentStream stream = this.streams.get(pageNum - 1);
    if (stream == null) {
      throw new IllegalStateException(String.format("Couldn't load page #%d.", pageNum));
    }

    // Try to draw the line.
    try {
      // Set the opacity of the border.
      PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
      graphicsState.setStrokingAlphaConstant(opacity);
      stream.setGraphicsStateParameters(graphicsState);
      stream.moveTo(x0, y0);
      stream.lineTo(x1, y1);
      stream.setStrokingColor(color);
      stream.setLineWidth(width);
      stream.stroke();
    } catch (IOException e) {
      throw new IllegalStateException("Error on drawing the border of the line.", e);
    }
  }

  // ==============================================================================================

  /**
   * Draws a circle into the PDF file.
   *
   * @param pageNum        The page number of the page on which the circle should be drawn.
   * @param x              The x-coordinate of the midpoint of the circle.
   * @param y              The y-coordinate of the midpoint of the circle.
   * @param r              The radius of the circle.
   * @param borderWidth    The border width.
   * @param borderColor    The border color; if null, no border will be drawn.
   * @param borderOpacity  The border opacity; a value between 0 (= invisible) and 1 (= visible).
   * @param fillingColor   The filling color; if null, no filling will be drawn.
   * @param fillingOpacity The filling opacity; a value between 0 (= invisible) and 1 (= visible).
   *
   * @throws IllegalArgumentException If the validation of one of the arguments fails.
   * @throws IllegalStateException    If there is an error on drawing the circle.
   */
  public void drawCircle(int pageNum, float x, float y, float r, float borderWidth,
          Color borderColor, float borderOpacity, Color fillingColor, float fillingOpacity)
          throws IllegalArgumentException, IllegalStateException {
    // Validate the page number.
    if (pageNum < 1) {
      throw new IllegalArgumentException("Page number must be > 0.");
    }

    // Validate the page number.
    if (pageNum > this.streams.size()) {
      String msg = String.format("Page number must be < %d.", this.streams.size() + 1);
      throw new IllegalArgumentException(msg);
    }

    // Validate the radius.
    if (r < 0) {
      throw new IllegalArgumentException("The radius must be >0.");
    }

    // Validate the border width.
    if (borderWidth < 0) {
      throw new IllegalArgumentException("Border width must be >= 0.");
    }

    // Validate the border opacity.
    if (borderOpacity < 0f || borderOpacity > 1f) {
      throw new IllegalArgumentException("Border opacity must be a value between 0 and 1.");
    }

    // Validate the filling opacity.
    if (fillingOpacity < 0f || fillingOpacity > 1f) {
      throw new IllegalArgumentException("Filling opacity must be a value between 0 and 1.");
    }

    // Load the stream of the page.
    PDPageContentStream stream = this.streams.get(pageNum - 1);
    if (stream == null) {
      throw new IllegalStateException(String.format("Couldn't load page #%d.", pageNum));
    }

    // The magic number needed to draw the circle.
    final float k = 0.552284749831f;
    if (fillingColor != null) {
      // Try to draw the filling.
      try {
        // Set the opacity of the filling.
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(fillingOpacity);
        stream.setGraphicsStateParameters(graphicsState);
        stream.setNonStrokingColor(fillingColor);
        stream.moveTo(x - r, y);
        stream.curveTo(x - r, y + k * r, x - k * r, y + r, x, y + r);
        stream.curveTo(x + k * r, y + r, x + r, y + k * r, x + r, y);
        stream.curveTo(x + r, y - k * r, x + k * r, y - r, x, y - r);
        stream.curveTo(x - k * r, y - r, x - r, y - k * r, x - r, y);
        stream.fill();
      } catch (IOException e) {
        throw new IllegalStateException("Error on drawing the filling of the circle.", e);
      }
    }

    if (borderColor != null) {
      // Try to draw the border.
      try {
        // Set the opacity of the border.
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setStrokingAlphaConstant(borderOpacity);
        stream.setGraphicsStateParameters(graphicsState);
        stream.setStrokingColor(borderColor);
        stream.moveTo(x - r, y);
        stream.curveTo(x - r, y + k * r, x - k * r, y + r, x, y + r);
        stream.curveTo(x + k * r, y + r, x + r, y + k * r, x + r, y);
        stream.curveTo(x + r, y - k * r, x + k * r, y - r, x, y - r);
        stream.curveTo(x - k * r, y - r, x - r, y - k * r, x - r, y);
        stream.setLineWidth(borderWidth);
        stream.stroke();
      } catch (IOException e) {
        throw new IllegalStateException("Error on drawing the border of the circle.", e);
      }
    }
  }

  // ==============================================================================================

  /**
   * Draws a piece of text into the PDF file.
   *
   * @param pageNum  The page number of the page on which the text should be drawn.
   * @param x        The x-coordinate of the lower left of the text to draw.
   * @param y        The y-coordinate of the lower left of the text to draw.
   * @param text     The text to draw.
   * @param fontName The font name.
   * @param fontSize The font size of the text.
   * @param color    The text color.
   * @param opacity  The text opacity; a value between 0 (= invisible) and 1 (= visible).
   *
   * @throws IllegalArgumentException If the validation of one of the arguments fails.
   * @throws IllegalStateException    If there is an error on drawing the text.
   */
  public void drawText(int pageNum, float x, float y, String text, String fontName, float fontSize,
          Color color, float opacity) throws IllegalArgumentException, IllegalStateException {
    // Validate the page number.
    if (pageNum < 1) {
      throw new IllegalArgumentException("Page number must be > 0.");
    }

    // Validate the page number.
    if (pageNum > this.streams.size()) {
      String msg = String.format("Page number must be < %d.", this.streams.size() + 1);
      throw new IllegalArgumentException(msg);
    }

    // Validate the text.
    if (text == null) {
      throw new IllegalArgumentException("No text given.");
    }

    // Validate the font.
    if (fontName == null) {
      throw new IllegalArgumentException("No font name given.");
    }

    // Validate the color.
    if (color == null) {
      throw new IllegalArgumentException("No color given.");
    }

    // Validate the font size.
    if (fontSize <= 0.0f) {
      throw new IllegalArgumentException("The font size must be > 0.");
    }

    // Load the stream of the page.
    PDPageContentStream stream = this.streams.get(pageNum - 1);
    if (stream == null) {
      throw new IllegalStateException(String.format("Couldn't load page #%d.", pageNum));
    }

    PDFont font = FONTS.containsKey(fontName) ? FONTS.get(fontName) : DEFAULT_FONT;

    try {
      // Try to draw the text.
      stream.setNonStrokingColor(color);
      stream.beginText();
      stream.setFont(font, fontSize);
      stream.newLineAtOffset(x, y);
      stream.showText(text);
      stream.endText();
    } catch (IOException e) {
      throw new IllegalStateException("Error on drawing the text.", e);
    }
  }

  // ==============================================================================================

  /**
   * Compeletes this drawer and closes all open streams. Beware: After calling this method, you
   * won't be able to add further drawings to the PDF and to call this method a second time.
   *
   * @return The visualization as byte array.
   */
  public byte[] complete() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      // Close all the open PDPageContentStream objects. Start at 1 because of the dummy at the
      // start.
      for (int i = 0; i < this.streams.size(); i++) {
        try {
          this.streams.get(i).close();
        } catch (IOException e) {
          continue;
        }
      }
      // Try to save the pdf document to the given file.
      this.pdDoc.save(baos);
    } catch (IOException e) {
      throw e;
    } finally {
      this.pdDoc.close();
    }
    return baos.toByteArray();
  }
}
