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

## 04 Background Colors

```text
Erweitere die Unterstützung für Farben in RaycastingCanvas um
Hintergrundfarben. Im EntryInfo um die Eigenschaft:
- backgroundColorLight: AnsiColor | null - Hintergrundfarbe des Eintrags bei Hell, default null
- backgroundColorDark: AnsiColor | null - Hintergrundfarbe des Eintrags bei Dunkel, default null

- Erweitere RaycastingCanvas um die Unterstützung für Hintergrundfarben.
- Erweitere RaycastingCanvasDemo um die Demonstration von Hintergrundfarben.
- Erweitere RaycastingCanvasTest um die Unterstützung für Hintergrundfarben.
```

## 05 Sprites

```text
Ich habe eine Raycasting-Engine mit einer Klasse RaycastingCanvas.
Bitte erweitere die Engine so, dass Objekte (z. B. Kisten, Gegner, Automaten) nicht nur als einfache Billboards dargestellt werden, sondern mit 8 Blickrichtungen. Je nachdem, aus welchem Winkel der Spieler auf ein Objekt schaut, soll automatisch die passende Sprite-Variante gewählt und gerendert werden.

Implementiere dazu:
	1.	Interface Sprite
	•	Repräsentiert ein einzelnes Bild (z. B. Textur), das später auf die Leinwand gezeichnet wird.
	•	Enthält mindestens Methoden/Properties für:
	•	getImage() oder Ähnliches für den Zugriff auf die Bilddaten
	•	optionale Breite/Höhe oder Skalierungsinformationen
	2.	Interface SpriteProvider
	•	Liefert für ein Objekt die passenden 8 Sprite-Instanzen.
	•	Hat eine Methode wie getSpriteForAngle(double relativeAngle), die anhand des relativen Blickwinkels des Spielers die richtige Sprite-Variante zurückgibt.
	3.	Klasse GameObject (oder ähnlich)
	•	Enthält Position (x, y), Orientierung (orientation in 45°-Schritten), und einen Verweis auf einen SpriteProvider.
	•	Kann später auch weitere Eigenschaften enthalten (interactable, enemy, hp, etc.).
	4.	Änderungen in RaycastingCanvas
	•	Beim Rendern der Objekte berechnest du den Winkel vom Spieler zum Objekt (atan2).
	•	Ermittle den relativen Winkel zwischen Spielerblickrichtung und Objektorientierung.
	•	Übergib diesen relativen Winkel an den SpriteProvider, um das richtige Sprite auszuwählen.
	•	Zeichne das Sprite abhängig von Entfernung und Tiefe korrekt mit Z-Buffering.
	5.	Beispielimplementierung
	•	Implementiere einen konkreten SimpleSpriteProvider, der ein Array mit 8 Bildern (Sprite[8]) hält und basierend auf dem Winkel den richtigen Index auswählt.
	•	Baue ein Beispiel-GameObject (z. B. eine Kiste oder ein Gegner) ein, das diesen SimpleSpriteProvider nutzt.

Bitte schreibe sauberen, modularen Code, sodass ich später leicht weitere Objekte mit unterschiedlichen SpriteProvidern hinzufügen kann.
Die Sprache ist [hier deine Programmiersprache einsetzen, z. B. Java oder Python].
```

## 06 Decke

```text
Erweitere die RaycastingCanvas um die Unterstützung für Decken. Genauso wie es einen Floor gibt, 
soll es auch eine Decke geben. Die Daten für eine Decke soll in EntryInfo definiert werden.

Eine Decke ist Optional und wird nur gerendert, wenn sie definiert ist. Die Höhe der Decke ist
konfigurierbar. Die Decke wird immer parallel zum Boden gerendert.
```

## 07 Animation

```text
Erstelle ein Interface AnimationTicker mit der Methode:
- tick(): boolean

Es wird true zurückgegeben, wenn die Animation das Update des Bildschirms erfordert.

Ein AnimationManager hält alle AnimationTicker und ruft regelmäßig tick() auf. Dafür
benutzt er einen eigenen Thread der bei start() gestartet wird und bei stop() gestoppt wird.

Die Klasse ProcessLoop soll einen AnimationManager enthalten und diesen bei start() starten
und bei stop() stoppen.
```


## 08 Hintergrund

```text
Erweitere die RaycastingCanvas um die Unterstützung für einen Hintergrund. Der Hintergrund wird immer gerendert, wenn kein Boden oder Decke
gerendert wird. Der Hintergrund wird von einem BackgroundProvider bereitgestellt.

Erstelle das Interface BackgroundProvider mit der Methode:
- getBackground(x, y): StyledChar

Erstelle die Klasse SolidColorBackgroundProvider, die einen einfarbigen Hintergrund bereitstellt.

Erstelle die Klasse CloudsBackgroundProvider, die einen Himmel mit Wolken simuliert. Die Wolken bewegen sich langsam über den Bildschirm.
Dafür wird eine einfache Perlin Noise Funktion verwendet.

Erstelle die Klasse StarfieldBackgroundProvider, die einen Sternenhimmel simuliert. Die Sterne bewegen sich langsam über den Bildschirm.

Für die Animation registriert sich der BackgroundProvider beim AnimationManager.
```


