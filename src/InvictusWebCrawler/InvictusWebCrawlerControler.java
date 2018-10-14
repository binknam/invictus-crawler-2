package InvictusWebCrawler;

public class InvictusWebCrawlerControler {

  private long numberOfCrawler = 0;
  private String root;
  private long depthOfCrawler = -1;

  public InvictusWebCrawlerControler(String root, long numberOfCrawler, long depthOfCrawler) {
    this.numberOfCrawler = numberOfCrawler;
    this.root = root;
    this.depthOfCrawler = depthOfCrawler;
    InvictusWebCrawler.queue.add(new WebUrl(0, root));
  }

  public void start() {
    for (int i = 0 ; i < numberOfCrawler; i++) {
      InvictusWebCrawler invictusWebCrawler = new InvictusWebCrawler(i+1, depthOfCrawler);
      Thread thread = new Thread(invictusWebCrawler);
      thread.start();
    }
  }

}