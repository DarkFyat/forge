# Forge desktop UI: Arcane Glass (work in progress)

## Context and scope

Forge has two different front ends: the desktop client is Swing, while the mobile/Android/iOS client uses libGDX. They share skin assets and game logic, but not their views. This first pass targets the desktop shell and common Swing controls. It is not a complete redesign of either client. The web preview in this directory is a **design concept**, not the application or a screenshot of the implementation.

## Direction

Use dark ink surfaces, restrained cyan accents and violet focus rings, clear system typography, compact rounded controls and a simpler navigation hierarchy. Keep card art and game information visually prominent. Use colour together with text or shape for states; ensure the keyboard focus indicator remains visible. Reuse the existing controllers, navigation and localization rather than changing game rules.

The desktop tokens and painting helpers live in `forge-gui-desktop/src/main/java/forge/toolbox/FDesign.java`. The first pass changes the home sidebar, selected navigation tabs, common buttons, scrollbars and some Swing defaults. Default skin resources remain untouched because they are shared with the mobile client. Custom skins still load their assets and colour swatches, but some new components use fixed Arcane Glass colours; full custom-skin compatibility needs further work and testing.

## Next passes

1. Run the desktop client and exercise startup, home navigation, compact menu, preferences, dialogs, deck editor and an entire game at multiple window sizes and font settings. Fix clipping, contrast, focus and layout regressions.
2. Complete the component system and replace remaining bespoke gradients/textures, while making new controls derive their colours from the selected skin.
3. Redesign the deck editor and match interface around their information density and existing interactions, with accessibility and localisation checks.
4. Assess mobile/Android/iOS separately: libGDX needs its own component implementations and touch-sized layouts. Coordinate any shared asset changes across both clients.

## Validation status

The changed Java files were syntax-parsed and the diff checked for whitespace errors. A local Maven build was attempted with a Java runtime, but dependency resolution failed because Maven Central was unreachable; the full application has **not** been compiled, run, or visually tested. `FDesign`, `FButton` and `StartButton` were separately compiled with the Eclipse Java compiler and exercised headlessly using test doubles for Forge dependencies. `FDesignTest` adds regression checks for a future full Maven test run. These isolated checks do not verify integration, startup, layouts or custom skins. The concept preview is illustrative only.
