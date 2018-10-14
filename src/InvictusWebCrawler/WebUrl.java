package InvictusWebCrawler;

public class WebUrl {
  private long depth;
  private String url;

  public WebUrl(long depth, String url) {
    this.depth = depth;
    this.url = url;
  }

  public long getDepth() {
    return depth;
  }

  public void setDepth(long depth) {
    this.depth = depth;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
