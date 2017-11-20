package com.mckay.example.crawler;

import org.jsoup.Jsoup;

import org.junit.Before;
import org.junit.Test;

import org.powermock.core.classloader.annotations.PrepareForTest;


/**
 * @author McKay
 */
@PrepareForTest(Jsoup.class)
public class JSoupCrawlerTest {

    private final String testCiks = "875045,318154,320193,816284,804055,882095,875320";


    @Test
    public void testUrlWithNoLinks() throws Exception {
        final Crawler crawler= new Crawler();
        crawler.processCIKs(testCiks);
    }



}
