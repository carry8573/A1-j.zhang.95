# Video Script - The Four Pillars

Target length: **4 minutes 40 seconds** (the limit is 5 minutes).
Have the IDE open with the project loaded, the `src` folder expanded, and the
console hidden until step 6. Keep the mouse still while talking, then move it
only when you point at a line.

The four pillars must each be **named**, **explained**, **pointed at in the
code**, and **justified** ("why did you choose to do it this way"). The script
does exactly those four things for each pillar, in about one minute each.

---

## 0:00 - 0:25 Opening

> "My name is Jiarui Zhang, student number 25259638. This is PROG2004
> Assessment 1: a clinic appointment system where a patient books a same day
> appointment with a general practitioner, a specialist, a nurse or a
> dietitian. It is written in Java and it has no user interface - the
> assessment asks for the object model plus a demonstration in `main`.
> I am going to take the four pillars of object oriented programming one at a
> time and show you where each one lives in my code, and why I built it that
> way."

Show: `src` folder, twelve files, then open `AssignmentOne.java` so the viewer
sees that this is the entry point.

---

## 0:25 - 1:25 Pillar 1: Encapsulation

> "The first pillar is **encapsulation**: keeping an object's data private and
> letting the object decide what is allowed to happen to it. An object should
> never be able to exist in an invalid state."

**Point at** `HealthProfessional.java`:

- the fields - "every field is `private final`: `id`, `name`, `workingDays`.
  Nothing outside this class can touch them, and once the constructor has
  finished they cannot change."
- the constructor - "all the validation happens here, once."
- `validateId` - "a loop over the characters that only accepts `'0'` to `'9'`,
  because the brief requires an ID that is numbers only. If you pass
  `"10A1"` it throws `IllegalArgumentException` with a message that names the
  rule and the bad value."
- `validateWorkingDays` - "rejects an empty roster, removes duplicates, and
  returns `Collections.unmodifiableList`, so the caller's list cannot be used to
  change this professional's roster afterwards. That is a defensive copy."

> "Why this way? Because a patient's mobile number is the key I use to find and
> cancel their appointments. If it could be changed in place, an appointment
> printed five minutes ago could quietly start pointing at a different number.
> `Patient` is built the same way - private final fields, the number validated
> in the constructor - so an appointment cannot contain an invalid phone
> number. And because the object is valid the moment it is built, no method ever
> has to re-check its own fields."

Show if time allows: `Patient.validateMobileNumber` - strips spaces and dashes,
then requires ten digits starting with a zero.

---

## 1:25 - 2:25 Pillar 2: Inheritance

> "The second pillar is **inheritance**: putting everything the four types of
> professional have in common in one place, so it is written once and reused."

**Point at** the class declaration:

```java
public abstract class HealthProfessional
```

and then `GeneralPractitioner.java`:

```java
public class GeneralPractitioner extends HealthProfessional implements Prescriber
```

> "`HealthProfessional` owns the ID, the name and the weekly roster, plus all
> the validation. `GeneralPractitioner`, `Specialist`, `Nurse` and `Dietitian`
> all `extends` it and add only what makes them different: a room and a bulk
> billing flag, a specialty and a fee, a level and years of experience, a focus
> area and a capacity. There is no duplicated field and no duplicated rule
> anywhere in the four subclasses - they call `super(...)` and that is it. And
> because `super(...)` is the first statement of every subclass constructor,
> the validation cannot be skipped."

**Point at** a subclass constructor, then at the overloaded constructor:

```java
public GeneralPractitioner(String id, String name, String roomNumber) {
    this(id, name, WEEKDAYS, roomNumber, true);
}
```

> "Here is the second constructor the brief asks for. It takes only three
> arguments and passes the sensible defaults - a Monday to Friday roster and
> bulk billing - to the full constructor. `WEEKDAYS` is a `protected static final`
> list on the superclass, so the default roster is defined once, for every
> subclass."

> "Why inheritance and not composition? Because this really is an is-a
> relationship: a specialist *is* a health professional, and the clinic stores
> all of them in a list of `HealthProfessional` and treats them the same way.
> The alternative - each type holding its own copy of an ID and a name - would
> duplicate both the state and the validation four times, and the four copies
> would eventually disagree."

---

## 2:25 - 3:40 Pillar 3: Polymorphism

> "The third pillar is **polymorphism**: one call, many behaviours. My design
> has two independent kinds of it, which is deliberate."

**First kind - through the superclass.** Point at the loop in
`AssignmentOne.displayServices`:

```java
for (HealthProfessional professional : staff) {
    ...
    professional.performService()
}
```

> "One variable type, `HealthProfessional`, and one call to `performService()`,
> but the general practitioner says it conducts a consultation and prescribes,
> the specialist says it treats patients in the cardiology clinic, the nurse
> says it takes observations and collects blood samples, and the dietitian says
> it prepares a meal plan. The loop does not know which type it is holding - the
> object supplies the answer."

Show the console output of that section so the viewer sees the four different
sentences coming from one line of code.

**Second kind - through the interface.** Point at `Prescriber.java`, then at the
`instanceof Prescriber` block in `displayServices`:

```java
if (professional instanceof Prescriber) {
    Prescriber prescriber = (Prescriber) professional;
    ... prescriber.prescribe("Amoxicillin 500 mg")
}
```

> "Only doctors may prescribe, so prescribing is not on the superclass - it is
> an interface, `Prescriber`, with one method. The general practitioner and the
> specialist implement it; the nurse and the dietitian do not. That is why this
> `instanceof` and cast are here: the capability is not part of being a health
> professional, so I check for the capability. If I had put `prescribe` on the
> superclass, a nurse would have a method she is not allowed to perform, and
> nothing but a comment would stop it being called. Why choose an interface?
> Because the compiler now enforces the rule. I cannot call `prescribe` on a
> nurse at all."

---

## 3:40 - 4:40 Pillar 4: Abstraction

> "The fourth pillar is **abstraction**: showing only what matters and hiding
> the rest, so that a caller depends on an idea rather than on a detail."

**Point at** the three abstract methods in `HealthProfessional`:

```java
public abstract String getRole();
abstract String performService();
protected abstract void addRoleDetails(List<String> lines);
```

> "`HealthProfessional` is abstract, so a 'plain' health professional cannot be
> instantiated - which is exactly what the brief asks for. But it is abstract
> in a second, more useful sense: it does not know what it is called, what it
> does, or which extra details it has, so it refuses to answer those questions
> itself and forces every subclass to answer them. If a surgeon is added
> tomorrow, the compiler will not let that class compile until it has said what
> its role is and what it does."

**Point at** `getDescription()` and `addRoleDetails`:

> "This is the template method. `getDescription()` writes the shared lines -
> role, ID, name, roster - and then calls `addRoleDetails`, which each subclass
> fills with its own lines. So the shape of the output is decided in one place,
> and a subclass cannot forget to print its role."

**Point at** `Schedule.java` and `AppointmentManager.addAppointment`:

> "Abstraction also means hiding a decision behind a name. The clinic's hours -
> eight in the morning to four in the afternoon, thirty minute slots - are not
> scattered through the manager; they are inside `Schedule`, which generates the
> sixteen start times in a loop. The manager only asks `isValidSlot(time)`. To
> change the timetable you change one object, not the booking code. And
> `Appointment` deliberately does not know whether its slot is free: availability
> depends on other appointments, and an object should not read a collection it
> does not own. The manager owns the collection, so the manager owns the rule.
> That is single responsibility, which is what abstraction is for."

---

## 4:40 - 4:55 Close

> "So: encapsulation keeps every patient and every professional valid;
> inheritance removes the duplication across the four professional types;
> polymorphism lets one list of professionals respond in four different ways,
> and a separate interface model the ability to prescribe; and abstraction means
> a plain professional cannot be created and the clinic's rules live behind one
> named class each. The demonstration in `main` runs all of it end to end,
> including the error cases - a double booking, a slot that does not exist, an
> off-duty professional, and cancellations that fail - because a system that
> only shows the happy path does not show that the rules are really enforced.
> Thank you."

---

## Recording checklist

- [ ] IDE font size large enough that the code is readable at 720p (16 pt or
      more).
- [ ] Close everything that shows email, chat or a browser tab that is not the
      code.
- [ ] Record the console output sections first, or keep the console visible in a
      second pane, so step 5 shows the four different sentences.
- [ ] Say each pillar's **name** out loud early in its section - the marker
      listens for it.
- [ ] Every pillar must be pointed at in the **actual code**, not described
      from memory.
- [ ] Say **why** each choice was made - the rubric asks for justification, not
      definition.
- [ ] Keep it under 5:00. If it runs long, cut the `Patient` example in step 2
      and the `Schedule` example in step 5, in that order.
- [ ] Upload as unlisted YouTube or SCU OneDrive, paste the link into
      `AssignmentOne.java`'s class comment and into `README.md`.
