import java.util.List;

/**
 * A dietitian: the professional who reviews what a patient eats and prepares a
 * meal plan for them.
 *
 * <p>Like a nurse, a dietitian cannot prescribe, so this class does not
 * implement {@link Prescriber}. It adds an area of dietary focus and the number
 * of patients it can see in a week.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 2.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    /** The area of dietary focus, for example "Diabetes management". */
    private final String focusArea;

    /** The maximum number of patients this dietitian can see in one week. */
    private final int weeklyCapacity;

    /**
     * Creates a dietitian with an explicit roster, focus area and capacity.
     *
     * @param id             the identifier, digits only
     * @param name           the full name
     * @param workingDays    the days this dietitian works
     * @param focusArea      the area of dietary focus; must not be blank
     * @param weeklyCapacity the number of patients per week; must be positive
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, List<WeekDay> workingDays,
                     String focusArea, int weeklyCapacity) {
        // Shared state comes from the superclass; the focus area and the weekly
        // capacity are the values that only a dietitian has.
        super(id, name, workingDays);
        // A dietitian with no focus area or with no capacity would print an empty
        // line or a nonsensical load, so both values are checked before they are
        // stored and the object is never left in that state.
        if (focusArea == null || focusArea.trim().isEmpty()) {
            throw new IllegalArgumentException("A dietitian must have an area of focus.");
        }
        if (weeklyCapacity <= 0) {
            throw new IllegalArgumentException("Weekly capacity must be greater than zero.");
        }
        this.focusArea = focusArea.trim();
        this.weeklyCapacity = weeklyCapacity;
    }

    /**
     * Creates a dietitian who works Monday to Friday and sees a standard load
     * of twenty patients a week.
     *
     * @param id        the identifier, digits only
     * @param name      the full name
     * @param focusArea the area of dietary focus; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, String focusArea) {
        this(id, name, WEEKDAYS, focusArea, 20);
    }

    /**
     * Returns the area of dietary focus.
     *
     * @return the focus area
     */
    public String getFocusArea() {
        return focusArea;
    }

    /**
     * Returns the weekly capacity of this dietitian.
     *
     * @return the number of patients per week
     */
    public int getWeeklyCapacity() {
        return weeklyCapacity;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code "Dietitian"}
     */
    @Override
    public String getRole() {
        return "Dietitian";
    }

    /**
     * {@inheritDoc}
     *
     * @param lines the list of formatted lines to append to
     */
    @Override
    protected void addRoleDetails(List<String> lines) {
        lines.add(detailLine("Focus area", focusArea));
        lines.add(detailLine("Weekly load", weeklyCapacity + " patients"));
        lines.add(detailLine("Can prescribe", "No"));
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of what a dietitian does
     */
    @Override
    public String performService() {
        return "reviews the patient's diet and prepares a personalised meal plan";
    }

    /**
     * Prepares a meal plan for the patient currently being seen.
     *
     * <p>This behaviour belongs to dietitians only, so it is declared here
     * rather than in {@link HealthProfessional}.</p>
     *
     * @return a description of the meal plan
     */
    public String createMealPlan() {
        return getName() + " (" + getRole() + ") prepares a meal plan focused on "
                + focusArea.toLowerCase() + ".";
    }
}
