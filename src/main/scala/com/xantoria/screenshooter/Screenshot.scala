package com.xantoria.screenshooter

import java.awt.{Cursor,
  Graphics2D,
  GraphicsEnvironment,
  Point
}
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import scala.swing._
import scala.swing.event._

class Screenshot(image: BufferedImage, cb: BufferedImage => Unit) extends Component {
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
        val rect = Utils.findBounds(fromPoint, toPoint)

        g.setPaint(Screenshot.translucentColour)
        g.fill(rect)

        g.setPaint(Screenshot.borderColour)
        g.draw(rect)

        if (rect.width > 50 && rect.height > 50) {
          val dims = s"${rect.width} x ${rect.height}"
          g.setPaint(Screenshot.textColour)
          Utils.drawCentredString(g, dims, rect)
        }
      }
      case _ => ()
    }
  }

  def commit(from: Point, to: Point): Unit = {
    println("Cropping and commiting image...")
    val rect = Utils.findBounds(from, to)
    val cropped = image.getSubimage(rect.x, rect.y, rect.width, rect.height)
    cb(cropped)
  }
}

object Screenshot {
  val translucentColour = new Color(0, 0, 0, 0xbb)
  val borderColour = new Color(0xff, 0xff, 0xff)
  val textColour = new Color(0xff, 0xff, 0xff)
}
