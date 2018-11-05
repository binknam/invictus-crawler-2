package InvictusWebCrawler;

import InvictusFileIO.InvictusFileWriter;
import org.jsoup.Connection;
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
  private RobotTxt robotTxt;
  private long timeToDelay;

  public InvictusWebCrawler(int number, long depthOfCrawler, String root,
      InvictusWebCrawlerControler invictusWebCrawlerControler) {
    this.number = number;
    this.depthOfCrawler = depthOfCrawler;
    this.frontier = invictusWebCrawlerControler.getFrontier();
    this.invictusFileWriter = new InvictusFileWriter();
    this.root = root;
    this.robotTxt = invictusWebCrawlerControler.getRobotTxt();
    this.timeToDelay = invictusWebCrawlerControler.getNumberOfCrawler() * 200;
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
    Document document;
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
          if (crawledUrl.getDepth() > depthOfCrawler) {
            System.out.println(
                this.invitctusThread.getName() + " === " + crawledUrl.getUrl()
                    + " will not be crawled because its depth matches max depth ====");
            return false;
          }
          this.frontier.markUrl(crawledUrl.getUrl());

          boolean ok = false;

          while (!ok) {
            try {
              Connection jsoupParser = Jsoup.connect(crawledUrl.getUrl());
              Map<String, String> headers = new HashMap<>();
              headers.put("User-Agent", "invictus");
              headers.put("From", "namvtran.bink@gmail.com");
              jsoupParser.headers(headers);
              document = jsoupParser.get();

              Elements links = document.body().select("a[href]");

              this.searchLinks(links, crawledUrl);

              this.visit(document, crawledUrl.getUrl(), crawledUrl.getDepth());

              ok = true;

              Thread.sleep(timeToDelay);
            } catch (MalformedURLException e) {
              System.out.println(this.invitctusThread.getName() + " MalformedURL" + crawledUrl.getUrl() + "====");
              ok = false;
            } catch (IOException e) {
              System.out.println(this.invitctusThread.getName() + " IOException url " + crawledUrl.getUrl() + " ====");
              ok = true;
            }
          }
        }
      } catch (Exception e) {
        System.out.println(this.invitctusThread.getName() + " Error when running job");
      }
    }
    return false;
  }

  public void visit(Document doc, String url, long depth) {
    String title = doc.title();
    String html = doc.html();
    String text = doc.body().text();

    if (text.length() == 0) {
      System.out.println(
          this.invitctusThread.getName() + " " + url + " will not be stored because it doesn't have text data =====");
      return;
    }

    System.out.println(this.invitctusThread.getName() + " site: " + url + " is being crawled in depth " + depth + " ====");

    invictusFileWriter.writeWebPageHtml(html, title);
    invictusFileWriter.writeWebPageText(text, title);
  }

  public void searchLinks(Elements links, WebUrl crawledUrl) {
    for (Element link : links) {
      String url = link.attr("href");
      if (url.length() < 256 && robotTxt.allowedUrl(url)) {
        if (url.startsWith("/") && !url.startsWith("//")) {
          url = this.root + url;
        }
        if (!frontier.getMarked().contains(url) && shouldVisit(url) && !frontier.isContainedUrl(url)) {
          System.out.println(this.invitctusThread.getName() + " site add " + url);
          frontier.addUrlToQueue(new WebUrl(crawledUrl.getDepth() + 1, url));
        } else {
          System.out.println(
              this.invitctusThread.getName() + " === " + url
                  + " won't be crawled because of your policy should visit or it's marked ====");
        }
      } else {
        System.out.println("site: " + url + "won't be crawled because its length is over size ====");
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

  public List<WebUrl> getNotMarkedUrls() {
    return notMarkedUrls;
  }

}
