# PROG2004 Assessment 1 - Clinic Appointment System

**Student:** Jiarui Zhang (25259638)
**Unit:** PROG2004 Object Oriented Programming, Southern Cross University
**Assessment:** Assessment 1, 40%, individual

A console application that lets a patient book an appointment with a health
professional at a small health clinic. Appointments are always for the current
day, and the clinic runs a fixed timetable of 30 minute slots from 08:00 to
16:00.

There is no menu, no user interface, no login and no file storage: the
assessment only asks for the object model and for `AssignmentOne.main` to
exercise it.

---

## 1. How to compile and run

The project was written and tested with **JDK 21**. It uses no libraries, so any
JDK from 8 upwards will compile it.

```bash
cd A1-25259638
javac -d out src/*.java
java -cp out AssignmentOne
```

In an IDE, open the folder as a Java project and run the `AssignmentOne` class
(the class that contains `main`).

All classes are deliberately kept in the **default package**, directly in `src`,
because the assessment brief asks for the class `AssignmentOne` to be created
in the `src` directory.

---

## 2. Project structure

| File | Part | Responsibility |
| --- | --- | --- |
| `src/AssignmentOne.java` | 3, 4, 5, 6 | `main`; builds the clinic, runs all six demonstrations and prints them |
| `src/WeekDay.java` | 1 | `enum` of Monday to Sunday, with a readable display name |
| `src/Prescriber.java` | 2 | `interface`: the capability "can prescribe medication" |
| `src/HealthProfessional.java` | 1 | `abstract` superclass: ID, name, working days, validation, `toString()` |
| `src/GeneralPractitioner.java` | 2 | A doctor who sees general patients in a room; can prescribe |
| `src/Specialist.java` | 2 | A doctor with a specialty and a consultation fee; can prescribe |
| `src/Nurse.java` | 2 | A nurse with a level and experience; can take a blood sample |
| `src/Dietitian.java` | 2 | A dietitian with a focus area and a weekly patient load |
| `src/Patient.java` | 4 | A patient: name plus a validated 10 digit mobile number |
| `src/Schedule.java` | 5 | The clinic's fixed slot set, built from a start, an end and a slot length |
| `src/Appointment.java` | 4 | One patient, one professional, one time; `Comparable` and `equals` |
| `src/AppointmentManager.java` | 5 | The single collection of appointments: add, view, filter, sort, cancel |

Extra files (not needed to compile):

| File | Purpose |
| --- | --- |
| `DESIGN.md` | the written justification of the design, mapped to the six parts |
| `VIDEO_SCRIPT.md` | the script used for the four pillars video |
| `SAMPLE_OUTPUT.txt` | a full run of the program, for reference |

---

## 3. The design in one paragraph

`HealthProfessional` is an **abstract class** that owns everything every
professional has - a numeric ID, a name and the set of weekdays they work - and
it validates all of that in its constructor, so an invalid professional can
never exist. It declares three abstract methods (`getRole()`,
`performService()`, `addRoleDetails(List<String>)`) so a "plain" professional
cannot be instantiated and every subclass must say what it is and what it does.
The four concrete subclasses add only their own state. The ability to prescribe
is modelled separately as an **interface**, `Prescriber`, because it cuts across
the hierarchy: general practitioners and specialists have it, nurses and
dietitians do not, and the clinic can still ask any professional "can you
prescribe?" without knowing its exact class. `Patient`, `Schedule`,
`Appointment` and `AppointmentManager` then build the booking side on top,
keeping the "is this booking legal?" rules in the manager and the "these are the
clinic's slots" rules in `Schedule`, so each class has one reason to change.

---

## 4. How each part of the brief is covered

### Part 1 - one class for a generic health professional

- `HealthProfessional` holds `id` (numbers only), `name` and
  `workingDays` (a `List<WeekDay>` stored as an unmodifiable copy, so the
  roster cannot be edited from outside).
- Validation is centralised in three private static helpers and runs in the
  constructor:
  - `validateId`: loops over the characters and accepts `'0'` to `'9'` only;
  - `validateName`: rejects null and blank names;
  - `validateWorkingDays`: rejects null or empty rosters, removes duplicates
    and wraps the result in `Collections.unmodifiableList`.
  Every failure throws `IllegalArgumentException` with a message that says what
  was wrong and what was received.
- Controlled access: all fields are `private final`, and the only way in is the
  constructor. There are no setters, so a professional cannot be put into an
  invalid state after creation.
- Readable output: `toString()` returns `getDescription()`, which prints the
  role, the ID, the name, the roster and then the subclass's own lines, so
  `System.out.println(doctorA)` already shows everything a receptionist needs.
- Overloaded constructors: `(id, name, workingDays)` and `(id, name)`, where the
  second one defaults to a Monday to Friday roster (see
  `protected static final List<WeekDay> WEEKDAYS`). The subclasses follow the
  same pattern, for example `GeneralPractitioner(id, name, roomNumber)` and
  `Specialist(id, name, specialty)`.

### Part 2 - at least four specific types, one of them a GP

| Type | Unique state | Unique behaviour |
| --- | --- | --- |
| `GeneralPractitioner` | room number, bulk billing flag | gives general consultations; `prescribe(...)` |
| `Specialist` | specialty, consultation fee | treats patients in that specialty clinic; `prescribe(...)` |
| `Nurse` | level (`Enrolled`, `Registered`, `Nurse Practitioner`), years of experience | `takeBloodSample()` |
| `Dietitian` | focus area, weekly capacity | `createMealPlan()` |

- Reuse: every shared field and every validation rule lives once, in
  `HealthProfessional`; the subclasses call `super(...)` and add only their own
  state.
- The type is always visible: `getRole()` returns `"General Practitioner"`,
  `"Specialist - Cardiology"`, `"Nurse"` or `"Dietitian"`, and the description
  prints the level, the specialty and so on underneath.
- Behaviour through the general type: `AssignmentOne` stores all ten
  professionals in one `List<HealthProfessional>` and calls `getRole()` and
  `performService()` on the superclass reference - each object answers in its
  own way.
- Not instantiable: `HealthProfessional` is `abstract`, and so is the meaning of
  `getRole()`. Java will not compile `new HealthProfessional(...)`, which is
  exactly the brief's requirement.
- Prescribing is an interface, not a superclass method, because not every
  professional may prescribe. `AssignmentOne` demonstrates it with
  `instanceof Prescriber` followed by a cast, and prints
  `"cannot prescribe medication"` for the nurse and the dietitian.

### Part 3 - create and display the objects

`AssignmentOne.createProfessionals()` builds ten objects - four general
practitioners (three with the long constructor, one with the short one to show
the defaults), two specialists, two nurses and two dietitians - and
`displayEveryProfessional()` prints each one through one loop and one
`println(professional)` call. A second loop prints the role and the
`performService()` text so the polymorphism is visible.

### Part 4 - the appointment

- `Patient` is a class of its own because a patient exists whether or not there
  is an appointment, and because the mobile number has its own validation and a
  formatted form. Equality is by mobile number, which is the clinic's way of
  identifying a patient.
- `Appointment` keeps `patient`, `professional`, `time` and a `reason`. The
  constructor rejects a null patient, a null professional, a null time and a
  null or blank reason, so an incomplete appointment cannot be created. It also
  validates the slot and the roster in `AppointmentManager`, immediately, so a
  rejected booking is never stored either.
- Printing an appointment prints the professional's full description, so the
  appointment always shows who it is with. `toString()` is used for the detailed
  view and `toSummaryString()` for table rows.
- Ordering: the time is stored as a `LocalTime`, not as a `String`, so ordering
  is chronological rather than alphabetical (a `String` would sort `"14:30"`
  before `"9:00"`). `Appointment implements Comparable<Appointment>` and
  `compareTo` compares the time first, then the professional's ID so that two
  appointments at the same time still have a stable order.

### Part 5 - managing all appointments in one place

- Collection: `ArrayList<Appointment>` inside `AppointmentManager`. It was
  chosen over an array because the number of bookings is not known ahead of
  time and bookings are added and cancelled during the day; over a `HashMap`
  because appointments are looked up by filtering on two different keys
  (professional ID and patient mobile) and printed in time order, which suits a
  list; and over a `TreeSet` because the manager needs insertions in arrival
  order and explicit control over when sorting happens.
- Adding: `addAppointment` checks, in order, for a null appointment, a time that
  is not a clinic slot, a professional who is not rostered today, and a
  double-booking. Each case returns a `"BOOKING REJECTED: ..."` message.
- Double bookings: because `Appointment.equals` and `hashCode` are defined by
  professional plus time, the whole check is one call,
  `appointments.contains(appointment)`.
- Viewing: `getAllAppointments()`, `getAppointmentsForProfessional(id)` and
  `getAppointmentsForPatient(mobile)` all return sorted copies, so printing
  them always shows earliest to latest and the caller cannot damage the
  manager's list.
- Sorting: `sortByTime(list)` uses `Collections.sort`, which relies on
  `Comparable`. It works on the whole collection and on any filtered subset,
  which is exactly what the brief asks for.
- Cancelling: `cancelAppointment(professionalId, time)` and
  `cancelAppointmentsByPatient(mobile)` return `"CANCELLED: ..."` on success or
  `"CANCELLATION FAILED: ..."` with the reason on failure. Nothing is silent.
- The fixed slot set is not hard-coded in the booking code: `Schedule` builds
  its slots from a start time, an end time and a slot length, so changing the
  clinic's hours or switching to 20 minute slots means changing one default, not
  the manager.
- Equality of professionals: the Java way is `equals`/`hashCode`, implemented
  once in `HealthProfessional` on the ID, which is the clinic's unique key.
  `AssignmentOne` shows it with two objects that have the same ID but different
  names and rooms.

### Part 6 - the end to end demonstration

`AssignmentOne.main` runs the whole story in order, with printed section titles:

1. the professionals and each type's own behaviour;
2. the roster for today, on duty and off duty;
3. the timetable and the three patients;
4. seven bookings across all four professional types;
5. the full details of one appointment;
6. the `equals` demonstration;
7. every appointment, earliest first;
8. the same list filtered by professional ID, then by patient mobile;
9. sorting applied to a filtered subset and to the whole collection;
10. the booking error cases: double booking, a time that is not a slot, an
    off duty professional, no details at all, and the two constructor
    exceptions;
11. the cancellation error cases, then two successful cancellations;
12. the collection after the cancellations, and a repeated cancellation that now
    fails - which proves the first one really removed the appointment.

A full captured run is in `SAMPLE_OUTPUT.txt`.

---

## 5. Design decisions and assumptions

1. **Bookings are same-day only.** The brief says so, so `Appointment` stores a
   `LocalTime` and the day is today. `AppointmentManager` refuses a booking for
   a professional who is not rostered on today's weekday.
2. **Slots are half open.** With 08:00 to 16:00 and 30 minute slots the clinic
   has 16 start times, 08:00 to 15:30: the 15:30 appointment finishes at 16:00,
   when the clinic closes, so 16:00 is the end of the day, not a slot.
3. **One professional is never double booked for the same slot.** Two different
   professionals may both be booked at 09:00; the same professional may not.
4. **`equals` compares IDs, not all fields.** Two objects with the same ID are
   the same professional even if a name was spelled differently or a room
   changed, which is what a database key would do.
5. **The manager returns messages instead of throwing.** A failed booking is an
   expected, everyday event at a reception desk, so `addAppointment` and the
   cancel methods return a `String` the caller can print. The constructors throw
   `IllegalArgumentException` for programming errors, because an appointment
   with no patient is a bug, not a business rule.
6. **Only first and second year concepts are used.** No lambdas, no streams, no
   `record`, no `var`, no `Optional`, no regular expressions, no external
   libraries. Validation uses plain character loops; the only classes used are
   `String`, the wrapper types, `List`/`ArrayList`, `Collections`, `LocalTime`,
   `LocalDate`, `Duration`, `DateTimeFormatter` and `String.format`.
7. **The demonstration writes to `System.out` only.** No file output, no
   `Scanner`, no user input, as the brief requires.

---

## 6. Generative AI declaration

> I acknowledge that I have used Generative AI tools to complete this assessment
> in accordance with the conditions outlined in the Assessment Brief. I used
> ChatGPT to help me plan the class design, to review my code for style and
> Javadoc comments, and to suggest the wording of the comments and of this
> README.

*(Edit this paragraph so that it matches what you actually used.)*

---

## 7. Links

- GitHub repository: **<add the repository URL here>**
- Video (four pillars, under 5 minutes): **<add the video link here>**
- WakaTime report: `A1-25259638-WakaTime.png`
