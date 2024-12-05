package org.example

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Sobel(private val directory: File, private val file: File) : Runnable {

    fun convertImageToGray(image: BufferedImage): BufferedImage {
        val bufferedImageGray = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        for (x in 0..<image.width) {
            for (y in 0..<image.height) {
                val color = image.getRGB(x, y)
                val colorRGB = Color(color)

                val red = colorRGB.getRed()
                val green = colorRGB.getGreen()
                val blue = colorRGB.getBlue()

                val gray = red * 0.3 + green * 0.59 + blue * 0.11

                val grayColor = Color(gray.toInt(), gray.toInt(), gray.toInt())

                bufferedImageGray.setRGB(x, y, grayColor.getRGB())
            }
        }

        return bufferedImageGray
    }

    fun sobel(bufferedImageGray: BufferedImage): BufferedImage {
        val bufferedImage = BufferedImage(bufferedImageGray.width, bufferedImageGray.height, BufferedImage.TYPE_INT_RGB)

        var cx: Int
        var cy: Int

        for (i in 0..<bufferedImageGray.width) {
            for (j in 0..<bufferedImageGray.height) {

                val matriz = arrayOf(
                    computeValue(bufferedImageGray, i - 1, j - 1),
                    computeValue(bufferedImageGray, i - 1, j),
                    computeValue(bufferedImageGray, i - 1, j + 1),
                    computeValue(bufferedImageGray, i, j - 1),
                    computeValue(bufferedImageGray, i, j),
                    computeValue(bufferedImageGray, i, j + 1),
                    computeValue(bufferedImageGray, i + 1, j - 1),
                    computeValue(bufferedImageGray, i + 1, j),
                    computeValue(bufferedImageGray, i + 1, j + 1)
                )

                cx = calculateCx(matriz)
                cy = calculateCy(matriz)

                //println("cx: $cx, cy: $cy")
                val g = pitagoras(cx, cy)
                //println("g: $g")
                var numColor = 0

                if (g < 0) {
                    numColor = 0
                } else if (g > 200) {
                    numColor = 255
                } else {
                    numColor = g.toInt()
                }

                val newColor = Color(numColor, numColor, numColor)

                bufferedImage.setRGB(i, j, newColor.getRGB())
            }

        }
        return bufferedImage
    }

    fun computeValue(b: BufferedImage, i: Int, j: Int): Int {
        if (i < 0 || i > b.width - 1 || j < 0 || j > b.height - 1) {
            return 0
        }

        return Color(b.getRGB(i, j)).red
    }

    fun calculateCx(matriz: Array<Int>): Int {
        var cx: Int = 0

        val gx = arrayOf(
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
        )

        for (i in gx.indices) {
            cx += gx[i] * matriz[i]
        }

        return cx
    }

    fun calculateCy(matriz: Array<Int>): Int {
        var cy: Int = 0

        val gy = arrayOf(
            -1, -2, -1,
            0, 0, 0,
            1, 2, 1
        )

        for (i in gy.indices) {
            cy += gy[i] * matriz[i]
        }

        return cy
    }

    fun pitagoras(cx: Int, cy: Int): Double {
        return Math.sqrt((Math.pow(cx.toDouble(), 2.0)) + (Math.pow(cy.toDouble(), 2.0)))
    }

    override fun run() {
        val imageOriginal = ImageIO.read(file.inputStream())

        val bufferedImageGray = convertImageToGray(imageOriginal)
        val fileNameGray = File("$directory/gray_${file.name}")
        ImageIO.write(bufferedImageGray, "jpeg", fileNameGray)

        val bufferedImageBorder = sobel(bufferedImageGray)
        val fileNameBorder = File("$directory/border_${file.name}")
        ImageIO.write(bufferedImageBorder, "jpeg", fileNameBorder)

        println("Terminado")
    }
}