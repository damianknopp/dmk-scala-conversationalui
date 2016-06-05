package dmk.converstionalui
  
import javax.sound.sampled._
import java.io._
import javax.sound.sampled.DataLine
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.LineUnavailableException
import java.nio.file.Paths
import javax.sound.sampled.TargetDataLine
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem


class RecordDemo {
  
  var line: TargetDataLine = null
  val wavFile = Paths.get("tmp.wav")

  def start(): Unit = {
    try {
      val format = genAudioFormat()
      val info = new DataLine.Info(classOf[TargetDataLine], format)

      // checks if system supports the data line
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line not supported")
        System.exit(0)
      }
      
      val wav = wavFile.toFile()
      if(wav.exists()) {
        wav.delete()
      }

      line = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      line.open(format)
      line.start() // start capturing

      System.out.println("Start capturing...")

      val ais = new AudioInputStream(line)

      System.out.println("Start recording...")
      
      // start recording
      AudioSystem.write(ais, RecordDemo.FILE_TYPE, wav)

    } catch { 
      case lue: LineUnavailableException => lue.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  def finish(): Unit = {
    line.stop()
    line.close()
    System.out.println("Finished")
  }
  
  def genAudioFormat(): AudioFormat = {
    val sampleRate = 16000
    val sampleSizeInBits = 16
    val channels = 1
    val signed = true
    val bigEndian = true
    val format = new AudioFormat(sampleRate, sampleSizeInBits,
                       channels, signed, bigEndian)
    format
  }
  
  def init(): Unit = {
    val recorder = new RecordDemo()

    // creates a new thread that waits for a specified
    // of time before stopping
    val recorderThread = new Thread(new Runnable() {
      def run(): Unit = {
        try {
          // start recording
          recorder.start()
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    })
    recorderThread.start()
    Thread.sleep(RecordDemo.RECORD_TIME)
    recorder.finish()
    
    System.exit(1)
  }
  
}

object RecordDemo {
  
  def main(args: Array[String]) {
    new RecordDemo().init
  }

  val RECORD_TIME = 1000 * 10 //millis
  val FILE_TYPE = AudioFileFormat.Type.WAVE

}