import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One booking: a patient, the health professional they will see, and the time of
 * the appointment.
 *
 * <p>An appointment is created only through this constructor and every field is
 * {@code final}, so an appointment cannot exist in an incomplete state. Passing a
 * missing patient, a missing professional, a missing time or a blank reason is
 * rejected immediately with an {@link IllegalArgumentException} rather than
 * producing a half filled object that fails later somewhere else.</p>
 *
 * <p>Two rules are deliberately <em>not</em> checked here: whether the time is a
 * real clinic slot, and whether the professional is already busy. Those rules
 * depend on the clinic's timetable and on all the other bookings, so they belong
 * to {@link AppointmentManager}. Keeping them there means an {@code Appointment}
 * stays a simple, always valid record of "who, with whom, when", and the manager
 * remains the single place that decides whether a booking can be accepted.</p>
 *
 * <p>The day of the appointment is stored as a {@link WeekDay}, not as a full
 * calendar date. The clinic books the current week, so a full date would carry
 * detail the clinic never uses, while the day of the week is exactly what the
 * roster is written in terms of. Storing the day also means an appointment can
 * state its own day and stay correct when a manager is working with a day other
 * than the machine's own - the booking and the roster keep using the same
 * vocabulary.</p>
 *
 * <p>The class implements {@link Comparable} so that a list of appointments can be
 * sorted earliest to latest by time, which is the ordering required by Parts 4
 * and 5.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 4.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    /** The reason used when a caller does not supply one. */
    private static final String DEFAULT_REASON = "General consultation";

    /** The patient who made the booking. */
    private final Patient patient;

    /** The health professional the patient will see. */
    private final HealthProfessional professional;

    /** The time of the appointment on the day it belongs to. */
    private final LocalTime time;

    /** The day of the week the appointment belongs to. */
    private final WeekDay day;

    /** Why the patient is attending. */
    private final String reason;

    /**
     * Creates an appointment for the current day with an explicit reason.
     *
     * @param patient      the patient; must not be null
     * @param professional the health professional; must not be null and any type
     *                     of professional is accepted
     * @param time         the time of the appointment; must not be null
     * @param reason       why the patient is attending; must not be blank
     * @throws IllegalArgumentException if any argument is missing or blank
     */
    public Appointment(Patient patient, HealthProfessional professional,
                       LocalTime time, String reason) {
        this(patient, professional, time, reason,
                WeekDay.valueOf(LocalDate.now().getDayOfWeek().name()));
    }

    /**
     * Creates an appointment for a given day with an explicit reason.
     *
     * <p>The day is only needed when the booking is not for the machine's own
     * date, for example when the demonstration runs the clinic on a chosen day.
     * The four argument constructor above covers the normal case, so callers do
     * not have to think about a day at all.</p>
     *
     * @param patient      the patient; must not be null
     * @param professional the health professional; must not be null
     * @param time         the time of the appointment; must not be null
     * @param reason       why the patient is attending; must not be blank
     * @param day          the day of the week the appointment is for; must not be
     *                     null
     * @throws IllegalArgumentException if any argument is missing or blank
     */
    public Appointment(Patient patient, HealthProfessional professional,
                       LocalTime time, String reason, WeekDay day) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment must have a patient.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment must have a health professional.");
        }
        if (time == null) {
            throw new IllegalArgumentException("An appointment must have a time.");
        }
        if (day == null) {
            throw new IllegalArgumentException("An appointment must have a day.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("An appointment must have a reason.");
        }
        this.patient = patient;
        this.professional = professional;
        this.time = time;
        this.day = day;
        this.reason = reason.trim();
    }

    /**
     * Creates an appointment for a general consultation.
     *
     * @param patient      the patient; must not be null
     * @param professional the health professional; must not be null
     * @param time         the time of the appointment; must not be null
     * @throws IllegalArgumentException if any argument is missing
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        this(patient, professional, time, DEFAULT_REASON);
    }

    /**
     * Returns the patient.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the health professional.
     *
     * @return the professional, as the general type so any specialisation works
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Returns the time of the appointment.
     *
     * @return the time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns the reason for the appointment.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the time of the appointment as text, for example {@code "09:30"}.
     *
     * @return the formatted time
     */
    public String getTimeText() {
        return Schedule.format(time);
    }

    /**
     * Returns the day of the week this appointment falls on.
     *
     * <p>This is the day the appointment was created for, not the day the program
     * happens to be running, so a printed appointment can never show a day that
     * disagrees with the booking.</p>
     *
     * @return the day of the appointment
     */
    public WeekDay getDay() {
        return day;
    }

    /**
     * Orders appointments by day and then by time, earliest first.
     *
     * <p>Two appointments can share a time when they are with different
     * professionals, so the professional's identifier breaks that tie. That is
     * allowed by the brief, which only requires that the ordering by time is
     * correct; the extra tie break simply keeps the order stable and repeatable.</p>
     *
     * @param other the appointment to compare with
     * @return a negative number if this appointment is earlier, zero if the same
     *         day, time and professional, a positive number otherwise
     */
    @Override
    public int compareTo(Appointment other) {
        int byDay = day.compareTo(other.day);
        if (byDay != 0) {
            return byDay;
        }
        int byTime = time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        return professional.getId().compareTo(other.professional.getId());
    }

    /**
     * Compares this appointment with another object.
     *
     * <p>An appointment is identified by the professional and the time: a
     * professional can be in one place at one time, so a second appointment with
     * the same professional at the same time is the same booking. Expressing that
     * in {@code equals} lets the manager detect a double booking with the
     * collection's own {@code contains} method instead of writing a loop of
     * comparisons.</p>
     *
     * @param other the object to compare with
     * @return {@code true} if both appointments are for the same professional at
     *         the same time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Appointment)) {
            return false;
        }
        Appointment that = (Appointment) other;
        return day.equals(that.day)
                && professional.equals(that.professional)
                && time.equals(that.time);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * <p>The same three values are combined in the same order, so two appointments
     * that {@code equals} accepts always produce the same hash code.</p>
     *
     * @return a hash code built from the day, the professional and the time
     */
    @Override
    public int hashCode() {
        return (day.hashCode() * 31 + professional.hashCode()) * 31 + time.hashCode();
    }

    /**
     * Returns the full details of the appointment, including the details of the
     * health professional, so that printing one appointment shows everything a
     * receptionist needs.
     *
     * @return the multi line description of the appointment
     */
    @Override
    public String toString() {
        List<String> lines = new ArrayList<>();
        lines.add("Appointment " + getTimeText() + " on " + getDay().getDisplayName());
        lines.add(fieldLine("Patient", patient.getName() + " ("
                + patient.getFormattedMobileNumber() + ")"));
        lines.add(fieldLine("Reason", reason));
        // The professional's own description is reused and indented, so an
        // appointment never has to know the fields of a general practitioner,
        // a nurse or any other type.
        List<String> professionalLines = professional.getDescriptionLines();
        lines.add(fieldLine("Professional", professionalLines.get(0)));
        for (int i = 1; i < professionalLines.size(); i++) {
            lines.add("   " + professionalLines.get(i));
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                text.append(System.lineSeparator());
            }
            text.append(lines.get(i));
        }
        return text.toString();
    }

    /**
     * Formats one labelled line of this appointment's description.
     *
     * @param label the label, for example {@code "Patient"}
     * @param value the value
     * @return the formatted line
     */
    private String fieldLine(String label, String value) {
        return String.format("   %-13s: %s", label, value);
    }
}
