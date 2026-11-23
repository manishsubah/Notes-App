---
version: 1
slug: "app-src-main-res-layout-activity-main-xml"
primary_target: "app/src/main/res/layout/activity_main.xml"
related_targets: ["app/src/main/java/com/notes/manishsubah/presentation/ui/MainActivity.kt"]
---

# Main notes screen

Scope: the single main screen (`activity_main.xml`, `MainActivity`). Visitor mode: Operate.

Audience and job: the owner, on their own phone, captures a thing to remember in seconds, then scans what is still open, ticks off, pins, edits or clears notes.

Constraints: native Android Views + Material 3 controls; add box at the top (user requirement); newest first; swipe to delete with Undo, tap to expand and edit, mark done, pin; unique (not stock Material), not childish, not cluttered; motion never delays capture.

## Direction contract

THESIS: The note list behaves like a departure gate board: notes rerank in place, and a change stays lit until you have noticed it. It refuses the category default of floating cards under a FAB or a chat-style bottom composer.

OWN-WORLD: Cool grey ground, white panels, navy-ink header band, one amber signal reserved for change (new, just pinned, just done). Labels in condensed tracked caps; times in tabular figures; the composer is a boarding pass with a dashed perforation and an amber stub; hairline row rules in a fixed column grid.

STORY: The owner sees at a glance how many notes are open, writes into the pass at the top, and watches the note land lit at the head of the board. They tick off, pin or swipe away notes and see each one move to its new place rather than vanish.

FIRST VIEWPORT: A navy band holds "My Notes", the "7 OPEN · 1 DONE" count and a theme switch (device / light / dark). A white boarding pass overlaps the band's lower edge, full width minus 16dp: a label line "NEW NOTE" and the text field on the left; a perforation; an 88dp amber ADD stub on the right (the primary action). Below it, a board panel with a fixed column header (TIME · NOTE · STATUS) and section labels PINNED, OPEN, DONE.

FORM: Gate Board challenger fused with the product (catalog vernacular-ephemera-boarding-pass-and-gate-board), chosen by the user over the assigned Swatch Strip (grounded #6). seed:656c0ede1eb5f3be

Signature interaction and motion grammar: a row never disappears and reappears elsewhere. Additions slide in at the head lit amber, pin/done reorders move the row in place and light it, and the light fades after it has been seen. The stub prints the note: the text drops out of the pass as the row arrives. "Remove animations" gives instant cuts.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

## Unresolved
- A real Play Store identity (name and icon) is still open in PRODUCT.md.
