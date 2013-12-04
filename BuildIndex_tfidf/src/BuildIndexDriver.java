import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.*;

import edu.umd.cloud9.collection.wikipedia.*;

public class BuildIndexDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new BuildIndexDriver(), args);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// JobClient client = new JobClient();
		JobConf conf = new JobConf(BuildIndexDriver.class);

		conf.setInputFormat(WikipediaPageInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		Path outputPath = new Path("output");
		FileInputFormat.addInputPath(conf, new Path("input"));
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
		// calculate statistics for tf-idf
		JobConf mapConf3 = new JobConf(false);
		ChainReducer.addMapper(conf, WordStatisticsMapper.class, Text.class,
				Text.class, Text.class, Text.class, true, mapConf3);

		RunningJob job = JobClient.runJob(conf);
		job.waitForCompletion();

		long docsNum = job
				.getCounters()
				.findCounter("org.apache.hadoop.mapred.Task$Counter",
						"MAP_INPUT_RECORDS").getValue();
		long tokensNum = job
				.getCounters()
				.findCounter("org.apache.hadoop.mapred.Task$Counter",
						"REDUCE_OUTPUT_RECORDS").getValue();
		
		System.out.println(docsNum);
		System.out.println(tokensNum);
		return 0;
	}
}
