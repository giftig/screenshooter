package com.xantoria.screenshooter

import java.awt.{Point, Rectangle}

object Main {
  def main(args: Array[String]): Unit = {
    val shot = Utils.captureScreen()
    val screenshotter = new Screenshotter(Utils.bufferImage(shot))
    screenshotter.main(Array.empty[String])
  }
}
