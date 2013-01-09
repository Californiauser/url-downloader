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
import scala.actors.Actor

// ----------------------------------------------------------
// Actors messages
// ----------------------------------------------------------

// Message for start downloading a file
case object DownloadFile
// Message for notify that a the file has finished downloading
case object DownloadComplete
// Message for notify that an error happened in the download process
case class DownloadError(val message: Throwable)

// ---------------------------------------------------------
// Actors
// ---------------------------------------------------------
/**
 * This Actor tries to Download a file by his URL in the path specified by downloadPath
 */
class DownloadFileActor(val downloadPath: String, val fileUrl: String) extends Actor {
  override def act() {
    //while (true) {
    loop {
      //Actor.receive {
      Actor.react {
        case DownloadFile =>
          println("Starting download for file '%s'".format(fileUrl))
          try {
            UrlDownloader.downloadFile(downloadPath, fileUrl)
            Actor.sender ! DownloadComplete
          } catch {
            case e: Exception => sender ! DownloadError(e)
          }
          Actor.exit()
        case other =>
          println("'%s' has received unexpected message '%s'".format(this, other))
          Actor.exit()
      }
    }
  }
}

/**
 * This actor is used to start downloading a file by sending a message to the
 * DownloadFileActor
 */
class FileRequesterActor(downloadFileActor: DownloadFileActor) extends Actor {
  override def act() {
    downloadFileActor ! DownloadFile
    //while (true) {
    loop {
      //Actor.receive {
      Actor.react {
        case DownloadComplete =>
          println("Download is complete for file '%s'....".format(downloadFileActor.fileUrl))
          Actor.exit()
        case DownloadError(exception) =>
          println("There was a problem trying to download file '%s': '%s'".format(downloadFileActor.fileUrl, exception))
          Actor.exit()
        case other =>
          println("'%s' has received unexpected message '%s'".format(this, other))
          Actor.exit()
      }
    }
  }
}

// ---------------------------------------------------------
// Utility objects
// ---------------------------------------------------------

/**
 * Utility object for download a file from an URL
 */
object UrlDownloader {

  def downloadFile(desinationPath: String, fileUrl: String) = {
    //println("downloading file {%s}... ".format(fileUrl))
    val url: URL = new URL(fileUrl)

    val nameOfDownloadedFile = fileUrl.split("/").last

    val bufferedInputStream = new BufferedInputStream(url.openStream())
    val fileOutputStream = new FileOutputStream(desinationPath + File.separatorChar + nameOfDownloadedFile)
    val bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
    val buffer: Array[Byte] = new Array[Byte](2048)

    Iterator.continually(bufferedInputStream.read(buffer)) takeWhile (_ != -1) foreach (bytes => {
      bufferedOutputStream.write(buffer, 0, bytes)
      bufferedOutputStream.flush()
    })

    bufferedInputStream.close()
    bufferedOutputStream.close()

    //println("done with file {%s}!, downloaded file {%s} in {%s}".format(fileUrl, nameOfDownloadedFile, desinationPath))
    //println()
  }
}
