/**
 * A patient of the clinic, identified by name and mobile phone number.
 *
 * <p>{@code Patient} is a class in its own right rather than two strings inside
 * {@code Appointment}. A patient has data of its own and rules of its own (a
 * mobile number must be a real, ten digit Australian number starting with
 * zero), a patient exists whether or not they currently hold an appointment,
 * and the same patient can book more than one appointment. Giving the concept
 * its own class keeps those rules in one place and lets the appointment manager
 * filter appointments by phone number without knowing anything else about
 * patients.</p>
 *
 * <p>The class is immutable: both fields are final and validated at
 * construction, so a patient's details cannot be corrupted after creation.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 4.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Patient {

    /** The number of digits in an Australian mobile number. */
    private static final int MOBILE_LENGTH = 10;

    /** The full name of the patient. */
    private final String name;

    /** The mobile number of the patient, stored as ten digits without spaces. */
    private final String mobileNumber;

    /**
     * Creates a patient.
     *
     * @param name         the full name; must not be blank
     * @param mobileNumber the mobile number; exactly ten digits starting with
     *                     zero, spaces are ignored
     * @throws IllegalArgumentException if either argument is invalid
     */
    public Patient(String name, String mobileNumber) {
        this.name = validateName(name);
        this.mobileNumber = validateMobileNumber(mobileNumber);
    }

    /**
     * Checks that a name is present.
     *
     * @param name the name to check
     * @return the name with surrounding spaces removed
     * @throws IllegalArgumentException if the name is null or blank
     */
    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A patient must have a name.");
        }
        return name.trim();
    }

    /**
     * Checks that a mobile number is a real Australian mobile number and returns
     * it without spaces, for example {@code "0412345678"}.
     *
     * <p>Spaces are stripped first so that a number entered as
     * {@code "0412 345 678"} is accepted, then every remaining character must be
     * a digit, the number must start with zero and it must be ten digits long.
     * The characters are examined one at a time instead of with a regular
     * expression so that the rule is explicit.</p>
     *
     * @param mobileNumber the number to check
     * @return the number as ten digits
     * @throws IllegalArgumentException if the number is null, blank, not ten
     *                                  digits long, or does not start with zero
     */
    private static String validateMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("A patient must have a mobile number.");
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < mobileNumber.length(); i++) {
            char character = mobileNumber.charAt(i);
            if (character == ' ' || character == '-') {
                // Separators are allowed in the input but are not stored.
                continue;
            }
            if (character < '0' || character > '9') {
                throw new IllegalArgumentException(
                        "A mobile number must contain digits only, but was \"" + mobileNumber + "\".");
            }
            digits.append(character);
        }
        if (digits.length() != MOBILE_LENGTH) {
            throw new IllegalArgumentException("A mobile number must be " + MOBILE_LENGTH
                    + " digits long, but was \"" + mobileNumber + "\".");
        }
        if (digits.charAt(0) != '0') {
            throw new IllegalArgumentException(
                    "An Australian mobile number must start with zero, but was \"" + mobileNumber + "\".");
        }
        return digits.toString();
    }

    /**
     * Returns the full name of the patient.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mobile number of the patient as ten digits.
     *
     * @return the mobile number, for example {@code "0412345678"}
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Returns the mobile number grouped for display, for example
     * {@code "0412 345 678"}.
     *
     * @return the formatted mobile number
     */
    public String getFormattedMobileNumber() {
        return mobileNumber.substring(0, 4) + " " + mobileNumber.substring(4, 7) + " "
                + mobileNumber.substring(7);
    }

    /**
     * Compares this patient with another object.
     *
     * <p>The mobile number identifies a patient, so two patients are equal when
     * their mobile numbers are the same. This is what allows a caller to pass a
     * phone number and have the manager locate everything belonging to that
     * patient reliably.</p>
     *
     * @param other the object to compare with
     * @return {@code true} if both objects describe the same mobile number
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Patient)) {
            return false;
        }
        Patient that = (Patient) other;
        return mobileNumber.equals(that.mobileNumber);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the mobile number
     */
    @Override
    public int hashCode() {
        return mobileNumber.hashCode();
    }

    /**
     * Returns the patient as readable text, for example
     * {@code "Jane Doe (0412 345 678)"}.
     *
     * @return the name and formatted mobile number
     */
    @Override
    public String toString() {
        return name + " (" + getFormattedMobileNumber() + ")";
    }
}
