package dmk.converstionalui.nd4j

import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor
import org.nd4j.linalg.io.ClassPathResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import java.io.File
import java.util.Arrays
import java.util.List
import collection.JavaConversions

/**
 * borrowed and modified from 
 * http://deeplearning4j.org/word2vec
 */
class Word2VecDemo {
 val log: Logger = LoggerFactory.getLogger(classOf[Word2VecDemo])
 
  def init(): Unit = {
    var word2Vec = loadModel 
    
    
    // overfit
    cosSimForWords(word2Vec, "people", "money")
    cosSimForWords(word2Vec, "time", "money")
    cosSimForWords(word2Vec, "school", "play")
    cosSimForWords(word2Vec, "government", "business")
    cosSimForWords(word2Vec, "day", "night")
    cosSimForWords(word2Vec, "have", "made")
    cosSimForWords(word2Vec, "work", "war")

    cosSimForWords(word2Vec, "little", "big")

    cosSimForWords(word2Vec, "document", "running")
    cosSimForWords(word2Vec, "case", "eat")

    var words = Array("country", "man", 
        "president", "court", "music", 
        "document", "house", "five", 
        "money", "time", "life", "public", "little")
    words.foreach { x =>  
      wordsNearest(word2Vec, x)
      simTo(word2Vec, x)
    }
    
    wordsAlgebra(word2Vec, Arrays.asList("country", "president"), Arrays.asList("man")) 
    wordsAlgebra(word2Vec, Arrays.asList("time", "people"), Arrays.asList("money")) 
    // This response is awesome, [time, people] - [money] => [best, world, all, part, fami, very, on, long, may, at]
 }
 
  def loadModel(): Word2Vec = {
    val file = new File(Word2VecDemo.MODEL)
    var word2Vec: Word2Vec = null
    if(file.exists()) {
      log.info(s"using existing model at $file")
      word2Vec = WordVectorSerializer.loadFullModel(Word2VecDemo.MODEL)
    } else {
      val iter = createIter()
      val tokenizer = tokenizeModel()
      log.info(s"no model found at $file, training new model, this might take 30 mins...")
      word2Vec = trainModel(tokenizer, iter)
      log.info(s"saving model to $file for future")
      WordVectorSerializer.writeFullModel(word2Vec, Word2VecDemo.MODEL)
    }
    word2Vec
  }
 
  def cosSimForWords(vec: Word2Vec, word1: String, word2: String): Unit = {
    val cosSim = vec.similarity(word1, word2)
    log.info(s"Similarity between $word1, $word2: $cosSim")
  }
  
  def simTo(vec: Word2Vec, word: String): Unit = {
    val t = vec.similarWordsInVocabTo(word, .7d)  
    log.info(s"Similar words to $word, $t")
  }
  
  def wordsNearest(vec: Word2Vec, word: String): Unit = {
    val lst3 = vec.wordsNearest(word, 10)
		log.info(s"Similar words to '$word': $lst3")
  }
  
  def wordsAlgebra(vec: Word2Vec, positive: List[String], negative: List[String]): Unit = {
    val lst3 = vec.wordsNearest(positive, negative, 10)
    log.info(s"Word algebra, $positive - $negative => $lst3")
  }

  
  def createIter(): SentenceIterator = {
    log.info("Load data....")
    val resource = new ClassPathResource("raw_sentences.txt")
    val iter = new LineSentenceIterator(resource.getFile())
    iter.setPreProcessor(new SentencePreProcessor() {
        @Override
        def preProcess(sentence: String): String = {
            return sentence.toLowerCase()
        }
    })
    
    iter
  }
  
  def tokenizeModel(): TokenizerFactory = {
    log.info("Tokenize data....")
    val preProcessor = new EndingPreProcessor()
    val tokenizer = new DefaultTokenizerFactory()
    tokenizer.setTokenPreProcessor(new TokenPreProcess() {
        @Override
        def preProcess(str: String): String = {
            val token = str.toLowerCase()
            var base = preProcessor.preProcess(token)
            base = base.replaceAll("\\d", "d")
            if (base.endsWith("ly") || base.endsWith("ing"))
                System.out.println()
            return base
        }
    })

    tokenizer
  }
  
  def trainModel(tokenizer: TokenizerFactory, iter: SentenceIterator): Word2Vec = {
    val batchSize = 1000
    val iterations = 3
    val layerSize = 150
       
    log.info("Build model....")
    val vec = new Word2Vec.Builder()
            .workers(8)
            .batchSize(batchSize) //# words per minibatch.
            .minWordFrequency(5) // 
            .useAdaGrad(false) //
            .layerSize(layerSize) // word feature vector size
            .iterations(iterations) // # iterations to train
            .learningRate(0.025) // 
            .minLearningRate(1e-3) // learning rate decays wrt # words. floor learning
            .negativeSample(10) // sample size 10 words
            .iterate(iter) //
            .tokenizerFactory(tokenizer)
            .build()
    vec.fit()
    
    vec
  }
  
}

object Word2VecDemo {
  
  val MODEL = "RawSentencesModel.txt"
  def main(args: Array[String]) {
    new Word2VecDemo().init
  }
}