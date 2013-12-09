import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.umd.cloud9.collection.wikipedia.WikipediaPageInputFormat;

public class BuildIndexJob {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage:<input path> <outputpath>");
			System.exit(1);
		}
		Configuration conf = new Configuration();
		RunningJob RJob = createJob(args[0], args[1], conf);

		RJob.waitForCompletion();
	}

	public static RunningJob createJob(String input, String output,
			Configuration config) throws IOException {
		JobConf conf = new JobConf(config, BuildIndexJob.class);

		conf.setInputFormat(WikipediaPageInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		Path outputPath = new Path(output);
		FileInputFormat.addInputPath(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, outputPath);

		outputPath.getFileSystem(conf).delete(outputPath, true);

		JobConf mapConf1 = new JobConf(false);
		// build basic inverted index
		ChainMapper.addMapper(conf, BuildIndexMapper.class, LongWritable.class,
				WikipediaPage.class, Text.class, Text.class, true, mapConf1);

		JobConf mapConf2 = new JobConf(false);
		// do stopwords elimination based on stopword set for English
		ChainMapper.addMapper(conf, StopwordsRemovalMapper.class, Text.class,
				Text.class, Text.class, Text.class, true, mapConf2);
		// write the posting list
		JobConf reduceConf1 = new JobConf(false);
		ChainReducer.setReducer(conf, BuildIndexReducer.class, Text.class,
				Text.class, Text.class, Text.class, true, reduceConf1);
		reduceConf1.setNumReduceTasks(10);
		conf.setNumReduceTasks(10);

		RunningJob job = JobClient.runJob(conf);
		return job;
	}
}
