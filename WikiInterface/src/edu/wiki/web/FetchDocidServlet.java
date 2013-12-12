package edu.wiki.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public class FetchDocidServlet extends HttpServlet {
	static final long serialVersionUID = 3986721097L;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		WikiQueryServer.getLogger().info(
				"triggered servlet for fetching document by docid");
		String docid = null;

		try {
			if (req.getParameterValues("docid") != null)
				docid = req.getParameterValues("docid")[0];

			WikipediaPage doc = WikiQueryServer.getForwardIndex().getDocument(
					docid);

			if (doc != null) {
				WikiQueryServer.getLogger().info("fetched: " + doc.getDocid());
				res.setContentType(doc.getDisplayContentType());

				PrintWriter out = res.getWriter();
				out.print(doc.getDisplayContent());
				out.close();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			// catch-all, in case anything goes wrong
			WikiQueryServer.getLogger().info("trapped error fetching " + docid);
			res.setContentType("text/html");

			PrintWriter out = res.getWriter();
			out.print("<html><head><title>Invalid docid!</title><head>\n");
			out.print("<body>\n");
			out.print("<h1>Error!</h1>\n");
			out.print("<h3>Invalid docid: " + docid + "</h3>\n");
			out.print("</body></html>\n");
			out.close();
		}
	}
}