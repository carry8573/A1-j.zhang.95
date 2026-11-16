import java.util.List;

/**
 * A specialist: a doctor who has completed advanced training in one area of
 * medicine, such as cardiology or paediatrics.
 *
 * <p>A specialist shares the prescribing ability of a general practitioner, so
 * {@link Prescriber} is implemented here as well rather than being pushed into
 * the {@link HealthProfessional} base class. A specialist adds a field of
 * specialisation and a consultation fee.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 2.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Specialist extends HealthProfessional implements Prescriber {

    /** The area of medicine this specialist practises, for example "Cardiology". */
    private final String specialty;

    /** The fee charged for one consultation, in dollars. */
    private final double consultationFee;

    /**
     * Creates a specialist with an explicit roster, specialty and fee.
     *
     * @param id              the identifier, digits only
     * @param name            the full name
     * @param workingDays     the days this specialist works
     * @param specialty       the area of medicine; must not be blank
     * @param consultationFee the fee in dollars; must not be negative
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, List<WeekDay> workingDays,
                      String specialty, double consultationFee) {
        // The shared state is stored by the superclass; the two values below are
        // the only ones that belong to a specialist.
        super(id, name, workingDays);
        this.specialty = validateSpecialty(specialty);
        this.consultationFee = validateFee(consultationFee);
    }

    /**
     * Creates a specialist who works Monday to Friday and charges no fee,
     * which suits a hospital based public clinic.
     *
     * @param id        the identifier, digits only
     * @param name      the full name
     * @param specialty the area of medicine; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, String specialty) {
        // A specialist in a public hospital clinic charges the patient nothing,
        // so the shorter constructor supplies that default for the fee.
        this(id, name, WEEKDAYS, specialty, 0.0);
    }

    /**
     * Checks that a specialty is present.
     *
     * @param specialty the specialty to check
     * @return the specialty with surrounding spaces removed
     * @throws IllegalArgumentException if the specialty is null or blank
     */
    private static String validateSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("A specialist must have a specialty.");
        }
        return specialty.trim();
    }

    /**
     * Checks that a fee is a sensible amount of money.
     *
     * @param fee the fee to check
     * @return the fee unchanged
     * @throws IllegalArgumentException if the fee is negative or not a number
     */
    private static double validateFee(double fee) {
        if (Double.isNaN(fee) || Double.isInfinite(fee) || fee < 0.0) {
            throw new IllegalArgumentException("The consultation fee must be zero or more.");
        }
        return fee;
    }

    /**
     * Returns the specialty of this doctor.
     *
     * @return the area of medicine
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Returns the consultation fee of this doctor.
     *
     * @return the fee in dollars
     */
    public double getConsultationFee() {
        return consultationFee;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The specialty is included in the role so that printing an object shows
     * clearly which kind of specialist it is.</p>
     *
     * @return the role, for example {@code "Specialist - Cardiology"}
     */
    @Override
    public String getRole() {
        return "Specialist - " + specialty;
    }

    /**
     * {@inheritDoc}
     *
     * @param lines the list of formatted lines to append to
     */
    @Override
    protected void addRoleDetails(List<String> lines) {
        lines.add(detailLine("Specialty", specialty));
        lines.add(detailLine("Fee", String.format("$%.2f", consultationFee)));
        lines.add(detailLine("Can prescribe", "Yes"));
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of what a specialist does
     */
    @Override
    public String performService() {
        return "reviews referrals and treats patients in the " + specialty.toLowerCase() + " clinic";
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
        return getName() + " (" + getRole() + ") prescribes " + medicationName.trim()
                + " as part of specialist treatment.";
    }
}
