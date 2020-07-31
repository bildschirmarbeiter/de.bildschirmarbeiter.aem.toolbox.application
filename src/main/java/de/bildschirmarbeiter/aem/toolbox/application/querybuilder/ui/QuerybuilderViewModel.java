package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryResult;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryResultParser;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryCommand;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryResultEvent;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import org.apache.commons.lang3.StringUtils;
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
    private volatile QueryResultParser resultParser;

    @Reference
    private volatile MessageService messageService;

    // server
    final ObservableList<String> schemes = FXCollections.observableArrayList("https", "http");

    final StringProperty scheme = new SimpleStringProperty(schemes.get(0));

    final StringProperty host = new SimpleStringProperty("localhost");

    final StringProperty port = new SimpleStringProperty("4502");

    final StringProperty username = new SimpleStringProperty("admin");

    final StringProperty password = new SimpleStringProperty("admin");

    // query
    final StringProperty query = new SimpleStringProperty();

    // result

    final StringProperty result = new SimpleStringProperty();

    final ObservableList<String> lines = FXCollections.observableArrayList();

    final BooleanProperty success = new SimpleBooleanProperty();

    final LongProperty results = new SimpleLongProperty();

    final LongProperty total = new SimpleLongProperty();

    final BooleanProperty more = new SimpleBooleanProperty();

    final LongProperty offset = new SimpleLongProperty();

    private final List<Map<String, ?>> hits = new ArrayList<>();

    final ObservableList<String> resultList = FXCollections.observableArrayList();

    final FilteredList<String> filteredResultList = new FilteredList<>(resultList);

    // templating

    final StringProperty template = new SimpleStringProperty("{{this}}");

    private final ChangeListener<String> templateChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        compileTemplate();
        processHits();
    };

    private Template handlebarsTemplate;

    // templating filter

    final StringProperty filterExpression = new SimpleStringProperty();

    private final ChangeListener<String> filterExpressionChangeListener = (observable, oldValue, newValue) -> filteredResultList.setPredicate(string -> filter(string, newValue));

    final ObjectProperty<FilterMode> filterMode = new SimpleObjectProperty<>(FilterMode.CONTAINS);

    private final ChangeListener<FilterMode> filterModeChangeListener = (observable, oldValue, newValue) -> filteredResultList.setPredicate(string -> filter(string, filterExpression.getValue()));

    final ObservableList<FilterMode> filterModes = FXCollections.observableArrayList(FilterMode.CONTAINS, FilterMode.MATCHES);

    // scripting
    final private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    final StringProperty script = new SimpleStringProperty("var json = JSON.parse(result);\nJSON.stringify(json, null, 2);");

    final StringProperty scriptOutput = new SimpleStringProperty();

    private final ChangeListener<String> scriptChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        renderScript();
    };

    //

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final JsonParser jsonParser = new JsonParser();

    public QuerybuilderViewModel() {
    }

    @Activate
    private void activate() {
        compileTemplate();
        template.addListener(templateChangeListener);
        filterExpression.addListener(filterExpressionChangeListener);
        filterMode.addListener(filterModeChangeListener);
        script.addListener(scriptChangeListener);
        messageService.register(this);
    }

    @Deactivate
    private void deactivate() {
        template.removeListener(templateChangeListener);
        filterExpression.removeListener(filterExpressionChangeListener);
        filterMode.removeListener(filterModeChangeListener);
        script.removeListener(scriptChangeListener);
        messageService.unregister(this);
    }

    @Subscribe
    public void onQueryCommand(final QueryCommand command) {
        clear();
    }

    @Subscribe
    public void onQueryResultEvent(final QueryResultEvent event) {
        clear();
        try {
            final JsonObject jso = jsonParser.parse(event.getQueryResult()).getAsJsonObject();
            final String json = gson.toJson(jso);
            result.setValue(json);
            final Scanner scanner = new Scanner(json);
            final List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            this.lines.addAll(lines);
        } catch (Exception e) {
            result.setValue(event.getQueryResult());
        }
        final QueryResult queryResult = resultParser.parseResult(event.getQueryResult());
        success.setValue(queryResult.isSuccess());
        results.setValue(queryResult.getResults());
        total.setValue(queryResult.getTotal());
        more.setValue(queryResult.hasMore());
        offset.setValue(queryResult.getOffset());
        hits.addAll(queryResult.getHits());
        processHits();
        renderScript();
    }

    private void compileTemplate() {
        try {
            handlebarsTemplate = handlebars.compileInline(template.getValueSafe());
        } catch (Exception e) {
            messageService.send(LogMessage.error(this, e.getMessage()));
        }
    }

    private String renderHit(final Map<String, ?> hit) {
        try {
            return handlebarsTemplate.apply(hit);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void processHits() {
        resultList.clear();
        resultList.addAll(hits.stream().map(this::renderHit).collect(Collectors.toList()));
    }

    private void renderScript() {
        if (StringUtils.isNotEmpty(result.getValue()) && StringUtils.isNotEmpty(script.getValue())) {
            final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
            final Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("result", result.getValue());
            try {
                final String value = (String) scriptEngine.eval(script.getValue());
                scriptOutput.setValue(value);
            } catch (Exception e) {
                scriptOutput.setValue(null);
                messageService.send(LogMessage.error(this, e.getMessage()));
            }
        }
    }

    private void clear() {
        result.setValue(null);
        success.setValue(false);
        results.setValue(0);
        total.setValue(0);
        more.setValue(false);
        offset.setValue(0);
        hits.clear();
        resultList.clear();
        lines.clear();
        scriptOutput.setValue(null);
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
