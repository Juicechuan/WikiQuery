import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

import edu.umd.cloud9.collection.wikipedia.*;

public class BuildInvertedIndex {
	
	public static class Map extends MapReduceBase implements
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
					posting.set(docId+','+Integer.toString(offset));
					word.set(m.group().toLowerCase());
					output.collect(word, posting);
					offset++;
				}
			}
		}
	}

	public static class Reduce extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
		private Text postinglist = new Text();

		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			HashMap<String,List<String>> uniquePos = new HashMap<String,List<String>>();

			for (; values.hasNext();) {
				String posting = values.next().toString();
				String docId = posting.split(",")[0];
				String offset = posting.split(",")[1];
				if (uniquePos.containsKey(docId)){
					//System.out.println(uniquePos.get(docId));
					uniquePos.get(docId).add(offset);
				}else{
					List<String> l = new ArrayList<String>();
					l.add(offset);
					uniquePos.put(docId, l);
				}
			}

			postinglist.set(new Text(StringUtils.join(uniquePos.entrySet(), ",")));
			output.collect(key, postinglist);
		}

	}

	public static void main(String[] args) throws Exception {

		JobConf conf = new JobConf(BuildInvertedIndex.class);

		conf.setInputFormat(WikipediaPageInputFormatOld.class);
		conf.setOutputFormat(TextOutputFormat.class);

		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		Path outputPath = new Path("output");
		FileInputFormat.addInputPath(conf, new Path("input"));
		FileOutputFormat.setOutputPath(conf, outputPath);

		outputPath.getFileSystem(conf).delete(outputPath, true);

		JobClient.runJob(conf);
	}

}
