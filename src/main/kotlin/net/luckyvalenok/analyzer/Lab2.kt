package net.luckyvalenok.analyzer

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

fun main() {
    TowerOfHanoi.start(8)

    FractalDrawer(CantorSet()).run()
}

class TowerOfHanoi(private val name: Char, discs: IntArray) {
    val discs = discs.toMutableList()

    companion object {

        private var step = 0

        @JvmStatic
        fun start(n: Int) {
            val discs = IntArray(n) { it + 1 }

            val first = TowerOfHanoi('A', discs)
            val second = TowerOfHanoi('B', IntArray(0))
            val third = TowerOfHanoi('C', IntArray(0))

            println("$first, $second, $third")
            move(first, second, third, n)
        }

        private fun move(x: TowerOfHanoi, y: TowerOfHanoi, z: TowerOfHanoi, n: Int) {
            if (n <= 0) return
            move(x, z, y, n - 1)
            x.discs.remove(n)
            z.discs.add(n)
            println(String.format("%4d шаг | %s из %s в %s.", ++step, n, x, z))
            move(y, x, z, n - 1)
        }
    }

    override fun toString(): String {
        return "$name=(${discs.toTypedArray().contentToString().removeSurrounding("[", "]")})"
    }

}

interface Fractal {
    fun draw(g: Graphics, x: Int, y: Int)
}

class CantorSet(
    val startLength: Int = 600,
    val minLength: Int = 1,
    val rectangleHeight: Int = 20,
    val spaceHeight: Int = 20,
    val color: Color = Color.WHITE
) : Fractal {

    override fun draw(g: Graphics, x: Int, y: Int) {
        g.color = color
        drawHelper(g, x, y, startLength)
    }

    private fun drawHelper(g: Graphics, x: Int, y: Int, length: Int) {
        if (length <= minLength) {
            return
        }

        g.fillRect(x, y, length, rectangleHeight)

        val newY = y + (rectangleHeight + spaceHeight)

        drawHelper(g, x, newY, length / 3)
        drawHelper(g, x + (length * 2 / 3), newY, length / 3)
    }

}

class FractalDrawer(private val fractal: Fractal) {
    private val width = 800
    private val height = 600
    val canvas: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val canvasGraphics: Graphics = canvas.graphics

    fun run() {
        val frame = JFrame()
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(width + 18, height + 24)
        frame.isVisible = true

        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                canvasGraphics.color = Color.BLACK
                canvasGraphics.fillRect(0, 0, width, height)

                fractal.draw(canvasGraphics, 10, 10)

                g.drawImage(canvas, 0, 0, width, height, this)
            }
        }
        frame.add(panel)
        panel.revalidate()
    }

}