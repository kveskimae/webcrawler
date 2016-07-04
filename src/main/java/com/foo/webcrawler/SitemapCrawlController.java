package com.foo.webcrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

public class SitemapCrawlController {

	private static final Logger logger = LoggerFactory.getLogger(SitemapCrawlController.class);

	private final String crawlDomain;
	private final String rootFolder;

	private CrawlConfig config;
	private CrawlController controller;

	public SitemapCrawlController(final String rootFolder, final String crawlDomain) throws Exception {
		this.rootFolder = rootFolder;
		this.crawlDomain = crawlDomain;
	}

	public void init() throws Exception {
		logger.info("Initializing crawler");
		initConfig();
		initSitemapCrawler();
		initController();
	}

	private void initConfig() {
		config = new CrawlConfig();
		config.setCrawlStorageFolder(rootFolder);
		config.setIncludeBinaryContentInCrawling(true);
	}

	private void initSitemapCrawler() throws MalformedURLException, URISyntaxException {
		SitemapCrawler.configure(crawlDomain); // Not too great architecture
	}

	private void initController() throws Exception {
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtServer robotstxtServer = new RobotstxtServer(new RobotstxtConfig(), pageFetcher);
		this.controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed(crawlDomain);
	}

	public void run() throws Exception {
		if (controller == null) {
			init();
		}
		int numberOfCrawlers = getNumberOfCrawlers();
		logger.info("Starting to crawl with " + numberOfCrawlers + " threads");
		controller.start(SitemapCrawler.class, numberOfCrawlers);
		SitemapCrawler.getFoundLResources().add(crawlDomain);
		controller.waitUntilFinish(); // Will block until finished
	}

	public Collection<String> getUrls() {
		return SitemapCrawler.getFoundLResources();
	}

	public int getNumberOfCrawlers() {
		return Runtime.getRuntime().availableProcessors() + 1;
	}
}
