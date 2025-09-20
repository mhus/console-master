# Raycasting

## 01 Erweitere Map Provider

```text
In der Klasse 'RaycastingCanvas' wird Raycasting implementiert. Aktuell
wird eine Map geladen und dargestellt.

In Zukunft soll die Map dynamisch bereitgestellt werden durch ein
MapProvider Interface. Erstelle auch ein DefaultMapprovider,
der eine einfache Map als Eingabe akzeptiert.
MapProvider Interface:

- getEntry(x: number, y: number): char
- getWidth(): number
- getHeight(): number
- getName(): string

Weite und Hoehe der Map dü®fen sich nicht ändern nach dem
zuweisen an den RaycastingCanvas.

Stelle auch die Klasse 'RaycastingCanvasDemo' um.
```

## 02 Erweitere MapEntry Eigenschaften

```text
Entries in der Map sind aktuell nur durch ein Zeichen
repräsentiert. Das soll in Zukunft ein Objekt 'EntryInfo' 
sein. Dieses Objekt soll folgende Eigenschaften haben:

- isWall: boolean - true, wenn das Feld als Wand dargestellt werden soll
- isFallthrough: boolean - true, wenn man durchlaufen kann
- isTransparent: boolean - true wenn die Wand durchsichtig ist
- char: char - das Zeichen, mit dem die wand oder der floor dargestellt wird, default '█' für Wände und '.' für Floors
- name: string - Name des Eintrags, z.B. "Wall", "Floor",
- color: AnsiColor | null - Farbe des Eintrags Default ist Weiss
- height: number - Höhe der Wand, default 1
- texture: string | null - Pfad zu einer Textur, default null

Implementiere:
- EntryInfo Klasse
- MapProvider Interface anpassen
- DefaultMapProvider anpassen
- RaycastingCanvas anpassen
  - Unterstützung für EntryInfo isWall implementieren
    - Unterstützung für EntryInfo isWalkthrue implementieren
    - Unterstützung für EntryInfo isTransparent implementieren
    - Unterstützung für EntryInfo color implementieren
    - Unterstützung für EntryInfo char implementieren
- RaycastingCanvasTest anpassen
- RaycastingCanvasDemo anpassen
```
