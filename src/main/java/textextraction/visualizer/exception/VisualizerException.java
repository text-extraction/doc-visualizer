package textextraction.visualizer.exception;

/**
 * The exception to throw on any errors while visualizing a document.
 * 
 * @author Claudius Korzen
 */
public class VisualizerException extends Exception {
  /**
   * The serial id.
   */
  protected static final long serialVersionUID = -1208363363395692674L;

  /**
   * Creates a new visualizer exception.
   * 
   * @param message The error message to show when the exception was caught.
   */
  public VisualizerException(String message) {
    super(message);
  }

  /**
   * Creates a new visualizer exception.
   * 
   * @param message The error message to show when the exception was caught.
   * @param cause   The cause of this exception (this can be used to trace the error).
   */
  public VisualizerException(String message, Throwable cause) {
    super(message, cause);
  }
}
