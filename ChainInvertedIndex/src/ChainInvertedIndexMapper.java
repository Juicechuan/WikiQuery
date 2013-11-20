import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public class ChainInvertedIndexMapper extends MapReduceBase implements
		Mapper<LongWritable, WikipediaPage, Text, Text> {
	private Text posting = new Text();
	private Text word = new Text();

	Pattern TOKEN = Pattern.compile("\\w+");

	public void map(LongWritable key, WikipediaPage value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String content = value.getTitle() + ' ' + value.getContent();
		String docId = value.getDocid();
		int offset = 0;

		for (String token : StringUtils.split(content)) {
			Matcher m = TOKEN.matcher(token);
			while (m.find()) {
				posting.set(docId + ',' + Integer.toString(offset));
				word.set(m.group().toLowerCase());
				output.collect(word, posting);
				offset++;
			}
		}
	}
}