import java.util.List;

/**
 * A general practitioner: the family doctor who sees patients for everyday
 * medical problems and who can prescribe medication.
 *
 * <p>A general practitioner is a {@link HealthProfessional} (it inherits the
 * identifier, the name and the roster) and it is also a {@link Prescriber}
 * (it implements the prescribing behaviour). It adds two pieces of information
 * of its own - the consulting room it works from and whether it bulk bills.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 2.</p>
 *
 * <p>Prescribing is added through the {@link Prescriber} interface rather than by
 * putting a prescribing method on {@link HealthProfessional}. Only some
 * professionals may prescribe, so the ability is modelled as a role that a class
 * takes on, and the main program can ask "is this professional a prescriber?"
 * without knowing which class it is looking at. That gives a second, independent
 * kind of polymorphism next to the inheritance that already exists.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional implements Prescriber {

    /** The consulting room this doctor works from, for example {@code "3B"}. */
    private final String roomNumber;

    /** Whether this doctor bulk bills, so the patient pays nothing. */
    private final boolean bulkBilling;

    /**
     * Creates a general practitioner with an explicit roster, room and billing
     * arrangement.
     *
     * @param id          the identifier, digits only
     * @param name        the full name
     * @param workingDays the days this doctor works
     * @param roomNumber  the consulting room; must not be blank
     * @param bulkBilling whether the doctor bulk bills
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, List<WeekDay> workingDays,
                               String roomNumber, boolean bulkBilling) {
        // The superclass constructor runs first and has already stored and
        // validated the shared state; this constructor only adds the values that
        // belong to a general practitioner.
        super(id, name, workingDays);
        // The room is stored through a validator, so the field can never hold a
        // blank value and cannot be changed afterwards.
        this.roomNumber = validateRoomNumber(roomNumber);
        this.bulkBilling = bulkBilling;
    }

    /**
     * Creates a general practitioner who works Monday to Friday, bulk bills and
     * needs only a room number supplied.
     *
     * <p>This overloaded constructor exists so that the most common case is
     * short to write, while the longer constructor above stays available for an
     * unusual roster.</p>
     *
     * @param id         the identifier, digits only
     * @param name       the full name
     * @param roomNumber the consulting room; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, String roomNumber) {
        // Delegating instead of copying the code keeps a single validation path:
        // every value is still checked by the constructor above.
        this(id, name, WEEKDAYS, roomNumber, true);
    }

    /**
     * Checks that a room number is present.
     *
     * @param roomNumber the room number to check
     * @return the room number with surrounding spaces removed
     * @throws IllegalArgumentException if the room number is null or blank
     */
    private static String validateRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("A general practitioner must have a room number.");
        }
        return roomNumber.trim();
    }

    /**
     * Returns the room number of this doctor.
     *
     * @return the consulting room
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Reports whether this doctor bulk bills.
     *
     * @return {@code true} if the doctor bulk bills
     */
    public boolean isBulkBilling() {
        return bulkBilling;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code "General Practitioner"}
     */
    @Override
    public String getRole() {
        return "General Practitioner";
    }

    /**
     * {@inheritDoc}
     *
     * @param lines the list of formatted lines to append to
     */
    @Override
    protected void addRoleDetails(List<String> lines) {
        // Only the details unique to this class are added here. The identifier,
        // the name and the roster are already listed by the superclass, so
        // toString() shows shared and unique state together without either class
        // needing to know the other's fields.
        lines.add(detailLine("Room", roomNumber));
        lines.add(detailLine("Bulk billing", bulkBilling ? "Yes" : "No"));
        lines.add(detailLine("Can prescribe", "Yes"));
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of what a general practitioner does
     */
    @Override
    public String performService() {
        return "conducts a general consultation, diagnoses common conditions and prescribes medication";
    }

    /**
     * {@inheritDoc}
     *
     * @param medicationName the medication and dose
     * @return the prescription as readable text
     * @throws IllegalArgumentException if the medication name is blank
     */
    @Override
    public String prescribe(String medicationName) {
        if (medicationName == null || medicationName.trim().isEmpty()) {
            throw new IllegalArgumentException("The medication name must not be empty.");
        }
        return getName() + " (" + getRole() + ") prescribes " + medicationName.trim() + ".";
    }
}
