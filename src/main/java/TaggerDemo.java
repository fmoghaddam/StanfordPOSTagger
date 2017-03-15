import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TaggerDemo  {

	private static Logger LOG = Logger.getLogger(TaggerDemo.class);

	private TaggerDemo() {}

	public static void main(String[] args) throws Exception {

		Properties props = new Properties();

		props.setProperty("annotators","tokenize, ssplit, pos");

		final MaxentTagger tagger = new MaxentTagger("english-bidirectional-distsim.tagger",props);
		final File input = new File("input/text.txt");

		final List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(input)));
		for (final List<HasWord> sentence : sentences) {
			final List<TaggedWord> tSentence = tagger.tagSentence(sentence);
//			/LOG.info(SentenceUtils.listToString(tSentence, false));
		}
	}

}