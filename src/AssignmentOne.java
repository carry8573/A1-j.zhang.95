import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Entry point of the clinic appointment system: creates the health
 * professionals, books appointments for the current day, and demonstrates
 * viewing, filtering, sorting and cancelling them.
 *
 * <p>PROG2004 Object Oriented Programming - Assessment 1.</p>
 *
 * <p><b>Student:</b> Jiarui Zhang (25259638)<br>
 * <b>Project:</b> A1-25259638<br>
 * <b>GitHub repository:</b> https://github.com/carry8573/A1-25259638
 * (private, tutor and unit assessor invited)<br>
 * <b>Video demonstration:</b> &lt;add the link to your five minute video&gt;</p>
 *
 * <p><b>Generative AI declaration:</b> I acknowledge that I have used Generative
 * AI tools to complete this assessment in accordance with the conditions
 * outlined in the Assessment Brief. I used a large language model to help draft
 * the class design and to review this code.</p>
 *
 * <p>The program is deliberately written using only the language features
 * covered in Modules 1 to 3: classes, encapsulation, inheritance, interfaces,
 * abstract classes, enumerations, {@code ArrayList}, {@code Comparable} and
 * {@code Collections.sort}, exception handling, and the {@code java.time} classes
 * for times.</p>
 *
 * @author  Jiarui Zhang (25259638)
 * @version 1.0
 */
public class AssignmentOne {

    /** The mobile number of the first patient used in the demonstration. */
    private static final String JANE_MOBILE = "0412 345 678";

    /** The mobile number of the second patient used in the demonstration. */
    private static final String MICHAEL_MOBILE = "0423456789";

    /** The mobile number of the third patient used in the demonstration. */
    private static final String AISHA_MOBILE = "0434567890";

    /** The column layout used whenever a list of appointments is displayed. */
    private static final String TABLE_FORMAT = "   %-6s %-6s %-34s %-16s %-13s %s%n";

    /** The rule drawn under the table headings. */
    private static final String TABLE_RULE =
            "   -------------------------------------------------------------------------------------------";

    /**
     * Prevents this class from being instantiated. It holds nothing but the
     * demonstration, which is run through {@link #main(String[])}.
     */
    private AssignmentOne() {
    }

    /**
     * Runs the demonstration.
     *
     * @param args command line arguments, not used
     */
    public static void main(String[] args) {
        // The clinic only takes bookings for the current day, so the day of the
        // week is worked out once from the system date and used throughout.
        WeekDay today = WeekDay.valueOf(LocalDate.now().getDayOfWeek().name());

        printBanner();
        System.out.println("Today is " + today.getDisplayName() + " " + LocalDate.now()
                + ", so every appointment below is for today.");

        List<HealthProfessional> staff = createProfessionals();

        // ---------------------------------------------------------------- Part 3
        printTitle("PART 3 - EVERY PROFESSIONAL, PRINTED WITH ITS FULL DETAILS");
        System.out.println("The clinic employs " + staff.size()
                + " professionals. Each one is printed with its own full details:");
        displayEveryProfessional(staff);

        printTitle("PART 2 AND 3 - EACH TYPE RESPONDS IN ITS OWN WAY (polymorphism)");
        System.out.println("The same loop is used for every object, but each object");
        System.out.println("supplies its own role and its own service:");
        displayServices(staff);

        printTitle("PART 3 - THE ROSTER FOR TODAY");
        displayTodayRoster(staff, today);

        // ---------------------------------------------------------------- Part 6
        printTitle("PART 4 AND 5 - SETTING UP THE APPOINTMENT COLLECTION");
        AppointmentManager manager = new AppointmentManager();
        System.out.println("Clinic timetable: " + manager.getSchedule().describe());
        List<LocalTime> slots = manager.getSchedule().getSlots();
        System.out.println("The " + slots.size() + " slots run from "
                + Schedule.format(slots.get(0)) + " to "
                + Schedule.format(slots.get(slots.size() - 1)) + ".");
        System.out.println("Appointments currently booked: " + manager.size());

        Patient jane = new Patient("Jane Doe", JANE_MOBILE);
        Patient michael = new Patient("Michael Brown", MICHAEL_MOBILE);
        Patient aisha = new Patient("Aisha Khan", AISHA_MOBILE);

        System.out.println();
        System.out.println("Patients registered: " + jane + ", " + michael + ", " + aisha);

        // Only professionals who are rostered today can be booked today, so one
        // of each type is selected from the staff list.
        HealthProfessional gp = firstOnDuty(staff, "General Practitioner", today);
        HealthProfessional specialist = firstOnDuty(staff, "Specialist", today);
        HealthProfessional nurse = firstOnDuty(staff, "Nurse", today);
        HealthProfessional dietitian = firstOnDuty(staff, "Dietitian", today);

        printTitle("PART 6 - BOOKING APPOINTMENTS");
        System.out.println("Each booking below uses a different type of professional:");
        System.out.println();
        System.out.println("   " + manager.addAppointment(
                new Appointment(jane, gp, LocalTime.of(9, 0), "Persistent cough")));
        System.out.println("   " + manager.addAppointment(
                new Appointment(michael, gp, LocalTime.of(9, 30), "Repeat prescription")));
        System.out.println("   " + manager.addAppointment(
                new Appointment(aisha, specialist, LocalTime.of(10, 0), "Heart murmur review")));
        // Same time as the first appointment, but a different professional, which
        // is allowed: a professional can only be in one place at a time, but two
        // professionals can both work at nine o'clock.
        System.out.println("   " + manager.addAppointment(
                new Appointment(jane, nurse, LocalTime.of(9, 0), "Blood test")));
        System.out.println("   " + manager.addAppointment(
                new Appointment(michael, dietitian, LocalTime.of(13, 0), "Type 2 diabetes meal plan")));
        System.out.println("   " + manager.addAppointment(
                new Appointment(aisha, gp, LocalTime.of(14, 30), "Annual health check")));
        System.out.println("   " + manager.addAppointment(
                new Appointment(michael, specialist, LocalTime.of(11, 0), "Follow up consultation")));
        System.out.println();
        System.out.println("Appointments currently booked: " + manager.size());

        printTitle("PART 4 - THE FULL DETAILS OF ONE APPOINTMENT");
        System.out.println("Printing an appointment also prints the details of the chosen");
        System.out.println("professional, whichever type that professional is:");
        System.out.println();
        List<Appointment> everything = manager.getAllAppointments();
        if (!everything.isEmpty()) {
            System.out.println(everything.get(0));
        }

        printTitle("PART 5 - WHEN ARE TWO HEALTH PROFESSIONALS THE SAME?");
        HealthProfessional alice = staff.get(0);
        HealthProfessional aliceCopy = new GeneralPractitioner("1001", "Dr A. Nguyen", "9Z");
        System.out.println("The clinic stores " + alice.getName()
                + " (ID " + alice.getId() + ") and a second object was");
        System.out.println("created with the same ID but a different spelling of the name and a");
        System.out.println("different room. The Java way to compare objects is equals(), so:");
        System.out.println();
        System.out.println("   alice.equals(aliceCopy)                 : "
                + alice.equals(aliceCopy));
        System.out.println("   alice.hashCode() == aliceCopy.hashCode(): "
                + (alice.hashCode() == aliceCopy.hashCode()));
        System.out.println();
        System.out.println("The ID is the clinic's unique key, so equals() and hashCode() compare");
        System.out.println("the ID only. That is also why an appointment can treat two bookings for");
        System.out.println("the same professional at the same time as the same booking.");

        printTitle("PART 5 - ALL APPOINTMENTS, EARLIEST FIRST");
        displayAppointments(manager.getAllAppointments());

        printTitle("PART 5 - FILTERED BY HEALTH PROFESSIONAL ID");
        System.out.println("Appointments for " + gp.getName() + " (ID " + gp.getId() + "):");
        displayAppointments(manager.getAppointmentsForProfessional(gp.getId()));

        printTitle("PART 5 - FILTERED BY PATIENT MOBILE NUMBER");
        System.out.println("Appointments for " + jane.getName() + " ("
                + jane.getFormattedMobileNumber() + "):");
        displayAppointments(manager.getAppointmentsForPatient(jane.getMobileNumber()));

        printTitle("PART 5 - SORTING APPLIED TO A FILTERED SUBSET AND TO EVERYTHING");
        List<Appointment> specialistAppointments =
                manager.getAppointmentsForProfessional(specialist.getId());
        // Both lists are deliberately put into the wrong order first, so that the
        // sort below visibly changes them.
        Collections.reverse(specialistAppointments);
        List<Appointment> wholeDay = manager.getAllAppointments();
        Collections.reverse(wholeDay);
        System.out.println("The appointments for " + specialist.getName()
                + ", filtered out of the collection and sorted:");
        System.out.println("   as built : " + describeTimes(specialistAppointments));
        System.out.println("   sorted   : "
                + describeTimes(manager.sortByTime(specialistAppointments)));
        System.out.println();
        System.out.println("The same call applied to every appointment in the clinic:");
        System.out.println("   as built : " + describeTimes(wholeDay));
        System.out.println("   sorted   : " + describeTimes(manager.sortByTime(wholeDay)));

        printTitle("PART 6 - BOOKING ERROR CASES");
        System.out.println("The manager refuses a booking it cannot make, and explains why:");
        System.out.println();
        System.out.println("   [same professional, same time]");
        System.out.println("   " + manager.addAppointment(
                new Appointment(michael, gp, LocalTime.of(9, 0), "Wants a second opinion")));
        System.out.println();
        System.out.println("   [a time that is not one of the clinic's slots]");
        System.out.println("   " + manager.addAppointment(
                new Appointment(jane, gp, LocalTime.of(9, 15), "Walk in request")));
        System.out.println();
        System.out.println("   [a professional who is not working today]");
        HealthProfessional offDuty = firstOffDuty(staff, today);
        if (offDuty != null) {
            System.out.println("   " + manager.addAppointment(
                    new Appointment(jane, offDuty, LocalTime.of(10, 30), "Wants to be seen today")));
        }
        System.out.println();
        System.out.println("   [no appointment details at all]");
        System.out.println("   " + manager.addAppointment(null));
        System.out.println();
        System.out.println("An appointment that is missing information cannot even be");
        System.out.println("created, so an invalid appointment can never reach the manager:");
        try {
            new Appointment(null, gp, LocalTime.of(10, 30), "No patient supplied");
        } catch (IllegalArgumentException exception) {
            System.out.println("   IllegalArgumentException: " + exception.getMessage());
        }
        try {
            new Patient("Jiarui Zhang", "0412-ABC");
        } catch (IllegalArgumentException exception) {
            System.out.println("   IllegalArgumentException: " + exception.getMessage());
        }

        printTitle("PART 5 - CANCELLING APPOINTMENTS");
        System.out.println("A cancellation that cannot be performed returns a warning:");
        System.out.println();
        System.out.println("   [unknown professional ID]");
        System.out.println("   " + manager.cancelAppointment("9999", LocalTime.of(11, 0)));
        System.out.println();
        System.out.println("   [known professional, but nothing booked at that time]");
        System.out.println("   " + manager.cancelAppointment(gp.getId(), LocalTime.of(12, 0)));
        System.out.println();
        System.out.println("   [unknown patient mobile number]");
        System.out.println("   " + manager.cancelAppointmentsByPatient("0499 999 999"));
        System.out.println();
        System.out.println("A cancellation that can be performed succeeds:");
        System.out.println();
        System.out.println("   [by health professional ID and time]");
        System.out.println("   " + manager.cancelAppointment(gp.getId(), LocalTime.of(9, 30)));
        System.out.println();
        System.out.println("   [by patient mobile number]");
        System.out.println("   " + manager.cancelAppointmentsByPatient(MICHAEL_MOBILE));

        printTitle("PART 6 - THE COLLECTION AFTER THE CANCELLATIONS");
        System.out.println("Appointments currently booked: " + manager.size());
        System.out.println();
        displayAppointments(manager.getAllAppointments());
        System.out.println();
        System.out.println("Cancelling again with the same details now fails, which proves");
        System.out.println("the earlier cancellation really removed the appointment:");
        System.out.println("   " + manager.cancelAppointment(gp.getId(), LocalTime.of(9, 30)));

        printTitle("END OF DEMONSTRATION");
    }

    /**
     * Creates the clinic's health professionals.
     *
     * <p>The roster is arranged so that, on every day of the week, at least one
     * professional of each type is working - a booking is only ever made for the
     * current day, so a type with nobody rostered today could never be booked -
     * while no professional works all seven days, so there is always somebody who
     * cannot be booked.</p>
     *
     * @return the professionals, held as the general type
     */
    private static List<HealthProfessional> createProfessionals() {
        List<HealthProfessional> staff = new ArrayList<>();

        // Three general practitioners (Part 3 asks for three of one type).
        staff.add(new GeneralPractitioner("1001", "Dr Alice Nguyen",
                Arrays.asList(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                        WeekDay.THURSDAY, WeekDay.FRIDAY),
                "3B", true));
        staff.add(new GeneralPractitioner("1002", "Dr Ben Carter",
                Arrays.asList(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY,
                        WeekDay.SATURDAY),
                "1A", true));
        staff.add(new GeneralPractitioner("1003", "Dr Priya Raman",
                Arrays.asList(WeekDay.TUESDAY, WeekDay.THURSDAY, WeekDay.SATURDAY,
                        WeekDay.SUNDAY),
                "5C", false));

        // Two specialists, each in a different field.
        staff.add(new Specialist("2001", "Dr Chen Wei",
                Arrays.asList(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.THURSDAY),
                "Cardiology", 180.00));
        staff.add(new Specialist("2002", "Dr Sofia Rossi",
                Arrays.asList(WeekDay.WEDNESDAY, WeekDay.FRIDAY, WeekDay.SATURDAY,
                        WeekDay.SUNDAY),
                "Paediatrics", 220.00));

        // Two nurses at different levels.
        staff.add(new Nurse("3001", "Nina Patel",
                Arrays.asList(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                        WeekDay.THURSDAY, WeekDay.FRIDAY),
                Nurse.Level.REGISTERED_NURSE, 8));
        staff.add(new Nurse("3002", "Tom Nguyen",
                Arrays.asList(WeekDay.SATURDAY, WeekDay.SUNDAY, WeekDay.MONDAY),
                Nurse.Level.ENROLLED_NURSE, 2));

        // Two dietitians with different areas of focus.
        staff.add(new Dietitian("4001", "Emma Wilson",
                Arrays.asList(WeekDay.TUESDAY, WeekDay.THURSDAY, WeekDay.SATURDAY),
                "Diabetes management", 24));
        staff.add(new Dietitian("4002", "Liam O'Connor",
                Arrays.asList(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY,
                        WeekDay.SUNDAY),
                "Sports nutrition", 18));

        // A fourth general practitioner is created with the shorter constructor,
        // which fills in the sensible defaults: a Monday to Friday roster and bulk
        // billing. That is the overloaded constructor required by Part 1.
        staff.add(new GeneralPractitioner("1004", "Dr Hassan Ali", "2D"));

        return staff;
    }

    /**
     * Prints the full details of every professional.
     *
     * <p>{@code System.out.println(professional)} is used deliberately: it calls
     * the object's own {@code toString}, which is the readable description
     * required by Part 1.</p>
     *
     * @param staff the professionals to print
     */
    private static void displayEveryProfessional(List<HealthProfessional> staff) {
        for (int i = 0; i < staff.size(); i++) {
            System.out.println();
            System.out.println("   -- professional " + (i + 1) + " of " + staff.size() + " --");
            System.out.println(staff.get(i));
        }
    }

    /**
     * Prints what each professional does, without asking what type it is.
     *
     * <p>The loop only knows the general type, so {@code getRole()} and
     * {@code performService()} are resolved by the object itself. The extra
     * abilities are shown separately: prescribing comes from the
     * {@link Prescriber} interface, and the nurse and dietitian abilities are
     * specific to those classes.</p>
     *
     * @param staff the professionals to print
     */
    private static void displayServices(List<HealthProfessional> staff) {
        System.out.println();
        for (HealthProfessional professional : staff) {
            System.out.printf("   ID %-5s %-28s %s%n", professional.getId(),
                    shortRole(professional), professional.performService());
            if (professional instanceof Prescriber) {
                // The cast is safe because the object has just been tested.
                Prescriber prescriber = (Prescriber) professional;
                System.out.println("           -> " + prescriber.prescribe("Amoxicillin 500 mg"));
            } else {
                System.out.println("           -> cannot prescribe medication");
            }
            if (professional instanceof Nurse) {
                Nurse nurse = (Nurse) professional;
                System.out.println("           -> " + nurse.takeBloodSample());
            }
            if (professional instanceof Dietitian) {
                Dietitian dietitian = (Dietitian) professional;
                System.out.println("           -> " + dietitian.createMealPlan());
            }
        }
    }

    /**
     * Prints who is working today and who is not, which explains why only some
     * professionals can be booked.
     *
     * @param staff the professionals to check
     * @param today the current day
     */
    private static void displayTodayRoster(List<HealthProfessional> staff, WeekDay today) {
        System.out.println("On duty on " + today.getDisplayName() + ":");
        for (HealthProfessional professional : staff) {
            if (professional.worksOn(today)) {
                System.out.printf("   ID %-5s %-28s %s%n", professional.getId(),
                        shortRole(professional), professional.getName());
            }
        }
        System.out.println("Not on duty on " + today.getDisplayName() + ":");
        for (HealthProfessional professional : staff) {
            if (!professional.worksOn(today)) {
                System.out.printf("   ID %-5s %-28s %s (works %s)%n", professional.getId(),
                        shortRole(professional), professional.getName(),
                        professional.getWorkingDaysText());
            }
        }
    }

    /**
     * Prints a list of appointments as an aligned table.
     *
     * @param list the appointments to print, already in the required order
     */
    private static void displayAppointments(List<Appointment> list) {
        if (list.isEmpty()) {
            System.out.println("   (no appointments)");
            return;
        }
        System.out.printf(TABLE_FORMAT, "TIME", "ID", "PROFESSIONAL (ROLE)", "PATIENT",
                "MOBILE", "REASON");
        System.out.println(TABLE_RULE);
        for (Appointment appointment : list) {
            System.out.printf(TABLE_FORMAT, appointment.getTimeText(),
                    appointment.getProfessional().getId(),
                    appointment.getProfessional().getName() + " ("
                            + shortRole(appointment.getProfessional()) + ")",
                    appointment.getPatient().getName(),
                    appointment.getPatient().getFormattedMobileNumber(),
                    appointment.getReason());
        }
    }

    /**
     * Describes the times of a list of appointments in one line, for example
     * {@code "09:00, 09:30, 10:00"}, so an ordering is easy to compare.
     *
     * @param list the appointments to describe
     * @return the times separated by commas, or a dash if the list is empty
     */
    private static String describeTimes(List<Appointment> list) {
        if (list.isEmpty()) {
            return "-";
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(list.get(i).getTimeText());
        }
        return text.toString();
    }

    /**
     * Finds the first professional of a given type who is working today.
     *
     * <p>The type is recognised from the start of the role name, so this method
     * works through the general type only and needs no special knowledge of any
     * subclass.</p>
     *
     * @param staff     the professionals to search
     * @param roleStart the beginning of the role name, for example "Nurse"
     * @param today     the current day
     * @return the first matching professional on duty, or {@code null} if there
     *         is none
     */
    private static HealthProfessional firstOnDuty(List<HealthProfessional> staff,
                                                  String roleStart, WeekDay today) {
        for (HealthProfessional professional : staff) {
            if (professional.worksOn(today) && professional.getRole().startsWith(roleStart)) {
                return professional;
            }
        }
        return null;
    }

    /**
     * Finds a professional who is not working today, used to demonstrate that
     * such a professional cannot be booked.
     *
     * @param staff the professionals to search
     * @param today the current day
     * @return the first professional who is off duty, or {@code null} if every
     *         professional works today
     */
    private static HealthProfessional firstOffDuty(List<HealthProfessional> staff, WeekDay today) {
        for (HealthProfessional professional : staff) {
            if (!professional.worksOn(today)) {
                return professional;
            }
        }
        return null;
    }

    /**
     * Returns a short name for the role of a professional, used to keep the
     * appointment table narrow.
     *
     * @param professional the professional
     * @return "GP" for a general practitioner, otherwise the first word of the
     *         role
     */
    private static String shortRole(HealthProfessional professional) {
        String role = professional.getRole();
        if (role.startsWith("General Practitioner")) {
            return "GP";
        }
        int space = role.indexOf(' ');
        return space < 0 ? role : role.substring(0, space);
    }

    /**
     * Prints a banner naming the program.
     */
    private static void printBanner() {
        System.out.println("================================================================");
        System.out.println(" PROG2004 Assessment 1 - Clinic Appointment System");
        System.out.println(" Jiarui Zhang (25259638)");
        System.out.println("================================================================");
        System.out.println();
    }

    /**
     * Prints a heading before each part of the demonstration.
     *
     * @param title the heading text
     */
    private static void printTitle(String title) {
        System.out.println();
        System.out.println("================================================================");
        System.out.println(" " + title);
        System.out.println("================================================================");
        System.out.println();
    }
}
