package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryResult;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryCommand;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryResultEvent;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = QuerybuilderViewModel.class
)
public class QuerybuilderViewModel {

    @Reference
    private volatile Handlebars handlebars;

    @Reference
    private volatile MessageService messageService;

    // server
    final StringProperty host = new SimpleStringProperty("localhost");

    final StringProperty port = new SimpleStringProperty("4502");

    final StringProperty username = new SimpleStringProperty("admin");

    final StringProperty password = new SimpleStringProperty("admin");

    // query
    final StringProperty query = new SimpleStringProperty();

    // result

    final BooleanProperty success = new SimpleBooleanProperty();

    final LongProperty results = new SimpleLongProperty();

    final LongProperty total = new SimpleLongProperty();

    final BooleanProperty more = new SimpleBooleanProperty();

    final LongProperty offset = new SimpleLongProperty();

    private final List<Map> hits = new ArrayList<>();

    final ObservableList<String> result = FXCollections.observableArrayList();

    final FilteredList<String> filteredResult = new FilteredList<>(result);

    // template

    final StringProperty template = new SimpleStringProperty("{{this}}");

    private final ChangeListener<String> templateChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        compileTemplate();
        processHits();
    };

    private Template handlebarsTemplate;

    // filter

    final StringProperty filterExpression = new SimpleStringProperty();

    private final ChangeListener<String> filterExpressionChangeListener = (observable, oldValue, newValue) -> filteredResult.setPredicate(string -> filter(string, newValue));

    final ObjectProperty<FilterMode> filterMode = new SimpleObjectProperty<>(FilterMode.CONTAINS);

    private final ChangeListener<FilterMode> filterModeChangeListener = (observable, oldValue, newValue) -> filteredResult.setPredicate(string -> filter(string, filterExpression.getValue()));

    final ObservableList<FilterMode> filterModes = FXCollections.observableArrayList(FilterMode.CONTAINS, FilterMode.MATCHES);

    public QuerybuilderViewModel() {
    }

    @Activate
    private void activate() {
        compileTemplate();
        template.addListener(templateChangeListener);
        filterExpression.addListener(filterExpressionChangeListener);
        filterMode.addListener(filterModeChangeListener);
        messageService.register(this);
    }

    @Deactivate
    private void deactivate() {
        template.removeListener(templateChangeListener);
        filterExpression.removeListener(filterExpressionChangeListener);
        filterMode.removeListener(filterModeChangeListener);
        messageService.unregister(this);
    }

    @Subscribe
    public void onQueryCommand(final QueryCommand command) {
        clear();
    }

    @Subscribe
    public void onQueryResultEvent(final QueryResultEvent event) {
        clear();
        final QueryResult queryResult = event.getQueryResult();
        success.setValue(queryResult.isSuccess());
        results.setValue(queryResult.getResults());
        total.setValue(queryResult.getTotal());
        more.setValue(queryResult.hasMore());
        offset.setValue(queryResult.getOffset());
        hits.addAll(queryResult.getHits());
        processHits();
    }

    private void compileTemplate() {
        try {
            handlebarsTemplate = handlebars.compileInline(template.getValueSafe());
        } catch (Exception e) {
            messageService.send(LogMessage.error(this, e.getMessage()));
        }
    }

    private String renderHit(final Map hit) {
        try {
            return handlebarsTemplate.apply(hit);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void processHits() {
        result.clear();
        result.addAll(hits.stream().map(this::renderHit).collect(Collectors.toList()));
    }

    private void clear() {
        success.setValue(false);
        results.setValue(0);
        total.setValue(0);
        more.setValue(false);
        offset.setValue(0);
        hits.clear();
        result.clear();
    }

    private boolean filter(final String string, final String filterExpression) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        switch (filterMode.get()) {
            case CONTAINS:
                return string.contains(filterExpression);
            case MATCHES:
                return string.matches(filterExpression);
            default:
                return false;
        }
    }

    enum FilterMode {

        CONTAINS("Hit contains"),
        MATCHES("Hit matches");

        private final String title;

        FilterMode(final String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

    }

}
