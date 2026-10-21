/**
 * The ability to prescribe medication.
 *
 * <p>Not every health professional can prescribe: general practitioners and
 * specialists can, whereas nurses and dietitians cannot. Rather than forcing
 * that difference into the inheritance tree (which would lead to a hierarchy of
 * "professionals who can prescribe" and "professionals who cannot"), the
 * capability is modelled as a separate interface that only the classes that
 * really have the capability implement.</p>
 *
 * <p>This means the rest of the program can ask "are you a prescriber?" with a
 * single {@code instanceof} check and then call {@link #prescribe(String)}
 * without knowing whether the object is a general practitioner or a
 * specialist - a second, independent form of polymorphism alongside the
 * inheritance used by {@code HealthProfessional}.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 2.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public interface Prescriber {

    /**
     * Prescribes a medication to the patient currently being seen.
     *
     * @param medicationName the name and dose of the medication, for example
     *                       {@code "Amoxicillin 500 mg"}; must not be blank
     * @return a description of the prescription, suitable for display
     * @throws IllegalArgumentException if {@code medicationName} is null or blank
     */
    String prescribe(String medicationName);
}
