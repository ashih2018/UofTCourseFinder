public class CrawlerTest {

    private Crawler crawler;
    public static int PAGE_NUM = 1;

    public CrawlerTest() {
        this.crawler = new Crawler();
    }

    public String run(String input, String keyword) {
        if (keyword.equals("")) {
//            this.crawler.getLinks(input, "", 0);
        }
        else {
//            this.crawler.getLinks(input, keyword, 0);
        }
//        System.out.println(this.crawler.getURLS());
        return this.crawler.getURLS();
    }

    public void serialize() {
        crawler.serialize("page" + PAGE_NUM);
        PAGE_NUM++;
    }

}
