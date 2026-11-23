# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

The primary user is the developer themself, using the app as a personal daily tool. They open it in a passing moment to jot down something they need to remember (a task, an event, a deadline), then close it. They also come back to scan what's still on the list and clear what's done. No other audience is confirmed.

## Product Purpose

A fast quick-capture list for things to remember. Success means a thought goes from head to saved note in seconds, the list is easy to scan at a glance, and removing a finished item is just as fast and can be undone.

## Positioning

Speed over features. The app deliberately stays a minimal capture list: type a line, save, see it, delete it. It is not trying to be a full notes or task manager.

## Operating Context

- Used on the user's own Android phone, often one-handed and in short bursts.
- Notes are created from a single input field at the top of the main screen, so it is reachable the moment the app opens.
- Everything happens on one screen: capture, list and delete with Undo.

## Capabilities and Constraints

- **Features:** add a note from the top of the screen; list notes newest first; tap a note to expand and edit it; mark a note done and clear done notes; pin notes above the rest; swipe to delete with Undo; an empty state; light and dark themes; a splash screen.
- **Android only.** Native Kotlin with XML views, Material 3, Room, MVVM (a ViewModel with LiveData). No iOS or web version is planned.
- **Offline and on-device.** Notes are stored locally in Room. There are no accounts, no cloud sync and no network use. Android's own system backup may still include the notes.
- **Stays minimal.** New features must protect capture speed. Titles, categories, search, rich text and similar fuller-notes features are out of scope unless this changes.
- **Undecided:** a Play Store release. The app ID is `com.notes.manishsubah`.

## Brand Commitments

None are binding. The current name "NotesApp", its in-app title "My Notes" and the pencil-on-note logo exist but may be changed.

## Evidence on Hand

- Screenshots of the current app, taken on an emulator with demo notes, are in `screenshots/` and shown in the README.
- There are no users, reviews, metrics or store listing. Do not invent any.

## Product Principles

1. **Capture first.** The fastest path from an open app to a saved note wins every trade-off.
2. **One glance.** The list should show what's pending without opening, sorting or navigating.
3. **Forgiving, not fussy.** Destructive actions are quick and can be undone, so no confirmation dialogs are needed.
4. **Private by default.** Notes never leave the device.
5. **Less, done well.** Polish the few things the app does before adding anything new.
