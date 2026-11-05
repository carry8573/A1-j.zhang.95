import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The fixed set of appointment slots the clinic offers in one day.
 *
 * <p>The clinic runs from 08:00 to 16:00 in thirty minute intervals, but those
 * numbers are not hard coded into the booking logic. They are defaults passed to
 * a constructor, and the slots themselves are generated from a start time, an
 * end time and a slot length. If the clinic later opens at 07:00 or moves to
 * twenty minute slots, only the values given to this class change - the
 * appointment and manager classes are untouched. That is what Part 5 means by
 * representing the fixed slot set "flexibly".</p>
 *
 * <p>PROG2004 - Assessment 1, Part 5.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class Schedule {

    /** The default time the clinic opens, 08:00. */
    public static final LocalTime DEFAULT_START = LocalTime.of(8, 0);

    /** The default time the clinic closes, 16:00. */
    public static final LocalTime DEFAULT_END = LocalTime.of(16, 0);

    /** The default length of one appointment, in minutes. */
    public static final int DEFAULT_SLOT_MINUTES = 30;

    /**
     * The format used everywhere in the program to show a time of day, for
     * example {@code "09:30"}. Keeping one formatter avoids each class choosing
     * a slightly different style.
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /** The time of the first slot. */
    private final LocalTime startTime;

    /** The time the clinic closes; not itself a slot start. */
    private final LocalTime endTime;

    /** The length of one slot in minutes. */
    private final int slotMinutes;

    /** The slot start times, earliest first. */
    private final List<LocalTime> slots;

    /**
     * Creates the clinic's default schedule: 08:00 to 16:00 in thirty minute
     * slots.
     */
    public Schedule() {
        this(DEFAULT_START, DEFAULT_END, DEFAULT_SLOT_MINUTES);
    }

    /**
     * Creates a schedule from a start time, an end time and a slot length.
     *
     * @param startTime   the time of the first slot; must not be null
     * @param endTime     the time the clinic closes; must not be null and must
     *                    be later than the start
     * @param slotMinutes the length of one slot in minutes; must be positive and
     *                    must divide the opening period exactly
     * @throws IllegalArgumentException if the arguments do not describe a
     *                                  usable timetable
     */
    public Schedule(LocalTime startTime, LocalTime endTime, int slotMinutes) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("The start and end of the day must both be given.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("The clinic must close after it opens.");
        }
        if (slotMinutes <= 0) {
            throw new IllegalArgumentException("The length of a slot must be greater than zero.");
        }
        long openingMinutes = Duration.between(startTime, endTime).toMinutes();
        if (openingMinutes % slotMinutes != 0) {
            throw new IllegalArgumentException("A " + slotMinutes
                    + " minute slot does not divide the opening period exactly.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotMinutes = slotMinutes;
        this.slots = buildSlots();
    }

    /**
     * Generates the slot start times.
     *
     * <p>The loop stops before the closing time, so a clinic open from 08:00 to
     * 16:00 offers 08:00 up to 15:30 - a slot that started at 16:00 would run
     * after the clinic had closed.</p>
     *
     * @return an unmodifiable list of slot start times
     */
    private List<LocalTime> buildSlots() {
        List<LocalTime> generated = new ArrayList<>();
        LocalTime slot = startTime;
        while (slot.isBefore(endTime)) {
            generated.add(slot);
            slot = slot.plusMinutes(slotMinutes);
        }
        return Collections.unmodifiableList(generated);
    }

    /**
     * Returns the time of the first slot.
     *
     * @return the opening time
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the time the clinic closes.
     *
     * @return the closing time
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the length of one slot.
     *
     * @return the slot length in minutes
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * Returns every slot start time, earliest first.
     *
     * @return an unmodifiable list of slot start times
     */
    public List<LocalTime> getSlots() {
        return slots;
    }

    /**
     * Returns the number of appointments the clinic can run in one day for one
     * professional.
     *
     * @return the number of slots
     */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Reports whether a time is one of the clinic's slots.
     *
     * <p>Only the exact start times of slots are valid, so 09:00 is accepted and
     * 09:15 is refused. A time carrying seconds is also refused because it will
     * not equal any generated slot.</p>
     *
     * @param time the time to test; may be null
     * @return {@code true} if the time is a slot start
     */
    public boolean isValidSlot(LocalTime time) {
        return time != null && slots.contains(time);
    }

    /**
     * Formats a time of day using the shared clinic format.
     *
     * @param time the time to format; may be null
     * @return the time as {@code "HH:mm"}, or {@code "-"} if the time is null
     */
    public static String format(LocalTime time) {
        return time == null ? "-" : time.format(TIME_FORMATTER);
    }

    /**
     * Describes the timetable in one line, for example
     * {@code "08:00-16:00, 30 minute slots (16 slots per professional)"}.
     *
     * @return the description
     */
    public String describe() {
        return format(startTime) + "-" + format(endTime) + ", " + slotMinutes
                + " minute slots (" + slots.size() + " slots per professional)";
    }
}
