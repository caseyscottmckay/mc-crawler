package com.mckay.example.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParserToJson {
    PrintWriter pw =new PrintWriter("./sec-data.txt");
    public ParserToJson() throws FileNotFoundException {
    }
    /*
        Method takes in current 10-k page url for company and return hasmap of subsidiaries' names and locations
        @params: String url of company's most uptodate 10k page
        @returns Hashmap<subsidiary name, subsidiary name, subsidiary location>
        *included location because it seems like it may be useful down the road, but not included in final ouput right now
     */
    public HashMap<String, String>  getJsonObjectFromUrl(String urlToStartWith) {
        Document doc;
        HashMap<String, String> subsidCompanyNamesAndStates=new HashMap();
        String name = "";
        String state = "";
        try {
            // need http protocol
            doc = Jsoup.connect(urlToStartWith).get();
            // get page title
            String title = doc.title();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                // get the value from href attribute
                if (link.attr("href").matches(".*(exhibit|ex)21.*?\\.htm")) {
                    String ex21URL = "https://www.sec.gov" + (link.attr("href"));

                    Document doc21 = Jsoup.connect(ex21URL).get();
                    String html = doc21.html();

                    //JSONObject jsonObject = new JSONObject();
                    // JSONArray list = new JSONArray();

                    //custom method to catch #7 that refuses to use a table for the data
                    //todo need better way to get outliers--i.e., this misses anything that is not div or table (and may be missy for other divs outside custom
                    if ((!html.contains("<tr>") && (!html.contains("<td>")))) {
                        for (Element row : doc21.select("div")) {
                            if(row.text().contains(",")) {
                                name = row.text().substring(0, row.text().lastIndexOf(","));
                                pw.println(name);
                                //jsonObject.put("Name", name);
                                // jsonObject.put("Location", state);
                                //list.add(jsonObject);
                                if(name.length()>2&&(!name.contains("SUBSIDIAR*"))&&(!name.contains("Name of Subsidiary"))&&(!name.contains("a subsidiary of"))&&(!name.equals(name.toUpperCase()))&&(!name.equals("name"))){
                                    subsidCompanyNamesAndStates.put(name, name);
                                }
                                state  = row.text().substring(row.text().lastIndexOf(","));
                            }
                        }
                    }
                    if ((html.contains("<tr>") && (html.contains("<td>")))) {
                        for (Element table : doc21.select("table")) {
                            for (Element row : table.select("tr")) {
                                Elements tds = row.select("td");
                                name = "";
                                state = "";
                                String temp = "";
                                for (int j = 0; j < tds.size(); j++) {
                                    temp += (tds.get(j).text() + "#");
                                    String[] tempOps=temp.split("#");
                                    if(tempOps.length>0){
                                        name=tempOps[0];
                                        //jsonObject.put("Name", name);
                                        //jsonObject.put("Location", state);
                                        //list.add(jsonObject);
                                        if(name.length()>2&&(!name.contains("SUBSIDIAR*"))&&(!name.contains("Name of Subsidiary"))&&(!name.contains("a subsidiary of"))&&(!name.equals(name.toUpperCase()))&&(!name.equals("name"))){
                                            subsidCompanyNamesAndStates.put(name, name);
                                        }

                                    }
                                }
                            }
                        }
                    }
                    else {
                        for (Element table : doc21.select("table")) {
                            for (Element row : table.select("tr")) {
                                Elements tds = row.select("td");

                                name = "";
                                state = "";
                                String temp = "";

                                for (int j = 0; j < tds.size(); j++) {
                                    temp += (tds.get(j).text() + "#");
                                    String[] tempOps=temp.split("#");
                                    if(tempOps.length>0){
                                        name=tempOps[0];
                                        //  jsonObject.put("Name", name);
                                        //jsonObject.put("Location", state);
                                        //list.add(jsonObject);
                                        //clean up the outliers
                                        if(name.length()>2&&(!name.contains("SUBSIDIAR*"))&&(!name.contains("Name of Subsidiary"))&&(!name.contains("a subsidiary of"))&&(!name.equals(name.toUpperCase()))&&(!name.equals("name"))){
                                            subsidCompanyNamesAndStates.put(name, name);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return subsidCompanyNamesAndStates;
    }

    public static HashMap<String, String> getImages(){
        HashMap<String, String> imagesOutMap =new HashMap();
        Document doc;
        try {
            //get all images
            doc = Jsoup.connect("https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK=875045&type=10-K").get();
            Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            for (Element image : images) {

                System.out.println("\nsrc : " + image.attr("src"));
                System.out.println("height : " + image.attr("height"));
                System.out.println("width : " + image.attr("width"));
                System.out.println("alt : " + image.attr("alt"));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        return imagesOutMap;
    }

    /*
        Input htmlString from crawler
        Output parsed html
     */
    public static HashMap<String, String> getMeta(String html){
        HashMap<String, String> metaOutMap =new HashMap();

        Document doc = Jsoup.parse(html.toString());

        //get meta description content
        String description = doc.select("meta[name=description]").get(0).attr("content");
        System.out.println("Meta description : " + description);

        //get meta keyword content
        String keywords = doc.select("meta[name=keywords]").first().attr("content");
        System.out.println("Meta keyword : " + keywords);

        return metaOutMap;
    }

}