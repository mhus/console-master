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

## 03 Textures

```text
Implementiere Unterstützung für Texturen in RaycastingCanvas.

Erstelle ein Interface TextureProvider mit der Methode:
- getTexture(path: string, width: number, height: number, EntryInfo entry, boolean light): Texture

Erstelle die Klasse PictureTextureProvider, die Texturen als
String Arrays verwaltet und rendert mittels PictureTexture. Wobei
die Texture immer auf die gesamte Fläche skaliert wird. Texturen können
gestaucht und gestreckt werden.

Interfacer Texture folgende Methoden:
- getCharAt(int x, int y): StyledChar

Der parameter light gibt an, ob die Textur hell werden soll.

Erstelle eine Klasse RegistryTextureProvider die mehrere TextureProvider
verwaltet und die Textur von dem ersten Provider zurückgibt, der die Textur
bereitstellen kann. Er cacht welcher Provider welche Textur bereitstellt. Als
Farbe wird immer colorLight/colorDark aus EntryInfo verwendet.

Passe EntryInfo an, um eine Textur zu referenzieren, erweitere um
- texture: string | null - Pfad zu einer Textur, default null
- textureInstructions: string | null - Anweisungen für die Textur, default null

Passe RaycastingCanvas an, um Texturen zu unterstützen, wenn eine Textur
referenziert wird in EntryInfo. Die Textur wird auf die gesamte Fläche transformiert
und anstelle des Char und der Farbe verwendet.

Passe RaycastingCanvasDemo an, um Texturen zu demonstrieren. Erstelle eine neue
Map mit verschiedenen Texturen.

Passe den Test RaycastingCanvasTest an, um Texturen zu testen.
```

```text
Die Struktur soll umgestellt werden:

Erstelle ein Interface TextureProvider mit der Methode:
- getTexture(path: string, width: number, height: number, EntryInfo entry, boolean light): Texture

Interfacer Texture folgende Methoden:
- getCharAt(int x, int y): StyledChar

Damit kann das Interface TextureProvider als reiner Transformator
genutzt werden der die eingeghenden Koordinaten auf das orginale
Bild transformiert.
```