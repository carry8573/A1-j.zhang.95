# PROG2004 Assessment 1 - Clinic Appointment System

Jiarui Zhang (25259638)
PROG2004 Object Oriented Programming, Southern Cross University
Assessment 1, 40%, individual

A small console program for a health clinic. A patient books an appointment with
a health professional for the current day, and the clinic runs from 08:00 to
16:00 in 30 minute slots. There is no menu, no login and no file storage: the
brief asks for the object model, and for `AssignmentOne.main` to show it working.

## 1. How to compile and run

The project was written and tested with JDK 21. It uses no libraries, so any JDK
from version 8 upwards will compile it.

```bash
cd A1-j.zhang.95
javac -d out src/*.java
java -cp out AssignmentOne
```

In an IDE, open the folder as a Java project and run the `AssignmentOne` class.

The classes are in the default package, directly in `src`, because the brief
asks for `AssignmentOne` to be created in the `src` directory.

## 2. Project structure

| File | Part | Responsibility |
| --- | --- | --- |
| `src/AssignmentOne.java` | 3, 4, 5, 6 | `main`; builds the clinic and runs every demonstration |
| `src/WeekDay.java` | 1 | `enum` Monday to Sunday with a readable name |
| `src/Prescriber.java` | 2 | `interface`: the ability to prescribe medication |
| `src/HealthProfessional.java` | 1 | `abstract` superclass: ID, name, roster, validation, printing |
| `src/GeneralPractitioner.java` | 2 | A doctor who sees general patients; prescribes |
| `src/Specialist.java` | 2 | A doctor with a specialty and a fee; prescribes |
| `src/Nurse.java` | 2 | A nurse with a level and experience; takes blood samples |
| `src/Dietitian.java` | 2 | A dietitian with a focus area and a weekly patient load |
| `src/Patient.java` | 4 | A patient: name and a validated 10 digit mobile number |
| `src/Schedule.java` | 5 | The clinic's slot set, built from a start, an end and a length |
| `src/Appointment.java` | 4 | One patient, one professional, one time; `Comparable` and `equals` |
| `src/AppointmentManager.java` | 5 | The one collection of appointments: add, view, filter, sort, cancel |

Two other files are included for reference and are not needed to compile:

| File | Purpose |
| --- | --- |
| `DESIGN.md` | the written justification of the design |
| `SAMPLE_OUTPUT.txt` | one complete run of the program |

## 3. Design

`HealthProfessional` is abstract. It holds the ID, the name and the days the
professional works, and it checks all three in the constructor, so an object
that exists is already valid. The class also declares three abstract methods -
`getRole()`, `performService()` and `addRoleDetails(List<String>)` - which means
a "plain" professional cannot be created and every subclass has to say what it
is and what it does. The four subclasses add only their own state, and everything
shared, including the validation, stays in one place.

Prescribing is an interface, `Prescriber`, instead of a method on the superclass,
because not every professional is allowed to prescribe. General practitioners and
specialists implement it, nurses and dietitians do not, and the program can still
ask any professional `instanceof Prescriber` without knowing the exact class.

`Patient`, `Schedule`, `Appointment` and `AppointmentManager` build the booking
side on top of that. The rules about what a booking may be live in the manager,
and the rules about what the clinic's time slots are live in `Schedule`, so
neither class has to know about the other's job. The timetable and the day the
manager is working on are given to the manager as objects rather than written
into it, which is why the same classes can run an evening clinic, or a day other
than the machine's own, without any code changing.

## 4. How each part of the brief is covered

### Part 1 - one class for a generic health professional

- `HealthProfessional` holds `id`, `name` and `workingDays`. The roster is a
  `List<WeekDay>` kept as an unmodifiable copy, so it cannot be edited from
  outside the object.
- Three private static helpers do the checking, and the constructor calls them:
  `validateId` loops over the characters and accepts `'0'` to `'9'` only,
  `validateName` rejects null and blank names, and `validateWorkingDays` rejects
  a null or empty roster, removes duplicates and wraps the result with
  `Collections.unmodifiableList`. Each failure throws `IllegalArgumentException`
  with a message that says what was wrong and what arrived.
- All fields are `private final` and there are no setters, so a professional
  cannot be put into an invalid state after it is created.
- `toString()` returns `getDescription()`, which prints the role, the ID, the
  name and the roster and then asks the subclass for its own lines. Printing a
  professional therefore shows everything a receptionist needs.
- There are two constructors, `(id, name, workingDays)` and `(id, name)`, and the
  second one defaults the roster to Monday to Friday
  (`protected static final List<WeekDay> WEEKDAYS`). The subclasses follow the
  same pattern, for example `GeneralPractitioner(id, name, roomNumber)` and
  `Specialist(id, name, specialty)`.

### Part 2 - at least four specific types, one of them a GP

| Type | Unique state | Unique behaviour |
| --- | --- | --- |
| `GeneralPractitioner` | room number, bulk billing flag | general consultations; `prescribe(...)` |
| `Specialist` | specialty, consultation fee | treats patients in that specialty; `prescribe(...)` |
| `Nurse` | level (`Enrolled`, `Registered`, `Nurse Practitioner`), years of experience | `takeBloodSample()` |
| `Dietitian` | focus area, weekly capacity | `createMealPlan()` |

- Every shared field and every validation rule is written once in
  `HealthProfessional`; the subclasses call `super(...)` and add their own state.
- The type is always visible. `getRole()` returns `"General Practitioner"`,
  `"Specialist - Cardiology"`, `"Nurse"` or `"Dietitian"`, and the description
  prints the level, the specialty and the other details underneath.
- `AssignmentOne` keeps all ten professionals in one `List<HealthProfessional>`
  and calls `getRole()` and `performService()` on the superclass reference, so
  each object answers in its own way.
- `HealthProfessional` is `abstract`, so `new HealthProfessional(...)` does not
  compile. A plain professional is not a thing the clinic can have.
- The nurse and the dietitian print `"cannot prescribe medication"`, which is the
  other half of the same idea: prescribing is a capability only some of the
  professionals have.

### Part 3 - create and display the objects

`createProfessionals()` builds ten objects: four general practitioners (three
with the long constructor and one with the short one, to show the defaults), two
specialists, two nurses and two dietitians. `displayEveryProfessional()` prints
them in one loop with a single `println(professional)` per object, and then a
second loop prints the role and the `performService()` text so the difference
between the types is visible.

### Part 4 - the appointment

- `Patient` is its own class because a patient exists whether or not there is an
  appointment, and because the mobile number has its own validation and its own
  formatted form. Two patients are equal when their mobile numbers are equal.
- `Appointment` holds `patient`, `professional`, `time` and `reason`. The
  constructor rejects a null patient, professional or time, and a null or blank
  reason, so an incomplete appointment cannot be built. Slot and roster checks
  follow immediately in `AppointmentManager`, so a refused booking is never
  stored either.
- An appointment prints the professional's full description, so the appointment
  always shows who it is with. `toString()` gives the detailed view and
  `toSummaryString()` gives the one line used in the tables.
- The time is a `LocalTime`, not a `String`, so ordering is chronological: as
  text, `"14:30"` would sort before `"9:00"`. `Appointment` implements
  `Comparable<Appointment>` and `compareTo` compares the time first and then the
  professional's ID, so two appointments at the same time still have a stable
  order.

### Part 5 - managing all appointments in one place

- The collection is an `ArrayList<Appointment>` inside `AppointmentManager`. The
  number of bookings is not known in advance and appointments are added and
  cancelled during the day, which is what a list is for. A `HashMap` would find
  one booking faster but would lose the ordering, and the clinic searches by two
  different keys (professional ID and patient mobile) and prints in time order.
  A `TreeSet` would sort automatically but would also refuse the "same time,
  different professional" case and take away control over when sorting happens.
- `addAppointment` checks, in this order, for a null appointment, an appointment
  written for another day, a time that is not a clinic slot, a professional who
  is not rostered that day, and a double booking. Each one returns a
  `"BOOKING REJECTED: ..."` message that says why.
- The double booking test is one call, `appointments.contains(appointment)`,
  because `Appointment.equals` and `hashCode` are defined by professional and
  time.
- `getAllAppointments()`, `getAppointmentsForProfessional(id)` and
  `getAppointmentsForPatient(mobile)` return sorted copies, so the caller always
  sees earliest to latest and cannot damage the manager's list.
- `sortByTime(list)` uses `Collections.sort`, so it works on the whole
  collection and on any filtered subset.
- Cancelling is done by professional ID plus time, or by patient mobile number.
  Both methods return `"CANCELLED: ..."` on success and
  `"CANCELLATION FAILED: ..."` with the reason on failure, including the case
  where there was nothing to cancel.
- The slots are not hard-coded in the booking code. `Schedule` builds them from a
  start time, an end time and a slot length, so a different timetable is a
  different `Schedule` object and the manager does not change.
- The day is not hard-coded either. The manager gets it from `getWorkingDay()`,
  which returns the real current day unless the manager was created with a fixed
  one, for example `new AppointmentManager(schedule, WeekDay.SATURDAY)`. The
  roster rule is written once and works for any day, and `AssignmentOne` runs it
  a second time for Saturday.
- `getFreeSlots(professional)` answers the other question a receptionist asks:
  which slots this professional could still be booked into, given the timetable
  and the roster.
- Two professionals are the same when their IDs are equal, which is what
  `equals` and `hashCode` in `HealthProfessional` do. `AssignmentOne` shows this
  with two objects that have the same ID but a different name and room.

### Part 6 - the end to end demonstration

`AssignmentOne.main` runs the whole story with a printed title for each section:
the professionals and their own behaviour, the roster for today, the timetable
and the patients, seven bookings across all four types, one full appointment, the
`equals` demonstration, every appointment earliest first, the same list filtered
by professional ID and by patient mobile, sorting on a filtered subset and on the
whole collection, the same classes used as a different clinic (an evening
timetable with 20 minute slots, a Saturday instead of today, the free slots for a
professional, and an appointment for the wrong day being refused), the booking
errors (double booking, a time that is not a slot, a professional who is off
duty, no details at all, and two constructor exceptions), the cancellation errors
followed by two successful cancellations, and finally the collection after the
cancellations, where repeating one of them now fails and proves the first one
removed the appointment.

`SAMPLE_OUTPUT.txt` is a complete captured run.

## 5. Design decisions and assumptions

1. **Bookings are for today only.** The brief says so, so `Appointment` stores a
   `LocalTime` and a `WeekDay`, and the manager refuses a booking for a
   professional who is not rostered on the day it is managing.
2. **The day ends at 16:00, so its last slot starts at 15:30.** With 08:00 to
   16:00 and 30 minute slots there are 16 start times. A 15:30 appointment runs
   until closing time, and 16:00 is the end of the day rather than a slot.
3. **The same professional is never booked twice into the same slot.** Two
   different professionals may both be booked at 09:00.
4. **Equality is by ID, not by every field.** Two objects with the same ID are the
   same professional even if a name was spelled differently or a room changed,
   the same way a database key works.
5. **The manager returns messages and the constructors throw.** A refused booking
   is an everyday event at a reception desk, so `addAppointment` and the cancel
   methods return a `String` the caller can print. An appointment with no patient
   is a programming mistake, not a business rule, so the constructors throw
   `IllegalArgumentException`.
6. **Only the concepts from the unit are used.** No lambdas, no streams, no
   `record`, no `var`, no `Optional`, no regular expressions and no external
   libraries. The validation is written with plain character loops, and the only
   library classes used are `String`, the wrapper types, `List`/`ArrayList`,
   `Collections`, `LocalTime`, `LocalDate`, `Duration`, `DateTimeFormatter` and
   `String.format`.
7. **The program only writes to the screen.** No files, no `Scanner` and no user
   input, as the brief requires.

## 6. Generative AI declaration

> I acknowledge that I have used Generative AI tools to complete this assessment
> in accordance with the conditions outlined in the Assessment Brief. I used
> ChatGPT to help me plan the class design, to review my code for style and
> Javadoc comments, and to suggest the wording of the comments and of this
> README.

## 7. Links

- Repository: **https://github.com/carry8573/A1-j.zhang.95**
