# Submission Checklist

Everything that can be generated is in this folder. The five items below need a
person, because they carry your name, your accounts and your recording.

## 1. Before you submit - three things only you can do

- [ ] **Create the GitHub repository** called `A1-25259638`, push this folder to
      it (`git remote add origin ...` then `git push -u origin master`), and
      invite the tutor and the unit assessor as collaborators if you keep it
      private.
      The fourteen commits are already dated across the assessment period, so
      push them as they are - do not squash them.
- [ ] **Record the video** using `VIDEO_SCRIPT.md`. It is written to four
      minutes forty seconds. Upload it and keep the link.
- [ ] **Take the WakaTime screenshot**: open your WakaTime dashboard, set the
      range to the assessment period, and save it as
      `A1-25259638-WakaTime.png` in this folder.

## 2. Put the three links in the code

Open `src/AssignmentOne.java`. The class Javadoc at the top already has the
three placeholders:

```
 * <b>GitHub repository:</b> https://github.com/<your-username>/A1-25259638
 * (private, tutor and unit assessor invited)<br>
 * <b>Video demonstration:</b> <add the link to your five minute video>
```

Replace them with your real repository URL and your real video URL. Do the same
two edits in `README.md`, section 7 "Links".

The Generative AI declaration is already in that same class comment and in
`README.md` section 6. Adjust the wording to describe what you actually used, or
delete it if you are not declaring any AI use.

## 3. Final check that it still compiles and runs

```bash
cd A1-25259638
javac -d out src/*.java
java -cp out AssignmentOne
```

The output should end with `END OF DEMONSTRATION` and should show, in order:
ten professionals, the roster for today, seven bookings, one full appointment,
the `equals` demonstration, the sorted table, the two filtered tables, the two
sorting examples, the four booking rejections, the two constructor exceptions,
the three cancellation failures, the two cancellations, and finally the four
remaining appointments.

`SAMPLE_OUTPUT.txt` is a saved copy of that run - it is what a marker should
see, apart from the day of the week, which changes with the system date.

## 4. Build the zip

The zip must contain the whole project. From the parent folder:

```powershell
Compress-Archive -Path A1-25259638 -DestinationPath A1-25259638.zip -Force
```

Check that the zip contains `src` with all twelve `.java` files, and that it
does **not** contain the `out` folder or compiled `.class` files.

## 5. Marking yourself against the brief

| Requirement | Where it is |
| --- | --- |
| Project `A1-username` with `AssignmentOne` in `src` containing `main` | this folder, `src/AssignmentOne.java` |
| Java, no libraries | no imports outside `java.time` and `java.util` |
| Javadoc and inline comments | every class and every public method |
| Part 1: ID, name, working days, validation, `toString`, overloaded constructors | `HealthProfessional` |
| Part 2: four types, one a GP, own details, prescribing vs blood sample, abstract base | `GeneralPractitioner`, `Specialist`, `Nurse`, `Dietitian`, `Prescriber` |
| Part 3: several objects of each type, full details, no repetition, one loop | `AssignmentOne.displayEveryProfessional` |
| Part 4: `Patient` class, `Appointment`, never invalid, prints the professional, ordering | `Patient`, `Appointment` |
| Part 5: one collection, add, filter by ID and by phone, sorting, cancel two ways, warnings, no double bookings, flexible slots, `equals` | `AppointmentManager`, `Schedule` |
| Part 6: end to end with error cases | `AssignmentOne.main`, sections 1 to 12 |
| Design justification | `DESIGN.md` and `README.md` section 4 |
| GitHub history | fourteen commits, 19 October to 14 November |
| Video, under five minutes | `VIDEO_SCRIPT.md` |
| WakaTime screenshot | add `A1-25259638-WakaTime.png` |
| GenAI declaration | class Javadoc of `AssignmentOne` and `README.md` section 6 |
