package edu.wiki.query;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.ReflectionUtils;

public class LocateWord {

	private String InputDir;
	private String OutputDir;

	public LocateWord(String InputDir, String OutputDir) {
		this.InputDir = InputDir;
		this.OutputDir = OutputDir;
	}

	public String doLocateWord(String words) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		conf.set("words", words);
		// conf.set("location",location);

		Job job = new Job(conf, "LocateWord");
		job.setJarByClass(LocateWord.class);
		job.setMapperClass(LocateWordMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		// job.setOutputFormatClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(this.InputDir));
		FileOutputFormat.setOutputPath(job, new Path(this.OutputDir));
		job.waitForCompletion(true);
		// System.out.println(result);
		String r = result;
		result = "";
		if (r.length() >= 1) {
			return r.substring(0, r.length() - 1);
		} else
			return r;

	}

	private static String result = "";

	public static class LocateWordMapper extends Mapper<Object, Text, Text, Text> {

		private String words = "";

		// private String location;
		public void setup(Context context) {
			words = context.getConfiguration().get("words");

			// location = context.getConfiguration().get("location");
		}

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			Text first = new Text();
			Text other = new Text();
			// System.out.println(words);
			String[] wordsToSearch = words.split(";");

			// word1 111=[11,11]|222=[22,22];word2 333=[333,333]
			String[] s = value.toString().split("\\s+", 2);
			String wordGet = s[0].trim();
			for (String wordToSearch : wordsToSearch) {
				if (wordGet.equals(wordToSearch)) {
					// System.out.println(wordGet);
					result += wordToSearch + " " + s[1].trim() + ":";
					// System.out.println(result);
					first.set(wordGet);
					other.set(s[1].trim());
					context.write(first, other);
				}
			}

		}

		public static class LocateWordReducer extends Reducer<Text, Text, Text, Text> {

			public void reduce(Text key, Iterator<Text> values, Context context)
					throws IOException, InterruptedException {
				context.write(key, values.next());
			}
		}

		public String arrayToString(ArrayList<String> list) {
			String result = "";
			for (String word : list) {
				word.trim();
				result += word + ";";
			}
			result.substring(0, result.length() - 1);
			// System.out.println(result);
			return result;

		}

		public ArrayList<String> stringToArray(String s) {
			int n = 0;
			ArrayList<String> result = new ArrayList<String>();
			String[] words = s.split(";");
			for (String word : words) {
				word.trim();
				result.add(n, word);
				n++;
			}
			// System.out.println(result.size());
			return result;

		}

	}
}
