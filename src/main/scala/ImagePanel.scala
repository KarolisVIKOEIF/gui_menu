/**
 * Created by Karolis Labrencis on 3/5/14.
 *
 */
package lt.labrencis.menu

import swing._

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImagePanel extends Panel {
  private var _imagePath = ""
  private var bufferedImage: BufferedImage = null

  def imagePath = _imagePath

  def imagePath_=(value: String) {
    try {
    _imagePath = value
    val imgUri = getClass.getResource(value)
    bufferedImage = ImageIO.read(new File(imgUri.toURI))
    } catch {
      case ex: NullPointerException => println("Fuckup: " +value + "\n")
    }
  }


  override def paintComponent(g: Graphics2D) = {
    if (null != bufferedImage) g.drawImage(bufferedImage, 0, 0, null)
  }
}

object ImagePanel {
  def apply() = new ImagePanel()
}
