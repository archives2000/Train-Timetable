package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Builder class used to construct events in the iCalendar (.ics) format.
 * This class provides methods to create and structure iCalendar events
 * which can be exported as .ics files.
 * <p>
 * It ensures correct formatting of event details, including summary, description,
 * timestamps, and structure handling (BEGIN/END components).
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class IcalBuilder {

    /**
     * List of components currently open in the iCalendar structure.
     */
    private final ArrayList<Component> components = new ArrayList<>();

    /**
     * StringBuilder used to construct the iCalendar content.
     */
    private final StringBuilder stringBuilder = new StringBuilder();

    /**
     * Maximum line length allowed in the iCalendar format before wrapping.
     */
    private static final int LINE_SIZE = 75;

    /**
     * Carriage return and line feed sequence required by the iCalendar format.
     */
    private static final String CRLF = "\r\n";

    /**
     * Enum representing the main components of an iCalendar event.
     */
    public enum Component {
        VCALENDAR,
        VEVENT;
    }

    /**
     * Enum representing possible fields used in an iCalendar file.
     * This centralizes all the possible names relevant for the ReCHor project.
     */
    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION;
    }

    /**
     * Adds a key-value pair to the iCalendar structure.
     * Ensures proper line folding according to the iCalendar standard.
     *
     * @param name  the key name appearing before the colon (":") in the .ics file.
     * @param value the value associated with the name parameter.
     * @return the current instance of {@code IcalBuilder}.
     */
    public IcalBuilder add(Name name, String value) {
        String text = name + ":" + value;
        StringJoiner sj = new StringJoiner(CRLF + " ");

        double numberOfReturns = Math.floor((double) text.length() / LINE_SIZE);
        int includedStartIdx = 0;
        for (int i = 0; i <= numberOfReturns; i++) {
            int length = (i == 0) ? LINE_SIZE : LINE_SIZE - 1;
            int endExcludedIdx = Math.min(includedStartIdx + length, text.length());
            sj.add(text.substring(includedStartIdx, endExcludedIdx));
            includedStartIdx = endExcludedIdx;
        }
        stringBuilder.append(sj).append(CRLF);
        return this;
    }

    /**
     * Formats a given {@link LocalDateTime} into the iCalendar date-time format (YYYYMMDDTHHMMSS).
     *
     * @param date the date to be formatted.
     * @return the formatted date-time string.
     */
    private String icsFormatDate(LocalDateTime date) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR)
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .appendLiteral("T")
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .toFormatter();
        return date.format(fmt);
    }

    /**
     * Adds a date-time entry to the iCalendar structure.
     *
     * @param name     the name of the field being added.
     * @param dateTime the date-time value to be formatted.
     * @return the current instance of {@code IcalBuilder}.
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        stringBuilder.append(name)
                .append(":")
                .append(icsFormatDate(dateTime))
                .append(CRLF);
        return this;
    }

    /**
     * Starts a new component (e.g., VCALENDAR or VEVENT) in the iCalendar structure.
     *
     * @param component the component to start.
     * @return the current instance of {@code IcalBuilder}.
     */
    public IcalBuilder begin(Component component) {
        stringBuilder.append(Name.BEGIN)
                .append(":")
                .append(component)
                .append(CRLF);
        components.add(component);
        return this;
    }

    /**
     * Ends the last started component in the iCalendar structure.
     *
     * @return the current instance of {@code IcalBuilder}.
     * @throws IllegalArgumentException if there is no open component to close.
     */
    public IcalBuilder end() {
        Preconditions.checkArgument(!components.isEmpty());
        Component lastComponentStarted = components.removeLast();
        stringBuilder.append(Name.END)
                .append(":")
                .append(lastComponentStarted)
                .append(CRLF);
        return this;
    }

    /**
     * Builds the final iCalendar string representation.
     * Ensures that all opened components have been properly closed.
     *
     * @return the complete iCalendar event as a string.
     * @throws IllegalArgumentException if some components remain unclosed.
     */
    public String build() {
        Preconditions.checkArgument(components.isEmpty());
        return stringBuilder.toString();
    }
}
