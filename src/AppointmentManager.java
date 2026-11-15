import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The single place where every appointment in the clinic is stored and managed.
 *
 * <p>The collection chosen is an {@link ArrayList} of {@link Appointment}. A list
 * is the right structure here because the clinic holds a small number of
 * appointments, because appointments must be kept in a defined order (the list is
 * sorted by time), because the same patient or the same professional legitimately
 * appears many times, and because the program needs to look at all of the
 * appointments for one professional or one patient - a full scan of a short list
 * is cheap. A {@code HashMap} keyed by time would find one booking faster, but it
 * would lose the ordering and make "show me everything" harder, and speed is not
 * a problem at this size.</p>
 *
 * <p>Every operation that can fail returns a message instead of throwing, so the
 * program always tells the user what happened: a booking is either accepted or
 * refused with the reason, and the same is true of a cancellation. That is the
 * "warning message when the action cannot be performed" required by Part 5.</p>
 *
 * <p>The manager, not the appointment, is responsible for the clinic's rules: the
 * time must be one of the timetable's slots, the professional must be rostered
 * for today, and a professional must never be booked twice into the same slot.</p>
 *
 * <p>The manager is deliberately not tied to one set of opening hours or to the
 * machine clock. Both are supplied through constructors: the timetable is a
 * {@link Schedule} object, and the day the roster is checked against can be
 * fixed instead of being read from the system date. Adding a second clinic, an
 * evening clinic, or a test that has to run on a known day therefore needs no
 * change to this class - only different objects handed to it. The manager can
 * also report which slots are still free for a professional, so a receptionist
 * can be told what is available instead of only being told what is refused.</p>
 *
 * <p>PROG2004 - Assessment 1, Part 5.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class AppointmentManager {

    /** Every appointment currently booked, in insertion order. */
    private final List<Appointment> appointments;

    /** The timetable that defines which times may be booked. */
    private final Schedule schedule;

    /**
     * The day this manager treats as today, or {@code null} to follow the system
     * clock. Fixing the day keeps the roster rule usable away from the machine's
     * own date, for example when planning a later day or when demonstrating the
     * rule on a known day of the week.
     */
    private final WeekDay workingDay;

    /**
     * Creates a manager for the clinic's default timetable, checking the roster
     * against the real current day.
     */
    public AppointmentManager() {
        this(new Schedule(), null);
    }

    /**
     * Creates a manager for a given timetable, so a clinic with different
     * opening hours can be supported without changing this class.
     *
     * @param schedule the timetable; must not be null
     * @throws IllegalArgumentException if the schedule is null
     */
    public AppointmentManager(Schedule schedule) {
        this(schedule, null);
    }

    /**
     * Creates a manager for a given timetable and a given day.
     *
     * <p>Passing a day makes the manager behave as if that day were today, so the
     * same roster rule applies to a day that is not the machine's own date.
     * Passing {@code null} means "use the real current day", which is what the
     * two shorter constructors do.</p>
     *
     * @param schedule   the timetable; must not be null
     * @param workingDay the day to treat as today, or {@code null} for the real
     *                   current day
     * @throws IllegalArgumentException if the schedule is null
     */
    public AppointmentManager(Schedule schedule, WeekDay workingDay) {
        if (schedule == null) {
            throw new IllegalArgumentException("An appointment manager needs a schedule.");
        }
        this.schedule = schedule;
        this.workingDay = workingDay;
        this.appointments = new ArrayList<>();
    }

    /**
     * Returns the timetable used by this manager.
     *
     * @return the schedule
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Returns the number of appointments currently booked.
     *
     * @return the number of appointments
     */
    public int size() {
        return appointments.size();
    }

    /**
     * Returns the slots a professional could still be booked into on this
     * manager's day, earliest first.
     *
     * <p>This is the flexible side of the rules: instead of only refusing a
     * booking, the manager can also say what is available. The result takes the
     * roster into account, so a professional who is not working that day has no
     * free slots at all, and the slots already taken by that professional are
     * left out. Other professionals' bookings do not matter, because two
     * professionals can work at the same time.</p>
     *
     * @param professional the professional to check; may be null
     * @return a new list of free slot start times, empty if there are none
     */
    public List<LocalTime> getFreeSlots(HealthProfessional professional) {
        List<LocalTime> free = new ArrayList<>();
        if (professional == null || !professional.worksOn(getWorkingDay())) {
            return free;
        }
        for (LocalTime slot : schedule.getSlots()) {
            if (!isBooked(professional, slot)) {
                free.add(slot);
            }
        }
        return free;
    }

    /**
     * Reports whether one professional already has an appointment at one time.
     *
     * <p>The professional is compared with {@code equals}, which the clinic
     * defines by ID, so two objects describing the same professional count as the
     * same person here.</p>
     *
     * @param professional the professional
     * @param time         the time to test
     * @return {@code true} if that professional is already booked then
     */
    private boolean isBooked(HealthProfessional professional, LocalTime time) {
        for (Appointment appointment : appointments) {
            if (appointment.getProfessional().equals(professional)
                    && appointment.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the day this manager treats as today, so the roster can be checked.
     *
     * <p>Reading the system clock is only the default. A manager created with a
     * fixed day always answers with that day, and every roster check goes through
     * this one method, so the two behaviours cannot drift apart.</p>
     *
     * @return the day to check rosters against
     */
    public WeekDay getWorkingDay() {
        return workingDay != null
                ? workingDay
                : WeekDay.valueOf(LocalDate.now().getDayOfWeek().name());
    }

    /**
     * Tries to book an appointment.
     *
     * <p>The checks run in the order a receptionist would make them: is there an
     * appointment at all, is it for the day this manager is running, is the time a
     * real slot, is the professional working that day, and is that professional
     * already busy at that time.</p>
     *
     * @param appointment the appointment to book; may be null
     * @return a message saying whether the booking was accepted, and why not if
     *         it was refused
     */
    public String addAppointment(Appointment appointment) {
        if (appointment == null) {
            return "BOOKING REJECTED: no appointment details were supplied.";
        }
        HealthProfessional professional = appointment.getProfessional();
        // The manager is only responsible for one day, so an appointment written
        // for another day belongs to a different manager's collection.
        if (!appointment.getDay().equals(getWorkingDay())) {
            return "BOOKING REJECTED: this appointment is for "
                    + appointment.getDay().getDisplayName() + ", but this clinic is managing "
                    + getWorkingDay().getDisplayName() + ".";
        }
        if (!schedule.isValidSlot(appointment.getTime())) {
            return "BOOKING REJECTED: " + appointment.getTimeText()
                    + " is not a clinic slot. The clinic runs " + schedule.describe() + ".";
        }
        if (!professional.worksOn(getWorkingDay())) {
            return "BOOKING REJECTED: " + professional.getName() + " (" + professional.getRole()
                    + ", ID " + professional.getId() + ") does not work on "
                    + getWorkingDay().getDisplayName() + "; the roster is "
                    + professional.getWorkingDaysText() + ".";
        }
        // equals on Appointment compares the day, the professional and the time,
        // so the collection itself can find a clash without a hand written loop.
        if (appointments.contains(appointment)) {
            return "BOOKING REJECTED: " + professional.getName() + " (ID " + professional.getId()
                    + ") is already booked at " + appointment.getTimeText() + ".";
        }
        appointments.add(appointment);
        return "BOOKED: " + appointment.getTimeText() + " - " + appointment.getPatient().getName()
                + " (" + appointment.getPatient().getFormattedMobileNumber() + ") with "
                + professional.getName() + " (" + professional.getRole() + ", ID "
                + professional.getId() + ").";
    }

    /**
     * Cancels the appointment for one professional at one time.
     *
     * @param professionalId the identifier of the professional; digits only
     * @param time           the time of the appointment
     * @return a message saying whether the appointment was cancelled, and why not
     *         if it was not
     */
    public String cancelAppointment(String professionalId, LocalTime time) {
        String id = professionalId == null ? "" : professionalId.trim();
        if (id.isEmpty() || time == null) {
            return "CANCELLATION FAILED: both a health professional ID and a time are required.";
        }
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (appointment.getProfessional().getId().equals(id)
                    && appointment.getTime().equals(time)) {
                appointments.remove(i);
                return "CANCELLED: the " + appointment.getTimeText() + " appointment with "
                        + appointment.getProfessional().getName() + " (ID " + id + ") for "
                        + appointment.getPatient().getName() + " has been removed.";
            }
        }
        return "CANCELLATION FAILED: no appointment was found for health professional ID " + id
                + " at " + Schedule.format(time) + ".";
    }

    /**
     * Cancels every appointment belonging to one patient, found by mobile number.
     *
     * @param mobileNumber the patient's mobile number
     * @return a message saying how many appointments were cancelled, or why none
     *         were
     */
    public String cancelAppointmentsByPatient(String mobileNumber) {
        String number = mobileNumber == null ? "" : mobileNumber.trim();
        if (number.isEmpty()) {
            return "CANCELLATION FAILED: a patient mobile number is required.";
        }
        List<Appointment> removed = new ArrayList<>();
        // The list is walked backwards so that removing an element does not shift
        // the positions of the elements still to be checked.
        for (int i = appointments.size() - 1; i >= 0; i--) {
            Appointment appointment = appointments.get(i);
            if (appointment.getPatient().getMobileNumber().equals(number)) {
                removed.add(appointment);
                appointments.remove(i);
            }
        }
        if (removed.isEmpty()) {
            return "CANCELLATION FAILED: no appointments were found for mobile number "
                    + number + ".";
        }
        Appointment first = removed.get(0);
        return "CANCELLED: " + removed.size() + " appointment(s) for "
                + first.getPatient().getName() + " ("
                + first.getPatient().getFormattedMobileNumber() + ") have been removed.";
    }

    /**
     * Returns every appointment, sorted earliest to latest.
     *
     * <p>A sorted copy is returned rather than the internal list so that a caller
     * cannot add or remove appointments behind the manager's back and break its
     * rules.</p>
     *
     * @return a new list of all appointments in time order
     */
    public List<Appointment> getAllAppointments() {
        return sortByTime(appointments);
    }

    /**
     * Returns the appointments for one health professional, sorted earliest to
     * latest.
     *
     * @param professionalId the identifier of the professional
     * @return a new list of that professional's appointments in time order
     */
    public List<Appointment> getAppointmentsForProfessional(String professionalId) {
        String id = professionalId == null ? "" : professionalId.trim();
        List<Appointment> found = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getProfessional().getId().equals(id)) {
                found.add(appointment);
            }
        }
        return sortByTime(found);
    }

    /**
     * Returns the appointments for one patient, found by mobile number and sorted
     * earliest to latest.
     *
     * @param mobileNumber the patient's mobile number
     * @return a new list of that patient's appointments in time order
     */
    public List<Appointment> getAppointmentsForPatient(String mobileNumber) {
        String number = mobileNumber == null ? "" : mobileNumber.trim();
        List<Appointment> found = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getMobileNumber().equals(number)) {
                found.add(appointment);
            }
        }
        return sortByTime(found);
    }

    /**
     * Returns a sorted copy of any list of appointments, so the same ordering can
     * be applied to the whole clinic or to a filtered subset.
     *
     * @param list the appointments to sort; must not be null
     * @return a new list in time order
     * @throws IllegalArgumentException if the list is null
     */
    public List<Appointment> sortByTime(List<Appointment> list) {
        if (list == null) {
            throw new IllegalArgumentException("A list of appointments is required.");
        }
        List<Appointment> sorted = new ArrayList<>(list);
        // Collections.sort uses Appointment's own compareTo, so the ordering rule
        // lives in one place - the class being ordered.
        Collections.sort(sorted);
        return sorted;
    }
}
