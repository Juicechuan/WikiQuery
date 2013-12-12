import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.umd.cloud9.collection.wikipedia.WikipediaForwardIndex;
import edu.umd.cloud9.collection.wikipedia.WikipediaPage;


public class WikipageRandomAccess extends Configured implements Tool{
	
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = getConf();
		WikipediaForwardIndex f = new WikipediaForwardIndex(conf);
		f.loadIndex(new Path("sample-enwiki-findex.dat"), new Path("sample-enwiki-docno.dat"), FileSystem.get(conf));
		
		WikipediaPage page;

		// fetch docno
		page = f.getDocument(10);
		System.out.println(page.getDocid() + ": " + page.getTitle());

		// fetch docid
		page = f.getDocument("765");
		System.out.println(page.getDocid() + ": " + page.getTitle());

		return 0;
	}
	
	public WikipageRandomAccess() {
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ToolRunner.run(new WikipageRandomAccess(), args);
	}



}
