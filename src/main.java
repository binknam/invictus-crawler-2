import InvictusWebCrawler.InvictusWebCrawlerControler;

public class main {
  public static void main(String args[]) {
    InvictusWebCrawlerControler invictusWebCrawlerControler = new InvictusWebCrawlerControler("https://vnexpress.net", 1);
    invictusWebCrawlerControler.start();
  }
}
