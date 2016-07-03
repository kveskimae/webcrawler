package com.foo.webcrawler;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.webapp.WebAppContext;

import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class SitemapCrawlControllerTest {

	private static final int PORT_NUMBER = 9876;

	public static class CrawlStartPageServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getOutputStream().println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"><html><head>\n" +
						"<title>Start page</title>\n" +
						"</head><body>\n" +
						"<ul class=\"navbar\">\n" +
						"  <li><a href=\"anotherpage.html\">My town</a>\n" +
						"</ul>\n" +
						"</body></html>"
			);
		}
	}

	public static class AnotherPageServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getOutputStream().println(
					"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n" +
							"<html><head>\n" +
							"  <title>Another page</title>\n" +
							"<link rel=\"stylesheet\" href=\"mystyle.css\">\n" +
							"</head><body></body></html>"
			);
		}
	}

	public static class CssServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getOutputStream().println(
					"a:link {color: blue}"
			);
		}
	}

	@Test
	public void testVisitsOtherPages() throws Exception {
		startServer();
		String folder = findWritableFolder();
		SitemapCrawlController sitemapCrawlController = new SitemapCrawlController(folder, "http://localhost:" + PORT_NUMBER + "/");
		sitemapCrawlController.run();
		assertTrue(sitemapCrawlController.getUrls().contains("http://localhost:" + PORT_NUMBER + "/"));
		assertTrue(sitemapCrawlController.getUrls().contains("http://localhost:" + PORT_NUMBER + "/anotherpage.html"));
		assertTrue(sitemapCrawlController.getUrls().contains("http://localhost:" + PORT_NUMBER + "/mystyle.css")); // referenced from another page
	}

	private void startServer() throws Exception {
		Server server = new Server(PORT_NUMBER);
		WebAppContext handler = new WebAppContext();
		handler.setResourceBase("/");
		handler.setContextPath("/");
		handler.addServlet(new ServletHolder(new CrawlStartPageServlet()), "/");
		handler.addServlet(new ServletHolder(new AnotherPageServlet()), "/anotherpage.html");
		handler.addServlet(new ServletHolder(new CssServlet()), "/mystyle.css");
		server.setHandler(handler);
		server.start();
	}

	private String findWritableFolder() throws IOException {
		String tmpFilename = "deleteme";
		File file =new File(tmpFilename);
		file.createNewFile();
		String folder = file.getAbsolutePath();
		file.delete();
		folder = folder.substring(0, folder.length() - tmpFilename.length());
		return folder;
	}

}
