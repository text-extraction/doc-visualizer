package textextraction.visualizer;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;

import textextraction.common.models.Character;
import textextraction.common.models.Document;
import textextraction.common.models.ElementClass;
import textextraction.common.models.Figure;
import textextraction.common.models.Page;
import textextraction.common.models.Position;
import textextraction.common.models.Rectangle;
import textextraction.common.models.Shape;
import textextraction.visualizer.exception.VisualizerException;


/**
 * A visualizer to visualize the elements of a document.
 *
 * @author Claudius Korzen
 */
public class DocumentVisualizer {
  /**
   * Visualizes the elements of the given document.
   * 
   * @param doc     The document from which the elements should be visualized.
   * @param clazzes The classes of the elements to visualize.
   * 
   * @return The visualization as a byte array.
   * 
   * @throws VisualizerException If something went wrong on visualizing the elements.
   */
  public byte[] visualize(Document doc, Collection<ElementClass> clazzes)
          throws VisualizerException {
    PdfDrawer drawer = new PdfDrawer(doc.getPath());
    try {
      for (ElementClass clazz : clazzes) {
        switch (clazz) {
          case CHARACTERS:
            visualizeCharacters(doc, drawer);
            break;
          case FIGURES:
            visualizeFigures(doc, drawer);
            break;
          case SHAPES:
            visualizeShapes(doc, drawer);
            break;
          default:
            continue;
        }
      }
      return drawer.complete();
    } catch (IOException e) {
      throw new VisualizerException("Error on visualization.", e);
    }
  }

  // ==============================================================================================

  /**
   * Visualizes the characters of the given document.
   * 
   * @param doc    The document to process.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the characters.
   */
  protected void visualizeCharacters(Document doc, PdfDrawer drawer) throws VisualizerException {
    for (Page page : doc.getPages()) {
      for (Character character : page.getCharacters()) {
        visualizeCharacter(character, drawer);
      }
    }
  }

  /**
   * Visualizes the given character.
   * 
   * @param c      The character to visualize.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the character.
   */
  protected void visualizeCharacter(Character c, PdfDrawer drawer) throws VisualizerException {
    visualizePosition(c.getPosition(), drawer, Color.BLACK);
  }

  // ==============================================================================================

  /**
   * Visualizes the figures of the given document.
   * 
   * @param doc    The document to process.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the figures.
   */
  protected void visualizeFigures(Document doc, PdfDrawer drawer) throws VisualizerException {
    for (Page page : doc.getPages()) {
      for (Figure figure : page.getFigures()) {
        visualizeFigure(figure, drawer);
      }
    }
  }

  /**
   * Visualizes the given figure.
   * 
   * @param figure The figure to visualize.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the figure.
   */
  protected void visualizeFigure(Figure figure, PdfDrawer drawer) throws VisualizerException {
    visualizePosition(figure.getPosition(), drawer, Color.CYAN);
  }

  // ==============================================================================================

  /**
   * Visualizes the shapes of the given document.
   * 
   * @param doc    The document to process.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the shapes.
   */
  protected void visualizeShapes(Document doc, PdfDrawer drawer) throws VisualizerException {
    for (Page page : doc.getPages()) {
      for (Shape shape : page.getShapes()) {
        visualizeShape(shape, drawer);
      }
    }
  }

  /**
   * Visualizes the given shape.
   * 
   * @param shape  The shape to visualize.
   * @param drawer The drawer to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the shape.
   */
  protected void visualizeShape(Shape shape, PdfDrawer drawer) throws VisualizerException {
    visualizePosition(shape.getPosition(), drawer, Color.ORANGE);
  }

  // ==============================================================================================

  /**
   * Visualizes the given position using the given color.
   * 
   * @param pos    The position to visualize.
   * @param drawer The drawer to use.
   * @param color  The color to use.
   * 
   * @throws VisualizerException If something went wrong on visualizing the position.
   */
  protected void visualizePosition(Position pos, PdfDrawer drawer, Color color)
          throws VisualizerException {
    if (pos != null) {
      Page page = pos.getPage();
      Rectangle rect = pos.getRectangle();

      if (page != null && rect != null) {
        drawer.drawRectangle(page.getPageNumber(), rect, 1f, color, 1f, null, 1f);
      }
    }
  }
}

