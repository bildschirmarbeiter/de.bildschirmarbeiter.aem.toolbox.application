package de.bildschirmarbeiter.aem.toolbox.application.querybuilder;

import java.util.List;
import java.util.Map;

public class QueryResult {

    private boolean success;

    private long results;

    private long total;

    private boolean more;

    private long offset;

    private List<Map> hits;

    public QueryResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public long getResults() {
        return results;
    }

    public long getTotal() {
        return total;
    }

    public boolean hasMore() {
        return more;
    }

    public long getOffset() {
        return offset;
    }

    public List<Map> getHits() {
        return hits;
    }

    @Override
    public String toString() {
        return String.format("[success: %s, results: %s, total: %s, more: %s, offset: %s]", success, results, total, more, offset);
    }

}
