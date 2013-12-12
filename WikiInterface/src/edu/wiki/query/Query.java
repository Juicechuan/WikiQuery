package edu.wiki.query;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

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
import org.apache.hadoop.util.ReflectionUtils;

public class Query {
	
	public static class QueryMapper extends 
			Mapper<Text, Text, Text, Text> {
		
		private String word;
		//private String location;
		public void setup(Context context){
			word = context.getConfiguration().get("word");
			//location = context.getConfiguration().get("location");
		}
		public void map(Text key, Text value,Context context
						) throws IOException,InterruptedException {
			
		    /*Configuration conf = new Configuration();	
			FileSystem fs = FileSystem.get(URI.create(location), conf);
			SequenceFile.Reader sfr = new SequenceFile.Reader(fs, new Path(location), conf);
			
			key = (Text) ReflectionUtils.newInstance(
					sfr.getKeyClass(), conf);
			
			value = (Text) ReflectionUtils.newInstance(
					sfr.getValueClass(), conf);
			
			
			try {
				while (sfr.next(key, value)) {
					if(key.toString().equals(word)){
						context.write(key, value);
					}
				}	
				} catch (IOException e1) {
					e1.printStackTrace();
				}finally {
	                IOUtils.closeStream(sfr);
				}
			}*/
			if(key.toString().equals(word)){
				context.write(key, value);
			}
		}
		
	
		
		
		public static class QueryReducer extends Reducer<Text, Text, Text, Text>{
			
			public void reduce(Text key, Iterator<Text> values,Context context
							)throws IOException, InterruptedException {
				context.write(key,values.next());
			}
		}

		/**
		 * @param args
		 * @throws IOException
		 * @throws InterruptedException
		 * @throws ClassNotFoundException
		 */
		public static void doQuery(String InputDir, String OutputDir,String word) throws IOException, InterruptedException, ClassNotFoundException{	
			Configuration conf = new Configuration();
			conf.set("word", word);
			//conf.set("location",location);
			
			
			Job job = new Job(conf, "Query");                                         
		    job.setJarByClass(Query.class);                                            
		    job.setMapperClass(QueryMapper.class);  
		    job.setNumReduceTasks(0);  
		    job.setOutputKeyClass(Text.class);  
		    job.setOutputValueClass(Text.class);  
		    //job.setOutputFormatClass(Text.class);
		    job.setInputFormatClass(SequenceFileInputFormat.class);
		    job.setOutputFormatClass(TextOutputFormat.class);
		    FileInputFormat.addInputPath(job, new Path(InputDir));  
		    FileOutputFormat.setOutputPath(job, new Path(OutputDir));  
		    System.exit(job.waitForCompletion(true) ? 0 : 1);  
		}
	}
}
