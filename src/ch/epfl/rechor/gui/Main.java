package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Main JavaFX entry point of ReCHor.
 * It loads the timetable, builds the three UI parts
 * (query, summary, detail), wires them together and shows the window.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class Main extends Application {

    private final static int STAGE_MIN_WIDTH = 800;
    private final static int STAGE_MIN_HEIGHT = 600;
    private ObjectBinding<List<Journey>> observableJourneys;
    private Profile profile;
    private String currentArrStation = null;
    private LocalDate currentLocalDate = null;

    /**
     * Launches the JavFX application.
     *
     * @param args command‑line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the numeric identifier of the station whose name equals the station name
     * inside stations, or -1 if no match is found.
     *
     * @param stations    the station list to search
     * @param stationName the human‑readable name to look up
     * @return the station’s index, or -1 when absent
     */
    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (Objects.equals(stations.name(i), stationName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * JavaFX entry‑point.
     *
     * @param stage the primary stage supplied by JavaFX
     * @throws Exception if the timetable fails to load
     */
    @Override
    public void start(Stage stage) throws Exception {

        Path pathToTimetable = Path.of("timetable");

        TimeTable timetable = FileTimeTable.in(pathToTimetable);
        Stations stations = timetable.stations();
        StationAliases stationAliases = timetable.stationAliases();

        List<String> stopNames = new LinkedList<>();

        for (int i = 0; i < stations.size() ; i++) {
            stopNames.add(stations.name(i));
        }

        Map<String, String> alternativeStopNamesTable = new HashMap<>();
        for (int i = 0; i < stationAliases.size(); i++) {
            alternativeStopNamesTable.put(stationAliases.alias(i), stationAliases.stationName(i));
        }

        StopIndex stopIndex = new StopIndex(stopNames,alternativeStopNamesTable);

        QueryUI queryUI = QueryUI.create(stopIndex);

        ObservableValue<String> depStopObserved = queryUI.depStopO();
        ObservableValue<String> arrStopObserved = queryUI.arrStopO();
        ObservableValue<LocalDate> dateObserved = queryUI.date0();
        ObservableValue<LocalTime> timeObserved = queryUI.time0();

        CachedTimeTable cachedTimeTable = new CachedTimeTable(timetable);
        Router router = new Router(cachedTimeTable);

        observableJourneys = Bindings.createObjectBinding(() -> {

                    if (arrStopObserved.getValue().isEmpty()) {
                        profile = null;
                    }

                    if (!arrStopObserved.getValue().isEmpty() && (!Objects.equals(currentLocalDate,
                            dateObserved.getValue()) ||
                            !Objects.equals(currentArrStation, arrStopObserved.getValue()))){

                        profile = router.profile(dateObserved.getValue(),
                                stationId(stations,arrStopObserved.getValue()));
                        currentArrStation = arrStopObserved.getValue();
                        currentLocalDate = dateObserved.getValue();
                    }

                    if (!depStopObserved.getValue().isEmpty() && profile != null) {

                        int depStationId = stationId(stations, depStopObserved.getValue());
                        return JourneyExtractor.journeys(profile, depStationId);
                    }
                    return null;
                },
                arrStopObserved, depStopObserved, dateObserved
        );

        SummaryUI summaryUI = SummaryUI.create(observableJourneys, timeObserved);
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        SplitPane splitPane = new SplitPane(summaryUI.rootNode(), detailUI.rootNode());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(splitPane);
        borderPane.setTop(queryUI.rootNode());

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);

        stage.setMinWidth(STAGE_MIN_WIDTH);
        stage.setMinHeight(STAGE_MIN_HEIGHT);
        stage.setTitle("ReCHor");
        stage.show();

        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());
    }
}