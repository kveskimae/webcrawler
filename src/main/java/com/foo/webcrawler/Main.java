package com.foo.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			logger.error("Need parameters: ");
			logger.error("\t crawlDomain (domain to crawl)");
			logger.error("\t rootFolder (optional parameter, which defaults to /sitemaps ; this folder will contain intermediate crawl data and the resultant sitemap will be saved in it)");
			logger.error("\t filename (optional parameter, which defaults to sitemap.xml ; this file will contain be the resultant sitemap)");
			return;
		}
		String crawlDomain = args[0];
		logger.info("Starting to crawl domain '" + crawlDomain + "'");
		String rootFolder = "/sitemaps";
		if (args.length > 1) {
			rootFolder = args[1];
		}
		File targetDirectory = new File(rootFolder);
		if (!targetDirectory.exists()) {
			// Also checks the storage folder before starting the actual heavy crawling process
			targetDirectory.mkdirs();
		}
		String filename = "sitemap.xml";
		if (args.length > 2) {
			filename = args[2];
		}
		logger.info("Resulting sitemap will be saved into file '" + rootFolder + "/" + filename + "'");
		SitemapWriter sitemapWriter = new SitemapWriter();
		SitemapCrawlController sitemapCrawlController = new SitemapCrawlController(rootFolder, crawlDomain);
		sitemapCrawlController.run();
		File file = new File(targetDirectory, filename);
		sitemapWriter.writeSitemap(file, sitemapCrawlController.getUrls());
		logger.info("Crawling domain has finished and site map has been saved successfully");
	}

}
