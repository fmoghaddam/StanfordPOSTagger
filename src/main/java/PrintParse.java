import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class PrintParse {

	private static final Map<String,String> SOV_MAP = new LinkedHashMap<>();
	private static Logger LOG = Logger.getLogger(PrintParse.class);
	public static void main(String[] args) throws FileNotFoundException {

		final File input = new File("input");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		for (final File file : input.listFiles()) {
			if (file.isFile() && FilenameUtils.getExtension(file.getName()).equals("txt")) {
				final List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(file)));
				for (final List<HasWord> fullSentence : sentences) {
					Annotation document =
							new Annotation(fullSentence.toString());
					pipeline.annotate(document);
					for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
						Tree constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
						TreebankLanguagePack languagePack = new PennTreebankLanguagePack();
						GrammaticalStructure structure = languagePack.grammaticalStructureFactory().newGrammaticalStructure(constituencyParse);
						Collection<TypedDependency> typedDependencies = structure.typedDependenciesCollapsed();

						for(TypedDependency td : typedDependencies) {
							if(td.reln().toString().equals("nsubj")) {
								int startOfVerbIndex = 6;
								int endOfVerbIndex = td.toString().indexOf("-");  
								int startOfSubjectIndex = td.toString().indexOf(", ")+2;
								int endOfSubjectIndex = td.toString().lastIndexOf("-");
								String verb = td.toString().substring(startOfVerbIndex,endOfVerbIndex);
								String subject = td.toString().substring(startOfSubjectIndex,endOfSubjectIndex);
								SOV_MAP.put(subject ,verb);
							}
						}
					}
				}
				LOG.info("File: "+file.getName()+" ==>");
				for(Entry<String, String> entity: SOV_MAP.entrySet()){
					LOG.info("Subject: "+entity.getKey() + "\tVerb: "+entity.getValue());
				}	
				LOG.info("--------------------------------------------");
			}else {
				LOG.warn("Skipping " + file.getAbsolutePath()+". Either it is not a file or extension is not supported. Only TXT is supported for now. ");
			}
		}



	}

}