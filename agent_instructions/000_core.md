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
Es sollen im Modul 'graphics25d' Klassen erstellt werden.
Das Modul soll eine Klasse 'Graphics25DCanvas' enthalten, die von Canvas erbt.
Die Klasse soll die Methode paint() implementieren, um 2.5D Grafiken darzustellen.

Eine 2.5D Grafik wird wie auf einem karrierten Papier dargestellt. Wobei die
Kacheln für die Z Achse 45 Grad geneigt sind.

Beispiel:
   ____
 /    /|
/____/ |
|    | /
|____|/

So können 3D Objekte in einer 2D Ansicht dargestellt werden.

Die 2.5D Grafiken sollen aus einer Liste von Objekten bestehen, die jeweils eine 
Position (x, y, z), eine Textur (String) und eine Farbe (ANSI Farbe) haben.

Die Darstellung kann von einem Punkt aus erfolgen (Camera25D), der eine 
Position (x, y, z) und eine Blickrichtung hat. Die Camera ist damei nicht
die Porjektionsebene am Convas, sondern weiter entfernt damit auch Objekte
um die Camera herum sichtbar sind.

In einer 2.5D Ansicht ist die
Blickrichtung immer von oben schräg nach unten. Kann aber aus vier Richtungen
(0 = vorne, 1 = rechts, 2 = hinten, 3 = links, 4 = oben, 5 = unten) gewählt werden.
Die Transformation erfolgt durch eine einfache Projektion der 3D Koordinaten auf eine
neue 3D Ebene die dann immer von vorne sichtbar ist. Eine Weitere Transformation 
verschiebt die Objekte so, dass sie am Cammera Punkt zentriert sind.

Beispiel:

      #########################
     #                      # #
    #                      #  #
   #                      #   #
  #                      #    #
 ########################     #
 #                      #     #
 #                      #    #
 #           C          #   #
 #                      #  #
 #                      # # 
 #                      ##
 ########################

Wobei C ist die Camera Position (0, 0, 0) und die Blickrichtung ist 0 (von vorne).
Wie weit die Camera von der Zeichenebene entfernt ist, kann am Canvas eingestellt werden.

Die Projektion kann umgestellt werden zwischen Wireframe (nur Kanten) und Solid (gefüllt).

Ein Objekt hat Flächen, die jeweils eine Texturen haben. Texturen bestehen aus Arrays
von StyledChar Objekten.

Erstelle eine Demo, du kannst dich an der Klasse 'Graphic3DDemo' orientieren um
UI und Key Handling zu implementieren. Lese die Klasse aus dem Modul 'demo' ein.
```
