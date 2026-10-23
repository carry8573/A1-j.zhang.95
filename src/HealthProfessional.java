import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A health professional who works at the clinic.
 *
 * <p>This is the common, generalised class required by Part 1 and it is the
 * foundation of the whole design. Every professional has the same three pieces
 * of information - an identifier made of digits only, a name, and the fixed set
 * of days each week that the professional works - so that information is
 * declared once, here, and inherited by every specific type.</p>
 *
 * <p>The class is {@code abstract} on purpose. A "plain" health professional has
 * no role and no service to offer, so it would be meaningless to create one;
 * making the class abstract means the compiler prevents it, and it also forces
 * every subclass to supply its own role name, its own extra details and its own
 * service. That is how the "a generic professional must not be instantiable"
 * requirement is met.</p>
 *
 * <p>Every field is {@code final} and every value is validated in the
 * constructor, so an object can never exist in an invalid state: once a
 * {@code HealthProfessional} has been constructed, its details cannot be
 * changed to something wrong.</p>
 *
 * <p>PROG2004 - Assessment 1, Parts 1 and 2.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public abstract class HealthProfessional {

    /**
     * A sensible default roster of Monday to Friday, used by the convenience
     * constructor and by subclasses when the caller does not supply days.
     * It is unmodifiable so no external code can change the default itself.
     */
    protected static final List<WeekDay> WEEKDAYS = Collections.unmodifiableList(
            Arrays.asList(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                    WeekDay.THURSDAY, WeekDay.FRIDAY));

    /** The identifier of this professional. Digits only. */
    private final String id;

    /** The full name of this professional. */
    private final String name;

    /** The days of the week this professional is rostered to work. */
    private final List<WeekDay> workingDays;

    /**
     * Creates a health professional with an explicit roster.
     *
     * @param id          the identifier, digits only, for example {@code "1001"}
     * @param name        the full name, for example {@code "Dr Alice Nguyen"}
     * @param workingDays the days this professional works; not null, not empty,
     *                    and containing no null entries
     * @throws IllegalArgumentException if any argument is invalid
     */
    public HealthProfessional(String id, String name, List<WeekDay> workingDays) {
        // Validation happens before assignment so that a half built, invalid
        // object can never be referenced from outside this constructor.
        this.id = validateId(id);
        this.name = validateName(name);
        this.workingDays = validateWorkingDays(workingDays);
    }

    /**
     * Creates a health professional who works the standard Monday to Friday
     * week. This overloaded constructor is the "sensible default" required by
     * Part 1: a caller who does not care about the roster does not have to
     * repeat the five weekday constants.
     *
     * @param id   the identifier, digits only
     * @param name the full name
     * @throws IllegalArgumentException if the identifier or name is invalid
     */
    public HealthProfessional(String id, String name) {
        this(id, name, WEEKDAYS);
    }

    /**
     * Checks that an identifier consists of digits only and returns it trimmed.
     *
     * <p>The check walks over the characters one at a time rather than using a
     * regular expression, because a simple loop is easier to read here and makes
     * the rule ("every character must be between '0' and '9'") explicit.</p>
     *
     * @param id the identifier to check
     * @return the identifier with surrounding spaces removed
     * @throws IllegalArgumentException if the identifier is null, blank or
     *                                  contains a character that is not a digit
     */
    private static String validateId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Health professional ID must not be null.");
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Health professional ID must not be empty.");
        }
        for (int i = 0; i < trimmed.length(); i++) {
            char character = trimmed.charAt(i);
            if (character < '0' || character > '9') {
                throw new IllegalArgumentException(
                        "Health professional ID must contain numbers only, but was \"" + id + "\".");
            }
        }
        return trimmed;
    }

    /**
     * Checks that a name is present and returns it trimmed.
     *
     * @param name the name to check
     * @return the name with surrounding spaces removed
     * @throws IllegalArgumentException if the name is null or blank
     */
    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Health professional name must not be empty.");
        }
        return name.trim();
    }

    /**
     * Copies and checks the roster, and wraps the copy so that it cannot be
     * modified later.
     *
     * <p>The defensive copy matters: if the caller kept a reference to the list
     * it passed in and changed it afterwards, this object's roster would change
     * too. Copying on the way in and returning an unmodifiable view on the way
     * out keeps the "never invalid state" promise.</p>
     *
     * @param workingDays the roster supplied by the caller
     * @return an unmodifiable copy of the roster
     * @throws IllegalArgumentException if the roster is null, empty or contains
     *                                  a null day
     */
    private static List<WeekDay> validateWorkingDays(List<WeekDay> workingDays) {
        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalArgumentException(
                    "A health professional must work at least one day of the week.");
        }
        List<WeekDay> copy = new ArrayList<>();
        for (WeekDay day : workingDays) {
            if (day == null) {
                throw new IllegalArgumentException(
                        "The list of working days must not contain a null day.");
            }
            // Adding only days that are not already present keeps the roster
            // free of duplicates such as "Monday, Monday".
            if (!copy.contains(day)) {
                copy.add(day);
            }
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Returns the identifier of this professional.
     *
     * @return the identifier, digits only
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the full name of this professional.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the days this professional works.
     *
     * @return an unmodifiable list of working days
     */
    public List<WeekDay> getWorkingDays() {
        return workingDays;
    }

    /**
     * Reports whether this professional works on a given day.
     *
     * <p>This is used by the appointment manager to refuse a booking for a
     * professional who is not rostered on today.</p>
     *
     * @param day the day to test; must not be null
     * @return {@code true} if this professional works on that day
     */
    public boolean worksOn(WeekDay day) {
        return workingDays.contains(day);
    }

    /**
     * Returns the roster as readable text, for example
     * {@code "Monday, Wednesday, Friday"}.
     *
     * @return the working days separated by commas
     */
    public String getWorkingDaysText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < workingDays.size(); i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(workingDays.get(i).getDisplayName());
        }
        return text.toString();
    }

    /**
     * Returns the name of this professional's role, for example
     * {@code "General Practitioner"}.
     *
     * <p>Every specific type must answer this, which is what makes each object
     * clearly identify its own type when printed.</p>
     *
     * @return the role of this professional
     */
    public abstract String getRole();

    /**
     * Adds the details that are specific to a particular type of professional,
     * such as a room number for a general practitioner or a nursing level.
     *
     * <p>The subclass only supplies its own extra lines; the shared details and
     * the layout are handled once in {@link #getDescription()}.</p>
     *
     * @param lines the list of already formatted lines to append to
     */
    protected abstract void addRoleDetails(List<String> lines);

    /**
     * Performs the service that this type of professional provides.
     *
     * <p>Each subclass returns its own sentence, so a collection of the general
     * type can be looped over and every object responds in its own way. This is
     * the polymorphic behaviour required by Parts 2 and 3.</p>
     *
     * @return a description of the service performed
     */
    public abstract String performService();

    /**
     * Builds the full description of this professional as a list of lines.
     *
     * <p>The layout is defined once here and the subclasses add their own lines
     * through {@link #addRoleDetails(List)}, so adding a new type of
     * professional does not mean copying the printing code. Returning the lines
     * separately also lets {@link Appointment} reuse this description and indent
     * it without having to split text apart again.</p>
     *
     * @return the description, one entry per line
     */
    public List<String> getDescriptionLines() {
        List<String> lines = new ArrayList<>();
        lines.add(getRole() + " (ID: " + id + ")");
        lines.add(detailLine("Name", name));
        lines.add(detailLine("Working days", getWorkingDaysText()));
        addRoleDetails(lines);
        return lines;
    }

    /**
     * Builds the full, readable description of this professional as one block of
     * text.
     *
     * @return the multi line description of this professional
     */
    public String getDescription() {
        StringBuilder text = new StringBuilder();
        List<String> lines = getDescriptionLines();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                text.append(System.lineSeparator());
            }
            text.append(lines.get(i));
        }
        return text.toString();
    }

    /**
     * Formats one "label: value" line so that every line of a description lines
     * up in the same column.
     *
     * @param label the label, for example {@code "Room"}
     * @param value the value, for example {@code "3B"}
     * @return the formatted line
     */
    protected String detailLine(String label, String value) {
        return String.format("   %-13s: %s", label, value);
    }

    /**
     * Returns the full description, so that
     * {@code System.out.println(professional)} prints every detail of the
     * object as required by Part 1.
     *
     * @return the multi line description of this professional
     */
    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * Compares this professional with another object.
     *
     * <p>Two health professionals are the same professional when they have the
     * same identifier - the identifier is the unique key that the clinic uses,
     * so the Java way to express that is to override {@code equals} (and
     * {@code hashCode}, which must always be overridden alongside it) and to
     * compare the identifiers. This is what lets the appointment manager ask
     * "is this the same professional?" without comparing names, which could
     * legitimately be identical.</p>
     *
     * @param other the object to compare with
     * @return {@code true} if both objects are professionals with the same ID
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the identifier
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
