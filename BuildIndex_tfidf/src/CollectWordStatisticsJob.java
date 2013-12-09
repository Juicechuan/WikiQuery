import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider.Text;

public class CollectWordStatisticsJob {

	public static final String NUMBER_OF_DOCUMENTS_KEY = "documents.num";
	public static final String NUMBER_OF_TOKENS_KEY = "tokens.num";

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out
					.println("Usage: <input path> <immediate output path> <Output path>");
			System.exit(1);
		}
		Configuration conf = new Configuration();
		RunningJob firstJob = BuildIndexJob.createJob(args[0], args[1], conf);
		firstJob.waitForCompletion();
//		Path immediate_index = new Path(args[1]);
//		immediate_index.getFileSystem(conf).delete(immediate_index, true);
		long numDocs = firstJob
				.getCounters()
				.findCounter("org.apache.hadoop.mapred.Task$Counter",
						"MAP_INPUT_RECORDS").getValue();
		long numTokens = firstJob
				.getCounters()
				.findCounter("org.apache.hadoop.mapred.Task$Counter",
						"REDUCE_OUTPUT_RECORDS").getValue();
		RunningJob secondJob = createJob(args[1], args[2], conf, numTokens,
				numDocs);
		secondJob.waitForCompletion();
	}

	private static RunningJob createJob(String input, String output,
			Configuration config, long numDocs, long numTokens)
			throws IOException {
		JobConf conf = new JobConf(config, CollectWordStatisticsJob.class);
		conf.setLong(NUMBER_OF_DOCUMENTS_KEY, numDocs);
		conf.setLong(NUMBER_OF_TOKENS_KEY, numTokens);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		Path outputPath = new Path(output);
		FileInputFormat.addInputPath(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, outputPath);

		outputPath.getFileSystem(conf).delete(outputPath, true);
		
		conf.setMapperClass(WordStatisticsMapper.class);
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Text.class);
	    conf.setOutputKeyClass(Text.class);
	    conf.setOutputValueClass(Text.class);
		conf.setNumReduceTasks(0);
		
		RunningJob job = JobClient.runJob(conf);
		return job;
	}

}
