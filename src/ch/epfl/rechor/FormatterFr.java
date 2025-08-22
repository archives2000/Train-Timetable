package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * The formatter class is a utility class that ensures
 * to get every text representation in the right format.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class FormatterFr {

    /**
     * Private constructor to prevent instantiation.
     */
    private FormatterFr() {}

    /**
     * Formats a given duration into a string in hours and minutes.
     *
     * @param duration the duration to format
     * @return a string representing the formatted duration
     */
    public static String formatDuration(Duration duration) {

        long totalMinutes = duration.getSeconds()/60;
        long totalHours = totalMinutes/60;
        long remainingMinutes = totalMinutes % 60;

        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }
        else {
            return totalHours + " h " + remainingMinutes + " min";
        }
    }

    /**
     * Formats a date time into a string in the format "HhMM".
     *
     * @param dateTime the date time to format
     * @return a string representing the formatted time
     */
    public static String formatTime(LocalDateTime dateTime) {

        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        return fmt.format(dateTime);
    }

    /**
     * Formats the platform name of the given stop.
     * If the platform name starts with a digit, it is labeled as "voie";
     * otherwise, it is labeled as "quai".
     *
     * @param stop the stop whose platform name is to be formatted
     * @return a string like "voie 3" or "quai A", or an empty string if the platform is missing
     */
    public static String formatPlatformName(Stop stop) {

        String platformName = stop.platformName();

        if (platformName == null || platformName.isEmpty()) {
            return "";
        }

        char firstChar = platformName.charAt(0);

        if (Character.isDigit(firstChar)) {
            return "voie " + platformName;
        } else {
            return "quai " + platformName;
        }
    }

    /**
     * Formats a walking leg of a journey.
     * If the departure and arrival stops are at the same station, the leg is considered a transfer
     * and labeled as "changement". Otherwise, it is labeled as "trajet à pied".
     *
     * @param footLeg the walking leg to format
     * @return a string representing the formatted walking leg
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        StringBuilder sb = new StringBuilder();

        if (footLeg.isTransfer()) {
            sb.append("changement");
        } else {
            sb.append("trajet à pied");
        }
        sb.append(" (").append(formatDuration(footLeg.duration())).append(")");
        return sb.toString();
    }

    /**
     * Formats a transport leg of a journey, including the departure time and stop,
     * optional departure platform, arrival stop with optional arrival platform, and arrival time.
     *
     * @param leg the transport leg to format
     * @return a string representing the formatted transport leg
     */
    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder sb = new StringBuilder();

        sb.append(formatTime(leg.depTime())).append(" ");
        sb.append(leg.depStop().name());

        String departurePlatform = formatPlatformName(leg.depStop());
        if (!departurePlatform.isEmpty()) {
            sb.append(" (").append(departurePlatform).append(")");
        }

        sb.append(" → ");
        sb.append(leg.arrStop().name());
        sb.append(" (arr. ").append(formatTime(leg.arrTime()));

        String arrivalPlatform = formatPlatformName(leg.arrStop());
        if (!arrivalPlatform.isEmpty()) {
            sb.append(" ").append(arrivalPlatform);
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Formats the route and destination of a transport leg.
     *
     * @param transportLeg the transport leg whose route and destination are to be formatted
     * @return a string representing the formatted route destination
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction " + transportLeg.destination();
    }
}