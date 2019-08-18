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
    private static final int MAX_DEPTH = 3;
    private List<String> urls;

    public Crawler() {
        this.urls = new LinkedList<>();
    }

    public void findCourses(String url, String keyword, String breadth, String level, int depth) {
        if (depth == 0) {
            // if we are searching we want to reset our urls list
            this.urls = new LinkedList<>();
        }
        if (!this.urls.contains(url) && this.urls.size() < MAX_SEARCH && depth < MAX_DEPTH) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");
                depth++;
                // Looks through all links and checks to see if they are in search specifications
                for (Element link: links) {
                    // if the link is a course, then we check if it is within the user's serach specifications
                    if (link.toString().contains("/course/")) {
                        String coursePage = "https://fas.calendar.utoronto.ca" + link.attr("href");
                        coursePage = coursePage.toLowerCase();
                        if (!this.urls.contains(coursePage)) {
                            String course = this.checkCourse(coursePage, keyword, breadth, level);
                            if (course != null) {
                                this.urls.add(course.toLowerCase());
                            }
                        }

                    }
                    // if the link found is a next page link, then we call this function with the next page link as a url
                    else if (link.toString().contains("/search-courses?page")) {
                        String endUrl = link.attr("href");
                        String nextPage = "https://fas.calendar.utoronto.ca" + endUrl;
                        this.findCourses(nextPage, keyword, breadth, level, depth);
                    }
                }
            }
            catch (IOException e) {
//                System.out.println("Error: " + e.getMessage());
            }
            catch (IllegalArgumentException e) {
//                System.out.println("Error retrieving course " + url);
            }
        }
    }

    public String getUrls() {
        StringBuilder allUrls = new StringBuilder();
        for (String link : this.urls) {
            allUrls.append(link + "\n");
        }
        return allUrls.toString();
    }

    // checks to see if the page has the keyword, breadth, or level entered
    private String checkCourse(String url, String keyword, String breadth, String level) {
        try {
            Document doc = Jsoup.connect(url).get();
            if (!keyword.equals("") && this.containsKeyword(doc, keyword)) {
                return url;
            }
            if (!breadth.equals("") && this.containsBreadth(doc, breadth)) {
                return url;
            }
            if (!level.equals("") && this.containsLevel(doc, level)) {
                return url;
            }
        } catch (IOException e) {
            System.out.println("Error retrieving course " + url);
        } catch (IllegalArgumentException e) {
            System.out.println("Error retrieving course " + url);
        }
        return null;
    }

    // returns true if the page contains the keyword
    private boolean containsKeyword(Document doc, String keyword) {
        String text = doc.body().text();
        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    // returns true if the breadth is equal to the breadth entered
    private boolean containsBreadth(Document doc, String breadth) {
        String breadthName = "";
        // finds the DOM element with the class field-name-field-breadth-req to get breadth
        Elements breadthCourses = doc.getElementsByClass("field-name-field-breadth-req");
        for (Element course : breadthCourses) {
            breadthName = course.text();
        }
        return breadthName.toLowerCase().contains(breadth.toLowerCase());
    }

    // returns true if the breadth is equal to the level entered
    private boolean containsLevel(Document doc, String level) {
        // finds the level through the DOM element with page-title as id
        String courseName = doc.getElementById("page-title").getElementById("page-title").text();
        if (courseName != null) {
            for (int i = 0; i < courseName.length(); i++) {
                if (Character.isDigit(courseName.charAt(i))) {
                    // checks to see if the first digits of course levels are the same
                    return courseName.charAt(i) == level.charAt(0);
                }
            }
        }
        return false;
    }

    // saves urls found as a text file
    public void save(String str) {
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
