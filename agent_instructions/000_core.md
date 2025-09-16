# Initial instructions for the agent.

## 01 Basic Instructions

```text
The project is a console framework to visualize game content in a console application.
The base class is a Canvas that contains text/graphics and can be rendered to the console.
The Canvas can use paint() method to draw content.
A Composite Canvas can contain multiple Canvases that can be rendered together.

The Screen Composite Canvas is the main entry point for the application.

There is a minimum size of the console. If the console is smaller than the minimum size, 
a warning Canvas is displayeed instead of the actual content.
```

```text
Der framework kann auch ANSI Farben und Formatierungen verwenden.
```

## 02 Graphics 2.5D

```text
Es soll ein neues Modul 'graphics25d' erstellt werden.
Das Modul soll eine Klasse 'Graphics25DCanvas' enthalten, die von Canvas erbt.
Die Klasse soll die Methode paint() implementieren, um 2.5D Grafiken darzustellen.

Die 2.5D Grafiken sollen aus einer Liste von Objekten bestehen, die jeweils eine Position (x, y, z), eine Textur (String) und eine Farbe (ANSI Farbe) haben.

Die Darstellung kann von einem Punkt aus erfolgen (Camera25D), der eine Position (x, y, z) und eine Blickrichtung (0,90,180,270 Grad) hat.
```
