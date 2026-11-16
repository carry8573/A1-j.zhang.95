# Design Justification

This document explains *why* the code is shaped the way it is. It is the written
justification the brief asks for. The code itself is in `src/`; `README.md`
describes how to run it and which class covers which part of the brief.

---

## 1. The starting point: what the domain actually is

The clinic's world has three kinds of thing:

1. **People who work there** - general practitioners, specialists, nurses,
   dietitians. They share a lot (an ID, a name, a weekly roster) and differ in a
   little (a room, a specialty, a nursing level, a focus area).
2. **People who are seen there** - patients. They are not staff, they do not
   share the staff hierarchy, and they have their own data (a mobile number that
   has to be valid because it is used to find and cancel their appointments).
3. **Events that connect them** - appointments. An appointment is not a kind of
   professional and not a kind of patient; it refers to one of each, at a time.

Almost every design decision below follows from that split. The one place where
it does *not* is prescribing, and that exception is what makes the design
interesting.

## 2. Abstract class for the professionals, not an interface

`HealthProfessional` is an **abstract class** rather than an interface, for three
reasons.

**Shared state must not be duplicated.** Every professional has the same three
fields - `id`, `name`, `workingDays` - and the same rules about them. If the four
subclasses each declared their own `id` field, the validation of "numbers only"
would exist four times and could drift apart. In an abstract class the field and
its validation exist once and the subclasses inherit both.

**A base class can enforce the constructor contract.** The brief requires that an
invalid professional can never be created. `HealthProfessional`'s constructor
validates, and every subclass *must* call `super(...)` as its first statement, so
there is no way to construct a subclass while skipping the validation. An
interface cannot do that: it cannot hold state and it cannot force validation.

**"A plain professional" must not be instantiable.** Making the class abstract
makes `new HealthProfessional("1001", "Someone")` a compile error, which is
literally the brief's requirement. Interfaces would allow a class that implements
them to exist with no behaviour of its own.

The price of an abstract class is single inheritance: `Nurse` cannot also extend
something else. That is acceptable here because nothing else in the domain wants
to be a nurse's parent.

The class is abstract but not *empty*: three methods are abstract so the
hierarchy is genuinely abstract rather than decorative.

| Method | Why abstract |
| --- | --- |
| `getRole()` | only a subclass knows what it is called ("General Practitioner", "Specialist - Cardiology", "Nurse", "Dietitian") |
| `performService()` | only a subclass knows what it does all day |
| `addRoleDetails(List<String>)` | only a subclass knows which extra lines its description needs |

`addRoleDetails` is the template method that makes the description work. The
superclass's `getDescription()` writes the shared lines and then calls
`addRoleDetails` to let the subclass append its own. The order of the output is
therefore controlled in one place, and a new subclass cannot forget to print its
role or its roster - it can only add lines.

## 3. Prescribing: an interface, not an inherited method

The obvious first attempt is to put `prescribe(...)` on `HealthProfessional` and
let nurses and dietitians override it to do nothing. That is wrong in a way
worth explaining, because it is the most common mistake in this assessment.

- It would put a method on every nurse and dietitian that they are not allowed
  to perform, and every caller would have to remember not to call it. The type
  system would be telling a lie.
- Refusing at run time (throwing from the subclass) turns a design error into an
  exception at 3am instead of a compile error.
- Returning a "cannot prescribe" string hides the same problem: the method
  exists and only convention stops it being used.

Instead, prescribing is a **capability**, and a capability is an interface:
`Prescriber` with one method, `String prescribe(String medicationName)`.
`GeneralPractitioner` and `Specialist` implement it; `Nurse` and `Dietitian` do
not. Now the compiler enforces the rule: you cannot call `prescribe` on a
`Nurse` reference at all, and the only way to use the capability is through
`instanceof Prescriber` plus a cast, which is a deliberate, visible decision at
the call site rather than a hidden landmine.

This also gives the program **two independent kinds of polymorphism**, which is
worth having in a submission about the four pillars:

- the inheritance-based one, through `HealthProfessional` (same call, four
  different answers to `getRole()` and `performService()`);
- the interface-based one, through `Prescriber` (same call, two different
  answers to `prescribe`, from two classes that are only related through this
  interface).

It also makes the design open for extension: if the clinic hires a pharmacist who
may adjust prescriptions but who is not a doctor, the pharmacist implements
`Prescriber` and no existing class changes.

## 4. Immutability and validation: never an invalid object

Every class that describes a thing rather than a process - `Patient`,
`HealthProfessional` and its subclasses, `Schedule` - is **immutable**: all
fields are `private final`, there are no setters, and there is no way to change
an object after construction.

The reasoning is that a patient's mobile number is the key used to find and
cancel their appointments. If it could be changed in place, an appointment
printed five minutes ago could silently start pointing at a different number.
The same applies to a professional's ID. When change is needed, the safe Java
answer at this level is to build a new object rather than mutate a shared one,
and the classes are written so that is what happens.

Validation follows the same rule: it happens **in the constructor, once**. By the
time any caller holds a reference, the object is known to be valid, so no method
needs to re-check its own fields, and a null or a `"04ABC"` mobile number simply
cannot be inside an `Appointment`.

Three details of the validation are deliberate:

1. **Character loops instead of regular expressions.** `validateId` and
   `validateMobileNumber` loop over the characters and compare with `'0'` and
   `'9'`. It is the technique taught in the early modules, it needs no imports,
   and it is easy to read out loud in the video.
2. **The error message names the rule and the bad value.** For example
   `Health professional ID must contain numbers only, but was "10A1"`. A message
   that just says "invalid ID" tells a marker nothing.
3. **Exceptions for bugs, messages for business rules.** The constructors throw
   `IllegalArgumentException` because passing a null patient is a programming
   mistake that should stop the program during development. A double booking, on
   the other hand, is an ordinary thing that happens at a reception desk, so
   `AppointmentManager.addAppointment` returns a `String` such as
   `"BOOKING REJECTED: Dr Priya Raman (ID 1003) is already booked at 09:00."`
   and the program continues. Getting this distinction right is what keeps the
   demonstration going instead of dying at the first error case.

## 5. Why the roster is a `List<WeekDay>` and not three booleans

The brief suggests "the set of days they work (for example Monday, Wednesday,
Friday)" and leaves the representation open. Three tempting options were
rejected:

- `boolean worksMonday, worksTuesday, ...` - seven fields, seven checks in
  `worksOn`, seven arguments in every constructor, and the code changes if the
  clinic ever works in half days.
- `boolean[7]` - compact, but the index has to be kept in step with an external
  convention, and `days[3]` means nothing when read.
- `String "Mon/Wed/Fri"` - readable in output but must be parsed to answer
  "does she work today?", and nothing stops `"Mon/Fri/Mon"`.

The `WeekDay` **enum** solves all three: it is type safe (no spelling errors, no
integers), it is ordered (Monday to Sunday, which is how the roster prints), it
carries the display name so the output says "Monday, Wednesday, Friday" without
a second table somewhere else, and `List<WeekDay>` accepts any number of days
from none (rejected by validation) to seven.

The list is stored as `Collections.unmodifiableList(new ArrayList<>(...))`, which
is the defensive copy pattern: the caller's list cannot be changed, and changes
to the caller's list cannot change the professional.

Comparing with the calendar is then a one-liner in one place,
`WeekDay.valueOf(LocalDate.now().getDayOfWeek().name())`, so the rest of the
program never deals with `java.time` day constants.

## 6. Why `Patient` is its own class

The brief asks the question directly, so the answer should be explicit.

- A patient exists before any appointment and after all of them are cancelled,
  so it is not a field of an appointment; the appointment refers to it.
- The mobile number has real rules - ten digits, starting with a zero, printable
  as `0412 345 678` - and those rules belong with the number, not in the
  appointment.
- Two appointments for the same patient must be recognisably for the same
  person. `Patient.equals` compares the mobile number, so "Jane Doe" and
  "J. Doe" with the same number are the same patient, and that is what makes
  "cancel all appointments for this patient" correct.
- If the clinic later needs a date of birth, an address or a Medicare number, it
  is added in one class.

The alternative - two `String` fields inside `Appointment` - would duplicate the
validation in every place an appointment is built and would make the cancellation
by phone number a string comparison against a field of a field.

## 7. Why `Schedule` exists instead of 16 hard-coded times

The brief asks for the fixed slot set to be represented flexibly, "so the
schedule can change". The naive implementation compares against a literal:

```java
if (time.equals(LocalTime.of(8, 0)) || time.equals(LocalTime.of(8, 30)) || ...)
```

That is 16 comparisons duplicated in the booking code and impossible to change
without editing the class that validates bookings.

`Schedule` instead takes a start, an end and a slot length, and generates the
list of start times in a loop:

```java
LocalTime slot = startTime;
while (slot.isBefore(endTime)) {
    slots.add(slot);
    slot = slot.plusMinutes(slotMinutes);
}
```

The clinic's default is then just a default (`DEFAULT_START`, `DEFAULT_END`,
`DEFAULT_SLOT_MINUTES`), and a 08:00-16:00 day with 30 minute slots produces
16 start times from 08:00 to 15:30. The half-open rule - the last appointment
ends at closing time but does not start at it - is documented in the class. The
constructor also rejects a length that does not divide the day evenly, so a
"45 minute slot from 08:00 to 16:00" cannot produce a misleading slot list.

`AppointmentManager` asks `schedule.isValidSlot(time)` and never knows what the
hours are. `AssignmentOne` prints `schedule.describe()`, and the same object can
be constructed with different hours elsewhere without touching any other class.

## 8. The collection: why `ArrayList`

The brief asks which data structure from the early modules is appropriate and
why, so the alternatives were considered explicitly.

| Option | Why not (or why) |
| --- | --- |
| `Appointment[]` | The number of bookings is not known in advance and the array cannot grow; cancellation would leave holes or force a manual shift. |
| `HashMap<LocalTime, Appointment>` | Fast by time, but the manager filters by professional ID *and* by patient mobile, so two more maps would be needed and they could disagree. Ordering by time would also be an accident of the key type rather than a stated rule. |
| `HashSet<Appointment>` | Excellent for the double-booking test, but has no order, cannot be indexed, and printing it in time order would mean copying and sorting anyway. |
| `TreeSet<Appointment>` | Keeps the order automatically, but the manager wants arrival order for its own bookkeeping and explicit control over when sorting happens (the brief asks for sorting to be *applied*, including to a filtered subset). |
| `ArrayList<Appointment>` | **Chosen.** Grows with the day, keeps insertion order, is cheap to walk for filtering, and `Collections.sort` gives the time order on demand, for the whole list or for any subset. |

`equals` is still used, but for the question it is good at: "is this exact
booking already in the list?" The manager's double-booking check is therefore
`appointments.contains(appointment)`, which is one readable line, and it works
only because `Appointment.equals` is defined on the right pair of fields.

## 9. Equality: which fields, and why that pair

Equality has to be decided twice in this design, and both decisions are load
bearing.

**Professionals are equal by ID.** `HealthProfessional.equals` compares IDs and
`hashCode` returns `id.hashCode()`. The ID is the clinic's unique key: two
objects with ID 1001 are the same person even if one was built with a
misspelled name or an old room number. Comparing every field instead would make
two records of the same person unequal and would break "add or update by ID".
`AssignmentOne` demonstrates the point with a second object that has the same ID
but a different name and room, and prints `true`.

**Appointments are equal by professional plus time.** That is exactly the
definition of a double booking: same professional, same slot. It deliberately
ignores the patient and the reason, because moving Jane's 09:00 appointment to
Michael does not create a second 09:00 slot for that doctor. This is what makes
`contains(...)` a correct double-booking test.

Both classes also override `hashCode`, for the contract that two equal objects
must have equal hash codes; without it, an `Appointment` would vanish in a
`HashSet`. `hashCode` is built from the same fields as `equals`, so the contract
holds by construction.

## 10. Sorting and comparison

Times are stored as `LocalTime` rather than `String` because `String` ordering is
lexicographic: `"14:30"` sorts *before* `"9:00"`, and a table of a day's
appointments would be wrong exactly when it matters. `LocalTime` compares
chronologically and is the right type for a time of day.

`Appointment implements Comparable<Appointment>` and `compareTo` breaks ties by
professional ID. The brief allows either order for the same time with different
professionals, but a total order is still required: without the tie-break,
`Collections.sort` could order two equal-time appointments differently on
different runs, which would look like a bug on the video. With it, the output is
reproducible.

Because comparison lives in the class, `Collections.sort(list)` needs no
comparator, which keeps the manager free of ordering logic.

## 11. Who checks what: responsibilities

A design is mostly a decision about who is allowed to know what. The split here:

| Class | It answers | It does not know about |
| --- | --- | --- |
| `HealthProfessional` + subclasses | who am I, what do I do, when do I work | patients, appointments, slots, the clinic's hours |
| `Patient` | who am I, what is my validated phone number | professionals, appointments |
| `Schedule` | what are the clinic's slots | who is booked, who is available |
| `Appointment` | which patient with which professional at which time | whether the slot is free, whether the professional is on duty |
| `AppointmentManager` | is this booking legal, who is booked when, add, filter, sort, cancel | how the slot list is generated, what a professional is made of |
| `AssignmentOne` | how to demonstrate all of the above | any rule at all |

Two consequences are worth pointing out in the video:

1. **`Appointment` is deliberately dumb about validity of a booking.** It
   guarantees completeness (no null patient, no blank reason) but not
   availability. Availability depends on *other* appointments, and an object
   should not need to read a collection it does not own - that would be a
   circular dependency and a nightmare to test. The manager owns the
   collection, so the manager owns the rule.
2. **The clinic's hours are in `Schedule` alone.** Nothing else in the program
   contains the number 8, 16 or 30 as a magic number.

## 12. Extending the design

The brief says the clinic may later add types of professional and change the
schedule, so the design was checked against those changes:

- **A surgeon.** `Surgeon extends HealthProfessional implements Prescriber`
  with an operating theatre and a list of procedures. Nothing existing changes;
  the roster list in `AssignmentOne` gains one line, and the table, filtering,
  sorting and cancellation all work on it already, because they only use
  `HealthProfessional`.
- **A physiotherapist who cannot prescribe.** Same as above without the
  interface, which is precisely why prescribing is not on the superclass.
- **A different timetable.** `new AppointmentManager(new Schedule(LocalTime.of(9, 0),
  LocalTime.of(17, 0), 20))`. No other class changes.
- **Several days of bookings.** `Appointment` already stores a `WeekDay` and the
  manager already takes the day it is managing, so the change is confined to
  replacing `WeekDay` with `LocalDate` in `Appointment` and letting the manager
  hold a date per appointment instead of one `workingDay`. No other class needs
  to change.
- **A database.** The immutable value classes, the single manager and the ID
  based equality are the same ideas a persistence layer needs; only the manager
  would change.

## 13. Keeping the rules flexible

A design that only works for one clinic, on one day, with one timetable would
describe today's data rather than model the clinic. Four decisions keep the
rules reusable without weakening any of them:

1. **The timetable is an object, not a constant.** `Schedule(start, end,
   slotMinutes)` builds its own slot list and rejects a slot length that does not
   divide the opening period exactly. An evening clinic open 17:00 to 20:00 in
   20 minute slots is
   `new Schedule(LocalTime.of(17, 0), LocalTime.of(20, 0), 20)` - nine slots -
   and neither `Appointment` nor `AppointmentManager` is touched.
2. **The day is supplied, not read from the clock.** `AppointmentManager` keeps a
   `workingDay` field that is `null` for "use the system date" and holds a
   `WeekDay` when a caller pins the manager to a day. Every roster check goes
   through `getWorkingDay()`, so the rule is written once and can be applied to
   any day. That is also what makes the rule demonstrable: the program does not
   have to wait for Saturday to prove that a Monday to Friday professional is
   refused on Saturday.
3. **The appointment carries its own day.** `Appointment` stores a `WeekDay`
   rather than a `LocalDate`. The clinic books the current week, so a full date
   would carry information the clinic never uses, while the day of the week is
   the vocabulary the roster is written in. Two useful consequences follow:
   printing an appointment can never show a day that disagrees with the booking,
   and the manager can refuse an appointment that belongs to another day's
   collection with a precise message instead of quietly storing it in the wrong
   list.
4. **The manager can answer "what is free?" as well as "is this allowed?".**
   `getFreeSlots(professional)` walks the timetable, skips the slots that
   professional already occupies and returns the rest, taking the roster into
   account. A receptionist interface can therefore offer the available times
   instead of making the patient guess a time and be rejected.

The cost is a slightly larger `Appointment` and `AppointmentManager`. The gain is
that each rule exists once, is parameterised, and both parameters - the
timetable and the day - are exercised by the demonstration rather than described
in a comment.

## 14. Trade-offs that were consciously accepted

- **Abstract class costs the subclasses their one inheritance slot.** Accepted:
  the professionals have a genuine is-a relationship, and the alternative (an
  interface plus duplicated fields) is worse.
- **`instanceof` in the demonstration.** `AssignmentOne.displayServices` uses
  `instanceof Prescriber`, `instanceof Nurse` and `instanceof Dietitian` to show
  the unique behaviours. That is the right tool for "this one capability is not
  on the superclass"; it would be the wrong tool inside the manager, which never
  needs to know a professional's exact class.
- **Messages instead of a result type.** A `BookingResult` class with a
  boolean and a message would be more disciplined, but it adds a class whose
  only job is to carry a `String`, and the brief's marking is about the object
  model of the clinic, not about command/result patterns. Returning a printable
  message keeps the demonstration readable.
- **No unit tests.** The brief asks for an end-to-end demonstration in `main`,
  and the error cases are exercised there, deliberately, with their output on
  screen. Tests would be a good addition and are the first thing to add next.
- **Default package.** Required by the brief's "create a class called
  `AssignmentOne` in the `src` directory", and it keeps the submission easy for
  a marker to open and run.
