import InvictusWebCrawler.InvictusWebCrawlerControler;

public class main {
  public static void main(String args[]) {
    InvictusWebCrawlerControler invictusWebCrawlerControler = null;
    try {
      invictusWebCrawlerControler = new InvictusWebCrawlerControler("https://vnexpress.net",
          7, 2);
    } catch (Exception e) {
      System.out.println("Error when create controller" + e);
    }
    invictusWebCrawlerControler.start();
  }
}
