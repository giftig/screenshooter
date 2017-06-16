package com.xantoria.screenshooter

import java.awt._
import java.awt.image.BufferedImage

object Utils {
  def findBounds(from: Point, to: Point): Rectangle = {
    val upperLeft = new Point(math.min(from.x, to.x), math.min(from.y, to.y))
    val dims = new Dimension(math.abs(from.x - to.x), math.abs(from.y - to.y))
    new Rectangle(upperLeft, dims)
  }

  def drawCentredString(g: Graphics2D, s: String, rect: Rectangle): Unit = {
    val metrics = g.getFontMetrics(g.getFont)
    val x = rect.x + (rect.width - metrics.stringWidth(s)) / 2
    val y = rect.y + (rect.height - metrics.getHeight) / 2 + metrics.getAscent

    g.drawString(s, x, y)
  }

  def bufferImage(image: Image): BufferedImage = {
    image match {
      case buffered: BufferedImage => buffered
      case _ => {
        val (w, h) = (image.getWidth(null), image.getHeight(null))
        val buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
        val g = buffered.createGraphics()
        g.drawImage(image, 0, 0, w, h, null)
        buffered
      }
    }
  }

  lazy val screenDimensions = Toolkit.getDefaultToolkit.getScreenSize

  def captureScreen(): Image = new Robot().createScreenCapture(
    new Rectangle(new Point(0, 0), screenDimensions)
  )
}
