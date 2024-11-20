package org.example

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.sign

fun main() {
    val directory = File("./images")

    var cont = 0
    if(directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        for (file in files) {
            if(file.name.endsWith(".jpg") || file.name.endsWith(".jpeg")) {
                if(!file.name.startsWith("gray_") && !file.name.startsWith("border_")) {
                    val sobelRunnable = Sobel(directory,file)
                    val thread = Thread(sobelRunnable)
                    thread.start()
                    cont++
                    println("lanzado hilo $cont")
                }
            }
        }
    }

}

