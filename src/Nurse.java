import java.util.List;

/**
 * A nurse: the professional who takes observations, gives routine care and
 * collects blood samples.
 *
 * <p>A nurse is a clear example of a type that <em>cannot</em> prescribe, so this
 * class deliberately does not implement {@link Prescriber}; it adds the blood
 * sampling behaviour instead. That difference between the types is exactly what
 * Part 2 asks to be modelled.</p>
 *
 * <p>The level of a nurse is a closed set of values, so it is modelled with a
 * nested enumeration rather than a string. That keeps "Registered Nurse" from
 * being stored as "registered nurse" in one place and "RN" in another.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 2.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Nurse extends HealthProfessional {

    /**
     * The recognised levels of nursing registration.
     */
    public enum Level {

        /** An enrolled nurse, who works under the supervision of a registered nurse. */
        ENROLLED_NURSE("Enrolled Nurse"),
        /** A registered nurse, who can practise independently. */
        REGISTERED_NURSE("Registered Nurse"),
        /** A nurse practitioner, who can diagnose and prescribe in some settings. */
        NURSE_PRACTITIONER("Nurse Practitioner");

        /** The label shown to a user. */
        private final String displayName;

        /**
         * Creates a level constant.
         *
         * @param displayName the label shown to a user
         */
        Level(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the label of this level.
         *
         * @return the display name, for example {@code "Registered Nurse"}
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the display name so that printing a level is readable.
         *
         * @return the display name
         */
        @Override
        public String toString() {
            return displayName;
        }
    }

    /** The registration level of this nurse. */
    private final Level level;

    /** How many years this nurse has been practising. */
    private final int yearsOfExperience;

    /**
     * Creates a nurse with an explicit roster, level and experience.
     *
     * @param id               the identifier, digits only
     * @param name             the full name
     * @param workingDays      the days this nurse works
     * @param level            the nursing level; must not be null
     * @param yearsOfExperience the years of experience; must not be negative
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, List<WeekDay> workingDays,
                 Level level, int yearsOfExperience) {
        super(id, name, workingDays);
        if (level == null) {
            throw new IllegalArgumentException("A nurse must have a nursing level.");
        }
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience must not be negative.");
        }
        this.level = level;
        this.yearsOfExperience = yearsOfExperience;
    }

    /**
     * Creates a registered nurse who works Monday to Friday and has just
     * started, which is the most common case in the clinic.
     *
     * @param id   the identifier, digits only
     * @param name the full name
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name) {
        this(id, name, WEEKDAYS, Level.REGISTERED_NURSE, 0);
    }

    /**
     * Returns the registration level of this nurse.
     *
     * @return the nursing level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Returns the years of experience of this nurse.
     *
     * @return the years of experience
     */
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    /**
     * {@inheritDoc}
     *
     * @return the role, always {@code "Nurse"}; the level is part of the
     *         details printed by {@link #getDescription()}
     */
    @Override
    public String getRole() {
        return "Nurse";
    }

    /**
     * {@inheritDoc}
     *
     * @param lines the list of formatted lines to append to
     */
    @Override
    protected void addRoleDetails(List<String> lines) {
        lines.add(detailLine("Level", level.getDisplayName()));
        lines.add(detailLine("Experience", yearsOfExperience + " years"));
        lines.add(detailLine("Can prescribe", "No"));
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of what a nurse does
     */
    @Override
    public String performService() {
        return "takes observations, provides routine care and collects blood samples";
    }

    /**
     * Takes a blood sample from the patient currently being seen.
     *
     * <p>This behaviour belongs to nurses only, so it is declared here rather
     * than in {@link HealthProfessional}.</p>
     *
     * @return a description of the sample being taken
     */
    public String takeBloodSample() {
        return getName() + " (" + level.getDisplayName()
                + ") collects a blood sample for pathology.";
    }
}
