package InvictusWebCrawler;

import java.util.*;

public class InvictusWebCrawlerControler {

  private long numberOfCrawler = 0;
  private String root;
  private long depthOfCrawler = -1;
  private Frontier frontier;
  private List<InvictusWebCrawler> invictusWebCrawlers;

  public InvictusWebCrawlerControler(String root, long numberOfCrawler, long depthOfCrawler) {
    this.numberOfCrawler = numberOfCrawler;
    this.root = root;
    this.depthOfCrawler = depthOfCrawler;
    List<WebUrl> list = Collections.synchronizedList(new ArrayList<WebUrl>());
    Set<String> markedUrls = new HashSet<>();
    invictusWebCrawlers = new ArrayList<InvictusWebCrawler>();
    this.frontier = new Frontier(list, markedUrls);
    frontier.addUrlToQueue(new WebUrl(0, this.root));
    frontier.setFinished(false);
  }

  public void start() {
    final List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < numberOfCrawler; i++) {
      InvictusWebCrawler invictusWebCrawler = new InvictusWebCrawler(i + 1, depthOfCrawler, this);
      Thread monitorThread = new Thread();
      invictusWebCrawler.setThread(monitorThread);
      monitorThread.start();
      invictusWebCrawlers.add(invictusWebCrawler);
      threads.add(monitorThread);
    }

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    while (!this.frontier.isFinished()) {
      for (int i = 0; i < threads.size(); i++) {
        Thread tempThread = (Thread) threads.get(i);
        if (!tempThread.isAlive()) {
          InvictusWebCrawler crawler = new InvictusWebCrawler(i + 1, depthOfCrawler, this);
          tempThread = new Thread(crawler, "Crawler " + (i + 1));
          threads.remove(i);
          threads.add(i, tempThread);
          crawler.setThread(tempThread);
          tempThread.start();
          invictusWebCrawlers.remove(i);
          invictusWebCrawlers.add(i, crawler);
        }
      }
      if (this.frontier.isListWebUrlEmpty()) {
        boolean someOneIsWorking = false;
        for (InvictusWebCrawler invictusWebCrawler : invictusWebCrawlers) {
          if (invictusWebCrawler.getNotMarkedUrls().size() != 0) {
            someOneIsWorking = true;
            break;
          }
        }
        if (!someOneIsWorking) {
          this.frontier.setFinished(true);
        }
      }
    }
  }

  public Frontier getFrontier() {
    return frontier;
  }
}