package dmk.converstionalui.cmusphinx

import edu.cmu.sphinx.api.Configuration
import edu.cmu.sphinx.api.LiveSpeechRecognizer

/**
 * Taken and modified from http://cmusphinx.sourceforge.net/wiki/tutorialsphinx4
 */
class TranscriberDemo2 {

  def init(): Unit = {

    val configuration = new Configuration()
    configuration
      .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us")
    configuration
      .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict")
    configuration
      .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

    val recognizer = new LiveSpeechRecognizer(configuration)

    recognizer.startRecognition(true)
    var result = recognizer.getResult()
    while (result != null) {
      System.out.format("Hypothesis: %s\n", result.getHypothesis())
      result = recognizer.getResult()
    }

    recognizer.stopRecognition()
  }

}

object TranscriberDemo2 {

  def main(args: Array[String]): Unit = {
    new TranscriberDemo2().init
  }

}