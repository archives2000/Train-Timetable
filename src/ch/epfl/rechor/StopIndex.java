package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents an index of stop names in which it's possible to sort the stops according
 * to the query that was given.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class StopIndex {
    private final List<String> indexedStopNames;
    private final Map<String, String> alternativeNamesMap;
    private final static int BEGINNING_OCCURRENCE_FACTOR = 4;
    private final static int ENDING_OCCURRENCE_FACTOR = 2;

    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    private static final Pattern SPLITTING_PATTERN = Pattern.compile("[aeiouc]");

    private static final Map<String, String> CHAR_REPLACEMENT =
            new HashMap<>(Map.of("a", "[aàâäá]", "e",
                    "[eéèêë]", "i", "[iîïìí]", "o", "[oôöòó]",
                    "u", "[uûùü]", "c", "[cç]"));

    private record StopScores(String stopName, int score) {}

    /**
     * Builds an index ready for auto-completion.
     *
     * @param stopName list of the stop's names to index
     * @param map      map associating the alternative stop's names to their main one's
     */
    public StopIndex(List<String> stopName, Map<String, String> map) {
        indexedStopNames = stopName;
        alternativeNamesMap = map;
    }

    /**
     * Method used to convert a query into a list of corresponding stops.
     *
     * @param query            user's query when searching for an arrival station
     * @param maxNumberOfStops the maximum number of stations suggested for a query
     * @return list of the stop's names corresponding to the query
     */
    public List<String> stopsMatching(String query, int maxNumberOfStops) {

        final String[] subQueries = SPACE_PATTERN.split(query);
        final List<Pattern> subQueriesPatterns = new ArrayList<>();

        for (String subQuery : subQueries) {
            subQueriesPatterns.add(pattern(subQuery));
        }

        Stream <String> originalStopsStream = indexedStopNames.stream();
        Stream <String> aliaseNamesStream = alternativeNamesMap.keySet().stream();
        List <String> matchingStops = Stream.concat(originalStopsStream, aliaseNamesStream)
                .map(stopName -> {
                    return stopScore(subQueriesPatterns, stopName);

                })
                .filter(Objects::nonNull)
                .sorted((firstPair, secondPair) ->
                        Integer.compare(secondPair.score(), firstPair.score()))
                .map((scorePair) -> scorePair.stopName)
                .map((stopName) -> alternativeNamesMap.getOrDefault(stopName, stopName))
                .distinct()
                .limit(maxNumberOfStops)
                .collect(Collectors.toList());

        return matchingStops;
    }

    /**
     * Computes a relevance score for the stop name against every pattern in the pattern list.
     *
     * @param patternList compiled sub-query patterns
     * @param stopName    the stop name being scored
     * @return a stop score record, or null if no match
     */
    private static StopScores stopScore (List <Pattern> patternList, String stopName) {

        int totalScore = 0;

        for (Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(stopName);
            if (matcher.find()) {
                int sequenceFound = matcher.end() - matcher.start();
                int currentSubQueryScore = (int) ((double) sequenceFound / stopName.length() * 100);
                if (matcher.start() == 0 || !Character.isLetter(stopName.charAt(matcher.start() - 1))) {
                    currentSubQueryScore *= BEGINNING_OCCURRENCE_FACTOR;
                }
                if (matcher.end() == stopName.length() ||
                        !Character.isLetter(stopName.charAt(matcher.end()))) {
                    currentSubQueryScore *= ENDING_OCCURRENCE_FACTOR;
                }
                totalScore += currentSubQueryScore;
            }
            else {
                return null;
            }
        }
        return patternList.isEmpty() ? null : new StopScores(stopName, totalScore);
    }

    /**
     * Turns a sub-query into a regex that tolerates accents and optionally ignores case.
     *
     * @param subquery the raw sub-query
     * @return the compiled pattern
     */
    public static Pattern pattern(String subquery) {
        boolean caseSensitive = !subquery.equals(subquery.toLowerCase());

        String toBeCompiled =
                Arrays.stream(SPLITTING_PATTERN.splitWithDelimiters(subquery,0))
                        .map(chunkOfLetters -> {
                            return CHAR_REPLACEMENT.getOrDefault
                                    (chunkOfLetters, Pattern.quote(chunkOfLetters));
                        })
                        .collect(Collectors.joining());

        return caseSensitive ? Pattern.compile(toBeCompiled) :
                Pattern.compile(toBeCompiled,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}