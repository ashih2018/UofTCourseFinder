import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Crawler {

    private static final int MAX_SEARCH = 100;
    private static final int MAX_DEPTH = 2;
    private List<String> urls;

    public Crawler() {
        this.urls = new LinkedList<>();
    }

    public String getLinks(String url, String keyword, String breadth, String level, int depth) {
        if (depth == 0) {
            this.urls = new LinkedList<>();
        }
        System.out.println("Searching");
        if (!this.urls.contains(url) && this.urls.size() < MAX_SEARCH && depth < MAX_DEPTH) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");
                if (keyword.length() != 0) {
                    if (this.containsKeyword(doc, keyword)) {
                        this.urls.add(url);
                    }
                }
                else {
                    this.urls.add(url);
                }
                for (Element link: links) {
                    this.getLinks(link.attr("abs:href=\\search-courses?page="), keyword, breadth, level, depth++);
                }
            }
            catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return "";
    }

    public boolean containsKeyword(Document d, String keyword) {
        String text = d.body().text();
        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    public String getURLS() {
        StringBuilder all_urls = new StringBuilder();
        for (String url: this.urls) {
            all_urls.append(url + "\n");
        }
        return all_urls.toString();
    }


    public void serialize(String str) {
        FileWriter fw;
        try {
            fw = new FileWriter(str + ".txt");
            BufferedWriter out = new BufferedWriter(fw);
            for (String url: this.urls) {
                out.write(url);
                out.newLine();
            }
        out.close();
        }
        catch (IOException e) {
            System.out.println("Could not open file");
        }
    }

}
