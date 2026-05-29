import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;

public class MongoDBClient {

//  TODO: Add connection string
  private static final String CONNECTION_STRING = "";
  private static final String DATABASE_NAME = "food_search";
  private static final String COLLECTION_NAME = "logs";

  private static MongoDBClient instance;
  private final MongoClient mongoClient;
  private final MongoCollection<Document> logs;

  private MongoDBClient() {
    mongoClient = MongoClients.create(CONNECTION_STRING);
    MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);
    logs = db.getCollection(COLLECTION_NAME);
  }

  public static synchronized MongoDBClient getInstance() {
    if (instance == null) instance = new MongoDBClient();
    return instance;
  }

  public void insertLog(LogEntry entry) {
    Document doc = new Document()
        .append("timestamp",      entry.getTimestamp())
        .append("query",          entry.getQuery())
        .append("clientIp",       entry.getClientIp())
        .append("responseStatus", entry.getResponseStatus())
        .append("resultCount",    entry.getResultCount())
        .append("durationMs",     entry.getDurationMs());

    logs.insertOne(doc);
  }

  public List<Document> getRecentLogs(int limit) {
    List<Document> result  =new ArrayList<>();
    logs.find()
        .sort(Sorts.descending("timestamp"))
        .limit(limit)
        .into(result);

    return result;
  }

  public List<Document> getTopQueries() {
    List<Document> result = new ArrayList<>();
    logs.aggregate(Arrays.asList(
        Aggregates.group("$query", Accumulators.sum("count", 1)),
        Aggregates.sort(Sorts.descending("count")),
        Aggregates.limit(5)
    )).into(result);
    return result;
  }

  public double getAvgDurationLast24h() {
    String since = Instant.now().minus(24, ChronoUnit.HOURS).toString();
    List<Document> result = new ArrayList<>();
    logs.aggregate(Arrays.asList(
        Aggregates.match(Filters.gte("timestamp", since)),
        Aggregates.group(null, Accumulators.avg("avgDuration", "$durationMs"))
    )).into(result);
    if (result.isEmpty())
      return 0.0;
    return result.get(0).getDouble("avgDuration");
  }

  public double getErrorRate() {
    long total = logs.countDocuments();
    long errors = logs.countDocuments(Filters.ne("responseStatus", 200));
    if (total == 0) return 0.0;
    return (errors * 100.0) / total;
  }
}
