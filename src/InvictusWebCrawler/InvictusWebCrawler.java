package InvictusWebCrawler;

import InvictusFileIO.InvictusFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvictusWebCrawler implements Runnable {
  protected static final Logger logger = LoggerFactory.getLogger(InvictusWebCrawler.class);

  private Queue<String> queue = new LinkedList<>();
  private Set<String> marked = new HashSet<>();
  private String regex = "http[s]*://(\\w+\\.)*(\\w+)";
  private String rootUrl;

  @Override
  public void run() {
    while (true) {
      queue.add(rootUrl);
      BufferedReader br = null;

      try {
        while (!queue.isEmpty()) {
          String crawledUrl = queue.poll();
          System.out.println("site: " + crawledUrl + "====");

          if (marked.size() > 100) {
            return;
          }

          boolean ok = false;
          URL url;

          while (!ok) {
            try {
              url = new URL(crawledUrl);
              br = new BufferedReader(new InputStreamReader(url.openStream()));
              ok = true;
            } catch (MalformedURLException e) {
              logger.error("MalformedURL" + crawledUrl + "====");
              crawledUrl = queue.poll();
              ok = false;
            } catch (IOException e) {
              logger.error("IOException url" + crawledUrl + "====");
              crawledUrl = queue.poll();
              ok = false;
            }
          }

          StringBuilder sb = new StringBuilder();
          String tmp;

          while ((tmp = br.readLine()) != null) {
            sb.append(tmp);
          }

          tmp = sb.toString();
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(tmp);

          while (matcher.find()) {
            String w = matcher.group();
            if (!marked.contains(w)) {
              marked.add(w);
              logger.error("site add" + w);
              queue.add(w);
            }
          }
        }

        if (br != null) {
          br.close();
        }

      } catch (Exception e) {
        logger.error("Error when running job");
      }
    }
  }

  public Queue<String> getQueue() {
    return queue;
  }

  public void setQueue(Queue<String> queue) {
    this.queue = queue;
  }

  public Set<String> getMarked() {
    return marked;
  }

  public void setMarked(Set<String> marked) {
    this.marked = marked;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public String getRootUrl() {
    return rootUrl;
  }

  public void setRootUrl(String rootUrl) {
    this.rootUrl = rootUrl;
  }

}
