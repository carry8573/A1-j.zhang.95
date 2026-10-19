/**
 * The days of the week that a health professional can be rostered to work.
 *
 * <p>The system stores the days a professional works as a collection of these
 * constants rather than as free text, so an invalid day (for example a typo
 * such as "Monay") can never be stored. Using an enumeration keeps the set of
 * legal values closed, gives the rest of the program a well defined type to
 * work with, and lets each constant carry its own human readable label.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 1.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public enum WeekDay {

    /** Monday. */
    MONDAY("Monday"),
    /** Tuesday. */
    TUESDAY("Tuesday"),
    /** Wednesday. */
    WEDNESDAY("Wednesday"),
    /** Thursday. */
    THURSDAY("Thursday"),
    /** Friday. */
    FRIDAY("Friday"),
    /** Saturday. */
    SATURDAY("Saturday"),
    /** Sunday. */
    SUNDAY("Sunday");

    /** The label shown to a user, for example "Monday". */
    private final String displayName;

    /**
     * Creates a weekday constant.
     *
     * @param displayName the label shown to a user
     */
    WeekDay(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the label of this day suitable for display, for example
     * {@code "Monday"}.
     *
     * @return the display name of this day
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the display name so that printing a constant directly produces
     * readable output instead of the upper case constant name.
     *
     * @return the display name of this day
     */
    @Override
    public String toString() {
        return displayName;
    }
}
