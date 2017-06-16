package com.xantoria.screenshooter

import java.awt.{Point, Rectangle}

class Main {
  def main(args: List[String]): Unit = {
    val shot = Utils.captureScreen()
    val screenshotter = new Screenshotter(Utils.bufferImage(shot))
    screenshotter.main(Array.empty[String])
  }
}
