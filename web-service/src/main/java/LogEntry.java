import java.time.Instant;

public class LogEntry {

  private final String timestamp;
  private final String query;
  private final String clientIp;
  private final int responseStatus;
  private final int resultCount;
  private final long durationMs;

  public LogEntry(String query, String clientIp, int responseStatus,
      int resultCount, long durationMs) {
    this.timestamp = Instant.now().toString();
    this.query = query;
    this.clientIp = clientIp;
    this.responseStatus = responseStatus;
    this.resultCount = resultCount;
    this.durationMs = durationMs;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getQuery() {
    return query;
  }

  public String getClientIp() {
    return clientIp;
  }

  public int getResponseStatus() {
    return responseStatus;
  }

  public int getResultCount() {
    return resultCount;
  }

  public long getDurationMs() {
    return durationMs;
  }
}
