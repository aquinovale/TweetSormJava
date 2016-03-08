package openNLP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class example {
	
	static int LIMIT_CHARACTER = 140;

	public static void main(String[] args) {
		SentenceModel model = InitializeLanguage(args);
		ArrayList<String> list = DetectSentence(new SentenceDetectorME(model), args[1]);
		int pos = 1;
		for(String sentences : list){
			System.out.println(pos+"/"+list.size()+" "+sentences);
			pos++;
		}

	}

	private static ArrayList<String> DetectSentence(SentenceDetectorME sentenceDetector, String msg) {
		String sentences[] = sentenceDetector.sentDetect(msg);
		ArrayList<String> newSentences = new ArrayList<String>();
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < sentences.length; i++){
			if (sentences[i].length() < 140){
				text.append(sentences[i]);
				if((i < sentences.length-1) && (sentences[i].length() + sentences[i+1].length() < 140)){
					continue;
				}else{
					newSentences.add(text.toString());
					text = new StringBuilder();
				}
			}else{
				sizeLimit(sentences[i], ",", newSentences);
			}
		}
		return newSentences;
	}

	private static void sizeLimit(String line, String comma, ArrayList<String> newSentences) {
		StringTokenizer token = new StringTokenizer(line, comma);
		StringBuilder tweet = new StringBuilder(); 
		int tokenSize = 0;
		while (token.hasMoreTokens()) {
			String word = token.nextToken();
			tokenSize = tokenSize + word.length() + comma.length();
			if(tokenSize < LIMIT_CHARACTER){
				tweet.append(word+comma);
				if(!token.hasMoreTokens()){
					newSentences.add(tweet.toString());
				}
			}else{
				if(tweet.length() > 0){
					newSentences.add(tweet.toString());
				}
				if(comma == ","){
					sizeLimit(word, " ", newSentences);
				}else{
					tweet = new StringBuilder();
					tweet.append(word+comma);
					tokenSize = tweet.length();
				}
			}
		}
	}

	private static SentenceModel InitializeLanguage(String[] args) {
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			return new SentenceModel(modelIn);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}