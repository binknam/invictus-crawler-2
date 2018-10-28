package InvictusWebCrawler;

import InvictusFileIO.InvictusFileWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Pattern;

public class InvictusWebCrawler implements Runnable {
  private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js"
      + "|zip|gz))$");

  private long depthOfCrawler = -1;
  private Frontier frontier;
  private int number;
  private List<WebUrl> notMarkedUrls = new ArrayList<WebUrl>();
  private Thread invitctusThread;
  private String root;
  private InvictusFileWriter invictusFileWriter;

  public InvictusWebCrawler(int number, long depthOfCrawler, String root, InvictusWebCrawlerControler invictusWebCrawlerControler) {
    this.number = number;
    this.depthOfCrawler = depthOfCrawler;
    this.frontier = invictusWebCrawlerControler.getFrontier();
    this.invictusFileWriter  = new InvictusFileWriter();
    this.root = root;
    System.out.println("Web crawler " + this.number + " was created!");
  }

  public void run() {
    while (true) {
      boolean stoppedCrawler = ProcessCrawl();
      if (stoppedCrawler) {
        System.out.println("======= Crawler number " + this.number + " is stopping!!! ===========");
        return;
      }
    }
  }

  public boolean ProcessCrawl() {
//    BufferedReader br = null;
    Document document = null;
    this.notMarkedUrls = frontier.getWebUrl(10);
    if (notMarkedUrls.isEmpty()) {
      if (frontier.isFinished()) {
        return true;
      }
      try {
        System.out.println("======= Crawler number " + this.number + " is waiting for new urls!!! ===========");
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        frontier.returnUrlsToFrontier(this.notMarkedUrls);
        System.out.println("Error occurred ===== " + e);
        return true;
      }
    } else {
      try {
        for (WebUrl crawledUrl : notMarkedUrls) {
          System.out.println("============= The crawler number " + this.number + " is running ============");
          System.out.println("============= We are in the depth " + crawledUrl.getDepth() + " ============");
          if (crawledUrl.getDepth() > depthOfCrawler) {
            System.out.println(
                "site: " + crawledUrl.getUrl() + " will not be crawled because its depth match max depth ====");
            return false;
          }
          this.frontier.markUrl(crawledUrl.getUrl());
          System.out.println("site: " + crawledUrl.getUrl() + " is crawling ====");

          boolean ok = false;

          while (!ok) {
            try {
              document = Jsoup.connect(crawledUrl.getUrl()).get();
              this.visit(document);

              Elements links = document.body().select("a[href]");
              this.searchLinks(links, crawledUrl);
              ok = true;
            } catch (MalformedURLException e) {
              System.out.println("MalformedURL" + crawledUrl + "====");
              ok = false;
            } catch (IOException e) {
              System.out.println("IOException url" + crawledUrl + "====");
              ok = false;
            }
          }
        }
      } catch (Exception e) {
        System.out.println("Error when running job");
      }
    }
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void visit(Document doc) {
    String title = doc.title();
    String html = doc.html();
    String text = doc.body().text();

    invictusFileWriter.writeWebPageHtml(html, title);
    invictusFileWriter.writeWebPageText(text, title);
  }

  public void searchLinks(Elements links, WebUrl crawledUrl){
    for (Element link: links) {
      String url = link.attr("href");
      if (url.startsWith("/") && !url.startsWith("//")) {
        url = this.root + url;
      }
      if (!frontier.getMarked().contains(url) && shouldVisit(url)) {
        System.out.println("site add " + url);
        frontier.addUrlToQueue(new WebUrl(crawledUrl.getDepth() + 1, url));
      } else {
        System.out.println(
            "site: " + url + " won't be crawled because of your policy should visit or it's marked ====");
      }
    }
  }

  public boolean shouldVisit(String url) {
    return !FILTERS.matcher(url).matches()
        && url.startsWith("https://vnexpress.net");
  }

  public void setThread(Thread thread) {
    this.invitctusThread = thread;
  }

  public long getDepthOfCrawler() {
    return depthOfCrawler;
  }

  public void setDepthOfCrawler(long depthOfCrawler) {
    this.depthOfCrawler = depthOfCrawler;
  }

  public Thread getInvitctusThread() {
    return invitctusThread;
  }

  public void setInvitctusThread(Thread invitctusThread) {
    this.invitctusThread = invitctusThread;
  }

  public List<WebUrl> getNotMarkedUrls() {
    return notMarkedUrls;
  }

  public int getNumber() {
    return number;
  }
}
