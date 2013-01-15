package urldownloader

import scala.io._
import java.io.OutputStream
import java.io.FileWriter
import java.net.URL
import java.io.File
import java.net.HttpURLConnection
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.FileOutputStream
import java.lang.Math
import java.io.BufferedInputStream
import scala.collection.Iterator

// Download simple
/**
 * object CursoAntiayudaDownloader {
 *
 * def fileUrlList = {
 * ((1 to 100) map (i => "http://media.metro951.com/metroymedio/audio/peto%d.MP3".format(i))) toList
 * }
 *
 * def main(args: Array[String]) {
 * fileUrlList foreach (fileUrl => UrlDownloader downloadFile ("/home/lucas/Music", fileUrl))
 * }
 *
 * }*
 */

// Download with multiple threads
object CursoAntiayudaDownloader {

  def fileUrlList = {
    ((1 to 100) map (i => "http://media.metro951.com/metroymedio/audio/peto%d.MP3".format(i))) toList
  }

  def main(args: Array[String]) {
    println("start main...")
    
    fileUrlList foreach (fileUrl => {
      val downloadFileActor = new DownloadFileActor("/home/lucas/tmp", fileUrl)
      val fileRequester = new FileRequesterActor(downloadFileActor)
      downloadFileActor.start()
      fileRequester.start()
    })

    println("end main...")
  }

}
