import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public class StopwordsRemovalMapper extends MapReduceBase implements
		Mapper<Text, Text, Text, Text> {
	private Stopwords sw = new Stopwords();
	public void map(Text key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (sw.is(key.toString())){
			//System.out.println("Remove stop word:"+key.toString());
		}else{
			output.collect(key, value);
		}
	}


}
