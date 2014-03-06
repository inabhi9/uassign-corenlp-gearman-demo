package org.abhi9.gearman;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CoreNlp {
	
	private StanfordCoreNLP pipeline;
	
	public CoreNlp(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	public String parse(String text){
		
		String output = ""; 
		
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		 
		// run all Annotators on this text
		this.pipeline.annotate(document);
		 
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values
		// with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		 
		for(CoreMap sentence: sentences) {
			//traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			  // this is the NER label of the token
			  String ne = token.get(NamedEntityTagAnnotation.class);
			  // if not NER, skip it
			  if (ne.equalsIgnoreCase("O")) continue;
			  // appending ner to final output
			  output += token + ", ";
			}
		}
		
		return output;
	}
	
	public static void main(String[] args){
		CoreNlp corenlp = new CoreNlp();
		System.out.print(corenlp.parse("Google is awesome company. Apple is king"));
	}
}


