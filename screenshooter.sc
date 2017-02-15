#!/usr/bin/env amm

import $ivy.`org.scala-lang.modules::scala-swing:2.0.0`

import java.awt.{
  Cursor,
  Dimension,
  Graphics2D,
  GraphicsEnvironment,
  Image,
  Point,
  Rectangle,
  Robot,
  Toolkit => AwtToolkit
}
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import javax.imageio.ImageIO
import scala.swing._
import scala.swing.event._

private val translucentColour = new Color(0, 0, 0, 0xbb)
private val borderColour = new Color(0xff, 0xff, 0xff)
private val textColour = new Color(0xff, 0xff, 0xff)

private val screenDimensions = AwtToolkit.getDefaultToolkit.getScreenSize

private val rootDir = new File(sys.env("HOME"), "Desktop")

private def findBounds(from: Point, to: Point): Rectangle = {
  val upperLeft = new Point(math.min(from.x, to.x), math.min(from.y, to.y))
  val dims = new Dimension(math.abs(from.x - to.x), math.abs(from.y - to.y))
  new Rectangle(upperLeft, dims)
}

private def drawCentredString(g: Graphics2D, s: String, rect: Rectangle): Unit = {
  val metrics = g.getFontMetrics(g.getFont)
  val x = rect.x + (rect.width - metrics.stringWidth(s)) / 2
  val y = rect.y + (rect.height - metrics.getHeight) / 2 + metrics.getAscent

  g.drawString(s, x, y)
}

private def bufferImage(image: Image): BufferedImage = {
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

private def screenshotDest: File = {
  val format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
  val now = format.format(new Date)
  val id = UUID.randomUUID.toString.filter { _ != '-' } take 8
  new File(rootDir, s"screenshot_${now}_$id.png")
}

private class Screenshot(image: BufferedImage, cb: BufferedImage => Unit) extends Component {
  private var from: Option[Point] = None
  private var to: Option[Point] = None

  listenTo(mouse.clicks, mouse.moves, keys)
  focusable = true
  cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)

  reactions += {
    case KeyPressed(_, Key.Escape, _, _) => sys.exit(0)  // FIXME: Never triggered
    case MousePressed(_, point, mods, _, _) => {
      from = Some(point)
    }
    case MouseDragged(_, point, _) => {
      to = Some(point)
      repaint()
    }
    case MouseReleased(_, point, mods, _, _) => {
      (from, to) match {
        case (Some(fromPoint), Some(toPoint)) => commit(fromPoint, toPoint)
        case _ => Console.err.println("WAR: Mouse released without to and from being set")
      }
    }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    g.drawRenderedImage(image, new AffineTransform())

    (from, to) match {
      case (Some(fromPoint), Some(toPoint)) => {
        val rect = findBounds(fromPoint, toPoint)

        g.setPaint(translucentColour)
        g.fill(rect)

        g.setPaint(borderColour)
        g.draw(rect)

        if (rect.width > 50 && rect.height > 50) {
          val dims = s"${rect.width} x ${rect.height}"
          g.setPaint(textColour)
          drawCentredString(g, dims, rect)
        }
      }
      case _ => ()
    }
  }

  def commit(from: Point, to: Point): Unit = {
    println("Cropping and commiting image...")
    val rect = findBounds(from, to)
    val cropped = image.getSubimage(rect.x, rect.y, rect.width, rect.height)
    cb(cropped)
  }
}

private class Screenshotter(screenshot: BufferedImage) extends SimpleSwingApplication {
  def saveAndExit(image: BufferedImage): Unit = {
    ImageIO.write(image, "png", screenshotDest)
    shutdown()
    sys.exit(0)
  }

  private val mainPanel = new Screenshot(screenshot, saveAndExit)
  mainPanel.preferredSize = screenDimensions

  def top = new MainFrame {
    // Go fullscreen
    peer.setUndecorated(true)
    val dev = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
    if (dev.isFullScreenSupported) {
      dev.setFullScreenWindow(peer)
    }

    title = "Test"
    contents = mainPanel
  }
}

@main
def main(): Unit = {
  val shot = new Robot().createScreenCapture(new Rectangle(new Point(0, 0), screenDimensions))
  val screenshotter = new Screenshotter(bufferImage(shot))
  screenshotter.main(Array.empty[String])
}
