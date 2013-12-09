import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class WordStatisticsMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {

	private long numDocs;
	private long numTokens;

	private int currentIndex = 0;
	private Text keyOut = new Text();
	private Text valOut = new Text();

	@Override
	public void configure(JobConf conf) {
		// to do
		numDocs = conf.getLong(
				CollectWordStatisticsJob.NUMBER_OF_DOCUMENTS_KEY, 1);
		numTokens = conf.getInt(CollectWordStatisticsJob.NUMBER_OF_TOKENS_KEY,
				1);
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {

		String val = "";
		String line = value.toString();
		String[] sp = line.split("\\s", 2);
		String token = sp[0];
		String postings = sp[1];
		double idf = 0d;

		// process value
		List<String> postinglist = Arrays.asList(StringUtils.split(postings,
				"|"));
		int docFreq = postinglist.size();
		idf = FastMath.log(numDocs) - FastMath.log(docFreq);
		// List<Integer> termFreqList = new ArrayList<Integer>();
		// for (String s : postinglist) {
		// if (!s.isEmpty()) {
		// String[] split = s.split("=");
		// // String docID = split[0];
		// int termFreq = split[1].substring(1, split[1].length() - 1)
		// .split(",").length;
		// termFreqList.add(termFreq);
		// }
		// }
		val = postings + ";" + Double.toString(idf) + ";"
				+ Integer.toString(currentIndex);

		currentIndex++;
		keyOut.set(token);
		valOut.set(val);
		output.collect(keyOut, valOut);

	}
}
