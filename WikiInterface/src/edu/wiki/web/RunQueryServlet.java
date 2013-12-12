package edu.wiki.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.umd.cloud9.collection.wikipedia.WikipediaForwardIndex;
import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.wiki.query.BooleanRetrieval;
import edu.wiki.query.ExpParser;
import edu.wiki.query.LocateResult;
import edu.wiki.query.ResultFormat;
import net.sf.jtpl.Template;

public class RunQueryServlet extends HttpServlet {

	WikipediaForwardIndex f = WikiQueryServer.getForwardIndex();
	String Qinput = WikiQueryServer.getQueryInputPath();
	String Qoutput = WikiQueryServer.getQueryOutputPath();
	WikipediaPage page;
	ExpParser exp;
	BooleanRetrieval br;
	int doc_length;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String query = null;
		if (request.getParameterValues("query") != null)
			query = request.getParameterValues("query")[0];

		long startTime = System.currentTimeMillis();
		exp = new ExpParser();
		br = new BooleanRetrieval(exp.parse(query), Qinput, Qoutput);
		Set<Integer> candidates = br.runQuery();

		PrintWriter out = response.getWriter();

		if (candidates.size() > 0) {
			LocateResult result = new LocateResult(
					br.resultIdAndOffset(candidates));

			// there we only extract top 10 results with highest tfidf score.
			List<ResultFormat> formatResult = result.getResult();
			if (formatResult.size() > 10)
				formatResult = formatResult.subList(0, 10);

			long endTime = System.currentTimeMillis();
			long searchTime = endTime - startTime;
			System.out.println("fetched results for query:" + query);
			result.printResult();

			// page = f.getDocument(12);
			// System.out.println(page.getDocid() + ": " + page.getTitle() + ":"
			// +
			// page.getDisplayContent());

			try {
				out.print(this.generatePage(query, formatResult, searchTime));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				out.print(this.generateErrorPage(query));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String generateErrorPage(String query) throws Exception {
		String webDir = WikiQueryServer.getwebappDirLocation();
		Template tpl = new Template(new File(webDir + "errorPage.jtpl"));
		tpl.assign("QUERY", query);
		tpl.parse("main");
		return (tpl.out());
	}

	private String generatePage(String s, List<ResultFormat> l, long time)
			throws Exception {
		String webDir = WikiQueryServer.getwebappDirLocation();
		Template tpl = new Template(new File(webDir+"resultPage.jtpl"));
		tpl.assign("TITLE", "Search Result");
		tpl.assign("QUERY", s);
		tpl.assign("TIME", Float.toString((float) (time / 1000f)));
		int count = 0;
		String doc_snippets;
		for (ResultFormat doc : l) {
			int docId = doc.getId();
			double rank = doc.getRank();
			page = f.getDocument(Integer.toString(docId));
			tpl.assign("LINK", "/fetch_docid?docid=" + Integer.toString(docId));
			tpl.assign("WIKI_TITLE", page.getTitle());
			// tpl.assign("COUNT", Integer.toString(count));
			doc_snippets = getSnippets(doc.getLow(), doc.getHigh(), page);
			tpl.assign("SNIPPETS", doc_snippets);
			tpl.assign("SCORE", Double.toString(rank / (double) doc_length));
			tpl.parse("main.result");
			count++;
		}
		tpl.parse("main");
		return (tpl.out());
	}

	private String getSnippets(int i, int j, WikipediaPage page1) {
		// return text snippets between offset i and j
		String content = page1.getTitle() + " " + page1.getContent();
		List<String> words = new ArrayList<String>();
		List<String> sublist;

		Pattern TOKEN = Pattern.compile("\\w+");
		for (String token : StringUtils.split(content)) {
			Matcher m = TOKEN.matcher(token);
			while (m.find()) {
				words.add(m.group());
			}
		}
		// put fromindex a little forward
		if (i > 20)
			i = i - 20;
		else
			i = 0;

		if (j - i > 70 && j + 1 <= words.size()) {
			sublist = words.subList(i, j + 1);
		} else {
			if (i + 70 < words.size())
				sublist = words.subList(i, i + 70);
			else
				sublist = words.subList(i, words.size());
		}

		doc_length = words.size();
		return "..." + StringUtils.join(sublist, " ") + "...";
	}
}
