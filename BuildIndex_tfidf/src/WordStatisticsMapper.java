import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
//import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class WordStatisticsMapper extends MapReduceBase implements
		Mapper<Text, Text, Text, Text> {
	
	private long numDocs;
	
	private int currentIndex = 0;
	private Text valOut = new Text();
	
	@Override
	public void configure(JobConf conf){
		//to do
	}
	
	@Override
	public void map(Text key, Text value, OutputCollector<Text, Text> output,
			Reporter reporter) throws IOException {

		String val = "";
		String postings = value.toString();

		// process value
		List<String> postinglist = Arrays.asList(StringUtils.split(postings,"|"));
		int docFreq = postinglist.size();
		List<Integer> termFreqList = new ArrayList<Integer>();
		for (String s : postinglist) {
			if (!s.isEmpty()) {
				String[] split = s.split("=");
				// String docID = split[0];
				int termFreq = split[1].substring(1, split[1].length() - 1)
						.split(",").length;
				termFreqList.add(termFreq);
			}
		}
		val = postings + ";" + StringUtils.join(termFreqList, ",") + ";"
				+ Integer.toString(docFreq) + ";"
				+ Integer.toString(currentIndex);

		currentIndex++;
		valOut.set(new Text(val));
		output.collect(key, valOut);
	}
}
