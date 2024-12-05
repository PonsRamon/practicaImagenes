package org.example

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.sign

fun main(args: Array<String>) {
    if (args.size == 0) {
        println("No se han pasado argumentos")
    } else {
        val directory = File(args[0])
        val threads = mutableListOf<Thread>()
        var cont = 0
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            for (file in files) {
                if (file.name.endsWith(".jpg") || file.name.endsWith(".jpeg")) {
                    if (!file.name.startsWith("gray_") && !file.name.startsWith("border_")) {
                        val sobelRunnable = Sobel(directory, file)
                        val thread = Thread(sobelRunnable)
                        thread.start()
                        threads.add(thread)
                        cont++
                        //println("lanzado hilo $cont")
                    }
                }
            }
        } else {
            println("El directorio '$directory' no existe")
        }

        while (threads.filter { it.isAlive }.isNotEmpty()) {
            println("Ejecutando los hilos...")
            Thread.sleep(100)
        }
    }
}

