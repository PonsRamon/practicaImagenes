package org.example

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.sign

fun main() {
    val image: Image
    val fileIn = File("./images/limaa.jpeg")
    val fileGray = File("./images/limaa_gray.jpeg")
    val fileBorder = File("./images/limaa_border.jpeg")

    val directory = File("./images")

    if(directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        for (file in files) {
            if(file.name.endsWith(".jpg") || file.name.endsWith(".jpeg")) {
                val imageOriginal = ImageIO.read(file.inputStream())
                val bufferedImageGray = convertImageToGray(imageOriginal)
                val fileNameGray = File("$directory/gray_${file.name}")
                ImageIO.write(bufferedImageGray, "jpeg", fileNameGray)
                val bufferedImageBorder = sobel(bufferedImageGray)
                val fileNameBorder = File("$directory/border_${file.name}")
                ImageIO.write(bufferedImageBorder, "jpeg", fileNameBorder)
            }
        }
    }




    /*val bufferedImage = ImageIO.read(fileIn)
    val bufferedImageGray = convertImageToGray(bufferedImage)

    ImageIO.write(bufferedImageGray, "jpeg", fileGray)

    val readbufferedImageGray = ImageIO.read(fileGray)

    val borderImage = sobel(readbufferedImageGray)

    ImageIO.write(borderImage, "jpeg", fileBorder)*/

}

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

    for (i in 1..<bufferedImageGray.width - 1) {
        for (j in 1..<bufferedImageGray.height - 1) {
            val matriz = arrayOf(
                Color(bufferedImageGray.getRGB(i - 1, j - 1)).red, Color(bufferedImageGray.getRGB(i - 1, j)).red, Color(bufferedImageGray.getRGB(i - 1, j + 1)).red,
                Color(bufferedImageGray.getRGB(i, j - 1)).red, Color(bufferedImageGray.getRGB(i, j)).red, Color(bufferedImageGray.getRGB(i, j + 1)).red,
                Color(bufferedImageGray.getRGB(i + 1, j - 1)).red, Color(bufferedImageGray.getRGB(i + 1, j)).red, Color(bufferedImageGray.getRGB(i + 1, j + 1)).red
            )
            cx = calculateCx(matriz)
            cy = calculateCy(matriz)

            //println("cx: $cx, cy: $cy")
            val g = pitagoras(cx, cy)
            //println("g: $g")
            var numColor = 0

            if (g < 0) {
                numColor = 0
            } else if (g > 100) {
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

fun calculateCx(matriz:Array<Int>): Int {
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

fun calculateCy(matriz:Array<Int>): Int {
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

fun pitagoras(cx:Int, cy:Int): Double {
    return Math.sqrt((Math.pow(cx.toDouble(),2.0)) + (Math.pow(cy.toDouble(),2.0)))
}