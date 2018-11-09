package InvictusWebCrawler;

public class WebUrl {
  private long depth;
  private String url;
  private WebUrl parent;
  private boolean marked;

  public WebUrl(WebUrl parent,long depth, String url) {
    this.parent = parent;
    this.url = url;
    this.depth=depth;
    this.marked = true;
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

  public WebUrl getParent() {
    return parent;
  }

  public void setParent(WebUrl parent) {
    this.parent = parent;
  }
}
