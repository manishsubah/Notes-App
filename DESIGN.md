---
name: Notes App
description: A personal note board that behaves like a departure gate board, with a boarding-pass composer.
colors:
  ink: "#0E1A2B"
  ink-dark: "#E6EBF2"
  band: "#0E1A2B"
  band-dark: "#132036"
  on-band: "#FFFFFF"
  on-band-muted: "#9FB0C6"
  ground: "#E9EDF2"
  ground-dark: "#0A111B"
  panel: "#FFFFFF"
  panel-dark: "#121B27"
  panel-sunken: "#F3F5F8"
  panel-sunken-dark: "#0E1621"
  hairline: "#DCE2E9"
  hairline-dark: "#233044"
  perforation: "#C3CCD7"
  perforation-dark: "#33425A"
  pass-stroke-dark: "#2A3950"
  label: "#5E6E84"
  label-dark: "#94A3B8"
  text-done: "#6B7789"
  text-done-dark: "#7E8A9C"
  signal: "#F2B705"
  on-signal: "#0E1A2B"
  signal-lit-row: "#FFF4CC"
  signal-lit-row-dark: "#33290A"
  danger: "#B3261E"
  on-danger: "#FFFFFF"
  danger-text: "#B3261E"
  danger-text-dark: "#F2B8B5"
typography:
  title:
    fontFamily: "Barlow Semi Condensed Bold"
    fontSize: "28sp"
    fontWeight: 700
    letterSpacing: "0em"
  body:
    fontFamily: "Roboto (Material3 BodyLarge)"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "26sp"
  body-input:
    fontFamily: "Roboto (Material3 BodyLarge)"
    fontSize: "18sp"
    fontWeight: 400
  label:
    fontFamily: "Barlow Semi Condensed SemiBold"
    fontSize: "12sp"
    fontWeight: 600
    letterSpacing: "0.08em"
    fontFeature: "tnum"
  figure:
    fontFamily: "Barlow Semi Condensed Medium"
    fontSize: "14sp"
    fontWeight: 500
    letterSpacing: "0.02em"
    lineHeight: 0.95
    fontFeature: "tnum"
  tag:
    fontFamily: "Barlow Semi Condensed Bold"
    fontSize: "11sp"
    fontWeight: 700
    letterSpacing: "0.06em"
    fontFeature: "tnum"
  stub:
    fontFamily: "Barlow Semi Condensed Bold"
    fontSize: "17sp"
    fontWeight: 700
    letterSpacing: "0.08em"
    fontFeature: "tnum"
rounded:
  tag: "4dp"
  panel: "10dp"
spacing:
  xs: "4dp"
  sm: "8dp"
  md: "16dp"
  lg: "24dp"
  xl: "32dp"
  row-inset: "14dp"
  band-inset: "20dp"
components:
  stub-add:
    backgroundColor: "{colors.signal}"
    textColor: "{colors.on-signal}"
    typography: "{typography.stub}"
    width: "88dp"
  pass:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
    rounded: "{rounded.panel}"
    padding: "12dp 8dp 12dp 16dp"
  board:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
    rounded: "{rounded.panel}"
  board-column-header:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.label}"
    typography: "{typography.label}"
    height: "36dp"
    padding: "0 14dp"
  board-row:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
    typography: "{typography.body}"
    height: "56dp"
  board-row-lit:
    backgroundColor: "{colors.signal-lit-row}"
    textColor: "{colors.ink}"
  board-row-done:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.text-done}"
  section-row:
    backgroundColor: "{colors.panel-sunken}"
    textColor: "{colors.label}"
    typography: "{typography.label}"
    height: "36dp"
    padding: "0 6dp 0 14dp"
  tag-new:
    backgroundColor: "{colors.signal}"
    textColor: "{colors.on-signal}"
    typography: "{typography.tag}"
    rounded: "{rounded.tag}"
    padding: "3dp 6dp"
  tag-pinned:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.panel}"
    typography: "{typography.tag}"
    rounded: "{rounded.tag}"
    padding: "3dp 6dp"
  tag-plain:
    textColor: "{colors.label}"
    typography: "{typography.tag}"
    padding: "3dp 6dp"
  row-action:
    textColor: "{colors.ink}"
    typography: "{typography.label}"
    height: "48dp"
    padding: "0 12dp"
  row-action-delete:
    textColor: "{colors.danger-text}"
    typography: "{typography.label}"
    height: "48dp"
    padding: "0 12dp"
  text-action:
    textColor: "{colors.ink}"
    typography: "{typography.label}"
    height: "48dp"
    padding: "0 8dp"
  swipe-strip-delete:
    backgroundColor: "{colors.danger}"
    textColor: "{colors.on-danger}"
  swipe-strip-done:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.panel}"
---

# Design System: Notes App

## Overview

**Creative North Star: "The Gate Board"**

The note list is a departure board, not a stack of cards. Notes sit as rows in a fixed TIME · NOTE · STATUS grid on one white panel, rerank in place when pinned or ticked off, and a change stays lit amber until it has been seen. The composer is a boarding pass that overlaps the foot of a navy header band: a label line, the field, a hint, a dashed perforation, and an amber stub that prints the note onto the board.

The world is cool and quiet so that one colour can speak. Neutrals are tinted toward the navy ink; amber is the only signal and only ever marks a change. Labels are condensed tracked caps; every figure is tabular so times and counts line up in columns. Density is that of a real board: hairline rules, 14dp row insets, no gaps between rows. The world refuses floating note cards under a FAB and the chat-style bottom composer.

Motion follows one grammar: a row never disappears and reappears elsewhere. New rows drop in at the head while the rows below make room, reorders slide, and the system "Remove animations" setting turns every movement into an instant cut.

**Key Characteristics:**
- Navy ink band at the top, cool grey ground, white panels, one amber signal.
- Boarding-pass composer with a perforation and an amber stub as the primary action.
- Board panel with a fixed column header, sunken section rows and hairline row rules.
- Condensed tracked caps for every label, tag and action; tabular figures for every time and count.
- Change is shown by light (an amber row that fades), never by a badge that lingers.

## Colors

A cool navy-and-grey board with a single amber signal reserved for change. Every colour has a light and a dark value; the `-dark` keys are the `values-night` resolutions of the same role.

### Primary
- **Departure Navy** (`ink`, `band`): text, the PINNED tag fill, the done swipe strip and the full-bleed header band. In dark mode ink inverts to a pale blue-white and the band lifts to a separate deep navy so it still reads as a band against the darker ground.

### Secondary
- **Signal Amber** (`signal`, with `on-signal` navy text): the ADD/SAVE stub and the NEW tag. Nothing else is amber at rest.
- **Lit Row Amber** (`signal-lit-row`): the background of a row that has just been added, pinned, marked done or reopened. It holds, then fades to `panel`.

### Neutral
- **Board Ground** (`ground`): the window background behind the panels.
- **Panel White** (`panel`): the pass and the board.
- **Sunken Panel** (`panel-sunken`): section label rows (PINNED · 1, OPEN · 3, DONE · 2).
- **Hairline** (`hairline`): row rules, the column-header rule, the board's 1dp stroke, ripple colour.
- **Perforation Grey** (`perforation`): the pass's dashed tear line only.
- **Pass Stroke** (transparent in light; `pass-stroke-dark` in dark): the pass gets a 1dp outline only at night, where its shadow cannot separate it from the band.
- **Label Slate** (`label`): column heads, section labels, OPEN tag, times, hints, the meta line.
- **Done Slate** (`text-done`): done note text (with strikethrough) and the DONE tag.
- **Band Muted** (`on-band-muted`): the open/done count on the band.

### Destructive
- **Delete Red** (`danger`, `on-danger`, `danger-text`): the delete swipe strip and the Delete row action. It appears only during a destructive act.

### Named Rules
**The One Signal Rule.** Amber marks change and the primary action, nothing else. The stub is always amber; the NEW tag and the lit row are amber only while the change is live. When the light fades, NEW settles to the row's real status.

**The Paired Value Rule.** No colour ships without its night value. Light and dark are the same roles, resolved from `values/colors.xml` and `values-night/colors.xml`.

## Typography

**Display / Label Font:** Barlow Semi Condensed (Medium 500, SemiBold 600, Bold 700; SIL OFL, bundled in `res/font`)
**Body Font:** Roboto via Material3 BodyLarge (system)

**Character:** A condensed, tracked caps voice for everything that is signage (title, column heads, sections, tags, actions, the stub), against a plain readable body for the notes themselves.

### Hierarchy
- **Title** (Bold 700, 28sp, no tracking, `on-band`): "My Notes" on the band; the one heading.
- **Body** (400, 16sp, +2sp line spacing, `ink`): note text in rows, two lines when collapsed, full when expanded.
- **Body Input** (400, 18sp): the pass's text field.
- **Figure** (Medium 500, 14sp, 0.02em, line multiplier 0.95, tabular): row times (`HH:mm`/`h:mm a` today, weekday this week, `d MMM` older).
- **Label** (SemiBold 600, 12sp, 0.08em, all caps, tabular, `label`): column heads, section labels, pass label and hint, the count, meta line, row and text actions.
- **Tag** (Bold 700, 11sp, 0.06em, all caps): status tags.
- **Stub** (Bold 700, 17sp, 0.08em, all caps, `on-signal`): ADD / SAVE.

### Named Rules
**The Tabular Figures Rule.** Every number on the board (times, counts, sequence numbers) is set with `tnum` so columns align.

**The Signage Caps Rule.** Caps are for signage only. Note text is never set in caps or in the condensed face.

## Layout

A single phone column, edge to edge. The band runs under the status bar and ends 56dp below the top of the pass, so the pass overlaps the band's lower edge. Both panels sit 16dp from the screen sides; the board fills the remaining height with a 16dp gap above and below. The band's content insets are 20dp; board content insets are 14dp.

The board is a fixed three-column grid: TIME (64dp) · NOTE (flex, 8dp gutter) · STATUS (72dp column, guideline 86dp from the end). The column header (36dp) does not scroll; rows scroll beneath its hairline. Rows have a 56dp minimum, 14–16dp top padding and a 14dp bottom; hairline rules inset 14dp from the start. Sections are ordered PINNED, OPEN, DONE. Spacing steps are 4, 8, 16, 24, 32dp. Every tappable target is at least 48dp tall. System bar and IME insets are applied to the screen; the board shrinks above the keyboard.

## Elevation & Depth

Mostly flat, with exactly one lifted object. The boarding pass is a Material card at 6dp elevation (soft ambient shadow) because it is the physical object laid on the board; everything else (the board panel, rows, sections) is flat at 0dp and separated by tone and hairlines. `elevationOverlayEnabled` is off, so dark surfaces never tint with elevation; at night the pass gains a 1dp `pass-stroke-dark` outline instead.

### Shadow Vocabulary
- **Pass lift** (Material card elevation 6dp): the composer only.

### Named Rules
**The One Ticket Rule.** Only the pass casts a shadow. The board and its rows are flat.

## Shapes

Panels have gently rounded corners (10dp); tags and the snackbar are nearly square (4dp). Rows and section bands are square-edged inside the board, which clips them. The pass carries the system's one silhouette: a semicircular notch bitten out of its top and bottom edges at the perforation, each painted in the colour behind it (band above, ground below), joined by a 1.5dp dashed line (5dp dash, 4dp gap).

## Components

### Boarding Pass (composer, signature)
- **Structure:** label line (NEW NOTE, or EDITING · SEQ 007 with a CANCEL text action), the 18sp field (hint "Write a note…", up to 5 lines), a hint (ENTER TO ADD / ENTER TO SAVE), the 16dp perforation, and the 88dp stub.
- **Stub:** always amber; ADD, or SAVE in edit mode. With an empty pass, tapping it focuses the field and raises the keyboard.
- **Print-out:** on add, the text moves 14dp down and fades (160ms, standard easing), clears, then rises from 6dp above (160ms, decelerate) while the row drops onto the board.
- **Edit mode:** happens in the pass; Back or CANCEL exits.

### Board Panel
- **Corner:** 10dp, 1dp `hairline` stroke, `panel` fill, no elevation.
- **Header:** fixed TIME · NOTE · STATUS label row with a hairline under it.
- **Empty state:** an empty departure board. Three placeholder rows sit in the real column grid, each with `--:--` (Figure), a 2dp dashed rule (7dp dash, 5dp gap) and `—` (Tag), all in `perforation`, with a hairline under each. Below them is a centred "NO NOTES ON THE BOARD" (Label 15sp, `ink`) with a BodyMedium line in `label`. Last is a gesture key on a `panel-sunken` tile with a hairline stroke: TAP · EXPAND · EDIT · PIN / SWIPE LEFT · DELETE · UNDO AFTER / SWIPE RIGHT · MARK DONE · REOPEN, gestures in `ink` and actions in `label`.

### Board Row
- **Default:** time (Figure), note (Body, 2 lines), status tag; ripple on the header area.
- **Expanded:** tapping opens a meta line (SEQ 003 · ADDED …) and a wrapping row of outlined-icon text actions (Edit, Pin/Unpin, Done/Reopen, Delete in `danger-text`). Icons are Material Icons Outlined at 18dp. Only one row is expanded at a time.
- **Done:** text in `text-done` with strikethrough.
- **Lit:** `signal-lit-row` background held 2200ms, then faded to `panel` over 900ms (decelerate).
- **Swipe:** toward the start deletes (red strip, DELETE); toward the end marks done or reopens (ink strip, DONE / REOPEN). Threshold 40%. Labels are 13sp Barlow Bold caps, 20dp inset.

### Status Tags
- **NEW:** amber fill, navy text, 4dp radius, 3dp × 6dp padding. Session-fresh notes only, until the light fades.
- **PINNED:** ink fill, panel text.
- **OPEN / DONE:** text only, in `label` / `text-done`.
- **Split-flap change:** a status change on a row that is already showing (OPEN→PINNED, OPEN→DONE, DONE→OPEN, NEW settling) never swaps in place. The tag folds on its X axis (pivot at mid-height):
  1. To 90° (70ms, accelerate).
  2. A blank tile (`panel-sunken` with a hairline, already at the new label's width) flips through (70ms standard), then folds again (70ms).
  3. The new status unfolds from −90° and settles (130ms, decelerate). About 340ms in total.
  - A newly bound row, or "Remove animations", sets the tag directly.

### Section Row
- `panel-sunken` band, 36dp, label "NAME · COUNT"; DONE carries a CLEAR text action.

### Buttons
- **Row action / text action:** borderless text buttons in the Label style, `ink`, 48dp min height, `hairline` ripple; 12dp (row) or 8dp (text) side padding.
- **Primary:** the stub (see Boarding Pass). There is no FAB.

### Feedback
- Delete and Clear show a long snackbar with UNDO; the snackbar is 4dp-cornered.

### Motion
- Easing: decelerate `cubic-bezier(0.05, 0.7, 0.1, 1)`, standard `cubic-bezier(0.2, 0, 0, 1)`. Durations: short 160ms, medium 280ms.
- Adds drop from 20dp above with a fade (280ms, delayed by a third of the move duration); moves slide (280ms); removes 160ms; no change crossfade.
- With "Remove animations" on, every transition is an instant cut.

## Do's and Don'ts

### Do:
- **Do** keep amber to the stub, the NEW tag and the lit row; let the light fade (2200ms hold, 900ms fade) and settle NEW to the real status.
- **Do** move rows to their new place on pin, done and reopen; never remove and re-insert them elsewhere.
- **Do** set every label, tag and action in Barlow Semi Condensed caps with tracking, and every figure with `tnum`.
- **Do** keep the fixed TIME (64dp) · NOTE · STATUS (72dp) grid and 14dp row insets for any new row type.
- **Do** define every new colour in both `values/colors.xml` and `values-night/colors.xml`.
- **Do** respect "Remove animations" with instant cuts.

### Don't:
- **Don't** add floating note cards, a FAB or a bottom chat composer; the pass at the top is the composer.
- **Don't** give the board or rows elevation; the pass is the only lifted object.
- **Don't** use amber for decoration, emphasis or static states.
- **Don't** set note text in caps or in the condensed face.
- **Don't** use red outside delete.
