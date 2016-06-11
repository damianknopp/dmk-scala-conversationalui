# dmk-scala-conversationalui

Voice
--

To run
--
 scala RecordDemo

 scala TranscriberDemo

 pocketsphinx_continuous -infile ~/workspace/dmk-scala-conversationalui/tmp.wav -lm corpus-test.1/1675.lm -dict corpus-test.1/1675.dic

Tools
---
http://cmusphinx.sourceforge.net/wiki/tutorialsphinx4

 pocketsphinx_continuous -inmic yes -lm corpus-test.2/6939.lm -dict corpus-test.2/6939.dic

 pocketsphinx_continuous -jsgf hello.jsgf -inmic yes

http://espeak.sourceforge.net/

  ./speak -v en-us+f2 --path ./ "a brief history of time" -b 1
