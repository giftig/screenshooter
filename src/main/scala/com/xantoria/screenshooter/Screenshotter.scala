package com.xantoria.screenshooter

import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import javax.imageio.ImageIO
import scala.swing._

class Screenshotter(
  screenshot: BufferedImage,
  rootDir: File = new File(sys.env("HOME"), "Desktop")
) extends SimpleSwingApplication {
  private lazy val screenshotDest: File = {
    val format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    val now = format.format(new Date)
    val id = UUID.randomUUID.toString.filter { _ != '-' } take 8
    new File(rootDir, s"screenshot_${now}_$id.png")
  }
  private val mainPanel = new Screenshot(screenshot, saveAndExit)

  private def saveAndExit(image: BufferedImage): Unit = {
    ImageIO.write(image, "png", screenshotDest)
    shutdown()
    sys.exit(0)
  }

  mainPanel.preferredSize = Utils.screenDimensions

  def top = new MainFrame {
    // Go fullscreen
    peer.setUndecorated(true)
    val dev = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
    if (dev.isFullScreenSupported) {
      dev.setFullScreenWindow(peer)
    }

    title = "Screenshotter"
    contents = mainPanel
  }
}
