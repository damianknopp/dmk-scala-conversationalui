package dmk.converstionalui

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

import edu.cmu.sphinx.api.Configuration
import edu.cmu.sphinx.api.SpeechResult
import edu.cmu.sphinx.api.StreamSpeechRecognizer

/**
 * Taken and modified from http://cmusphinx.sourceforge.net/wiki/tutorialsphinx4
 */
class TranscriberDemo {       

  def init(): Unit = {
    
        val configuration = new Configuration()
        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us")
        configuration
                .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict")
        configuration
                .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

        val recognizer = new StreamSpeechRecognizer(
                configuration)
        val stream = new FileInputStream(new File("tmp.wav"))

        recognizer.startRecognition(stream)

        var result = recognizer.getResult()
        while (result != null) {
          System.out.format("Hypothesis: %s\n", result.getHypothesis())
          result = recognizer.getResult()
        }
        
        recognizer.stopRecognition()
  }

}

object TranscriberDemo {

  def main(args: Array[String]) : Unit = {
    new TranscriberDemo().init
  }
  
}