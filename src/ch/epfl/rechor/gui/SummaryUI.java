package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Record representing the list of journeys.
 *
 * @param rootNode the root used to represent the summary of the journey for a given date.
 * @param selectedJourneyO the journey that needs to be seen in DetailUI.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    /**
     * Creates a SummaryUI bound to the supplied observables.
     *
     * @param listOfJourneys an observable list of journeys to show
     * @param journeyTime    a reference time used to auto-select the first journey
     *                       that departs on or after it
     * @return the new SummaryUI
     */
    public static SummaryUI create(ObservableValue<List<Journey>> listOfJourneys,
                                   ObservableValue<LocalTime> journeyTime){

        ListView<Journey> listView = new ListView<>();
        listView.setCellFactory(view -> new JourneyListCell());

        listOfJourneys.subscribe((journeyList -> {

            if (journeyList == null){
                listView.getItems().setAll(List.of());
            }
            else {
                listView.getItems().setAll(journeyList);
            }
            selectJourneyInListView(journeyTime.getValue(), listView);
        }));

        journeyTime.subscribe( newTime -> {
            selectJourneyInListView(newTime,listView);
        });

        listView.getStylesheets().add("summary.css");

        return new SummaryUI(listView, listView.getSelectionModel().selectedItemProperty());
    }

    private static void selectJourneyInListView(LocalTime localTime, ListView<Journey> listView){
        boolean defaultSelection = true;
        for (Journey j : listView.getItems() ) {
            if (!localTime.isAfter(j.depTime().toLocalTime())) {
                listView.getSelectionModel().select(j);
                listView.scrollTo(j);
                defaultSelection = false;
                break;
            }
        }
        if (listView.getItems().isEmpty()) return;
        else if (defaultSelection) {
            listView.getSelectionModel().select(listView.getItems().getLast());
            listView.scrollTo(listView.getSelectionModel().getSelectedItem());
        }
    }

    private static class JourneyListCell extends ListCell<Journey> {

        private final BorderPane borderPane;
        private final Text depTime;
        private final Text arrTime;
        private final HBox routeHBox;
        private final HBox durationHBox;
        private final Text duration;
        private final ImageView imageView;
        private final Text textDirection;
        private final Pane centerPane;
        private final Line line;
        private final Group group;
        private static final double MARGIN = 5;
        private static final int CIRCLE_RADIUS = 3;

        private JourneyListCell(){

            this.textDirection = new Text();
            this.imageView = new ImageView();
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            this.routeHBox = new HBox(imageView, textDirection);
            routeHBox.getStyleClass().add("route");

            this.line = new Line();
            this.group = new Group();
            this.centerPane  = new Pane(line, group) {

                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                    line.setStartX(MARGIN);
                    line.setStartY(getHeight()/2.0);
                    line.setEndX(getWidth() - MARGIN);
                    line.setEndY(getHeight()/2.0);
                    for (Node child : group.getChildren()) {
                        if (child instanceof Circle) {
                            double ratio = ((Number) child.getUserData()).doubleValue();
                            Circle circle = (Circle) child;
                            double circlePos = MARGIN + ratio * (getWidth() - 2 * MARGIN);
                            circle.setCenterY(getHeight()/2.0);
                            circle.setCenterX(circlePos);
                        }
                    }
                }
            };

            centerPane.setPrefSize(0,0);

            this.duration = new Text();
            this.durationHBox = new HBox(duration);
            durationHBox.getStyleClass().add("duration");

            this.depTime = new Text();
            depTime.getStyleClass().add("departure");
            this.arrTime = new Text();

            this.borderPane = new BorderPane(centerPane, routeHBox, arrTime, durationHBox, depTime);
            borderPane.getStyleClass().add("journey");
        }

        @Override
        protected void updateItem(Journey journey, boolean empty ) {
            super.updateItem(journey, empty);

            if (empty || journey == null) {
                setText(null);
                setGraphic(null);
            } else {
                Journey.Leg.Transport firstTransport = getFirstTransport(journey);

                long journeyDuration = journey.duration().toMinutes();
                LocalDateTime journeyDepTime = journey.depTime();
                depTime.setText(FormatterFr.formatTime(journey.depTime()));
                arrTime.setText(FormatterFr.formatTime(journey.arrTime()));

                if (firstTransport != null) {
                    imageView.setImage(VehicleIcons.iconFor(firstTransport.vehicle()));
                    textDirection.setText(FormatterFr.formatRouteDestination(firstTransport));
                }

                duration.setText(FormatterFr.formatDuration(journey.duration()));

                group.getChildren().clear();

                Circle depCircle = new Circle(CIRCLE_RADIUS);
                Circle arrCircle = new Circle(CIRCLE_RADIUS);

                depCircle.getStyleClass().add("dep-arr");
                arrCircle.getStyleClass().add("dep-arr");

                depCircle.setUserData(0);
                arrCircle.setUserData(1);

                List<Circle> whiteCircles = new ArrayList<>();

                List <Journey.Leg> journeyLegs = journey.legs();

                for (int i = 0; i < journeyLegs.size() ; i++) {
                    Journey.Leg currentLeg = journeyLegs.get(i);
                    if (currentLeg instanceof Journey.Leg.Foot
                            && i > 0 && i < journeyLegs.size() - 1) {
                        LocalDateTime currentTime = currentLeg.depTime();
                        double minutesElapsed = Duration.between(journeyDepTime, currentTime).
                                toMinutes();
                        Circle transferCircle = new Circle(CIRCLE_RADIUS);
                        transferCircle.getStyleClass().add("transfer");
                        double ratioToAssign = minutesElapsed / journeyDuration;
                        transferCircle.setUserData(ratioToAssign);
                        whiteCircles.add(transferCircle);
                    }
                }

                group.getChildren().setAll(whiteCircles);
                group.getChildren().addAll(depCircle, arrCircle);
                setGraphic(borderPane);
            }
        }
    }

    /**
     * Checks journey and returns its first leg, or null if the journey has none.
     *
     * @param journey the journey to inspect
     * @return the first transport leg, or {@code null} when absent
     */
    public static Journey.Leg.Transport getFirstTransport(Journey journey) {
        for (Journey.Leg leg : journey.legs()) {
            if (leg instanceof Journey.Leg.Transport) {
                return (Journey.Leg.Transport) leg;
            }
        }
        return null;
    }
}