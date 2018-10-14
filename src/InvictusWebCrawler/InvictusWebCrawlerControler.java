package InvictusWebCrawler;

public class InvictusWebCrawlerControler {

  private long numberOfCrawler = 0;
  private String root;

  public InvictusWebCrawlerControler(String root, long numberOfCrawler) {
    this.numberOfCrawler = numberOfCrawler;
    this.root = root;
    InvictusWebCrawler.queue.add(root);
  }

  public void start() {
    for (int i = 0 ; i < numberOfCrawler; i++) {
      InvictusWebCrawler invictusWebCrawler = new InvictusWebCrawler(i+1);
      Thread thread = new Thread(invictusWebCrawler);
      thread.start();
    }
  }

}