package edu.wiki.web;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import edu.umd.cloud9.collection.wikipedia.WikipediaForwardIndex;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 * 
 */
public class WikiQueryServer extends Configured implements Tool {

	private static final Logger LOG = Logger.getLogger(WikiQueryServer.class);
	private static WikipediaForwardIndex WIKI_INDEX;
	private static String Qinput;
	private static String Qoutput;
	private static String webappDirLocation;

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 5) {
			System.out
					.println("Usage: <forward index path> <mapping file path> <inverted index> <output> <webDir>");
			System.exit(1);
		}

		Qinput = args[2];
		Qoutput = args[3];

		webappDirLocation = args[4];

		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}

		Configuration conf = getConf();
		WIKI_INDEX = new WikipediaForwardIndex(conf);
		WIKI_INDEX.loadIndex(new Path(args[0]), new Path(args[1]),
				FileSystem.get(conf));

		Server server = new Server(Integer.valueOf(webPort));
		WebAppContext root = new WebAppContext();

		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);

		// Parent loader priority is a class loader setting that Jetty
		// accepts.
		// By default Jetty will behave like most web containers in that it
		// will
		// allow your application to replace non-server libraries that are
		// part of the
		// container. Setting parent loader priority to true changes this
		// behavior.
		// Read more here:
		// http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);

		server.setHandler(root);

		server.start();
		// server.join();
		return 0;
	}
	
	public static String getwebappDirLocation() {
		return webappDirLocation;
	}
	public static String getQueryInputPath() {
		return Qinput;
	}

	public static String getQueryOutputPath() {
		return Qoutput;
	}

	public static Logger getLogger() {
		return LOG;
	}

	public static WikipediaForwardIndex getForwardIndex() {
		return WIKI_INDEX;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ToolRunner.run(new WikiQueryServer(), args);

	}

}
