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