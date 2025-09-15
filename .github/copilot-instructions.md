# Copilot Instructions for Java Development

## General Guidelines

- the Base Package is 'com.consolemaster'
- Use Java 21 or higher.
- Use english for all code, comments and documentation.

## Project Structure

Multi-module Maven project with clear separation of concerns:
- **Parent POM** (`console-master`): Manages dependencies and plugin versions
- **Core Module** (`console-master-core`): Framework implementation with all core classes and tests
- **Demo Module** (`console-master-demo`): Example applications demonstrating framework features
- **Graphic3D Module** (`console-master-graphic3d`): 3D graphics extension for rendering 3D content in console

## Libraries and Frameworks

- Use lombok for boilerplate code reduction.
- Use JUnit 5 for unit testing.
- Use Mockito for mocking dependencies in tests.

## Project

The project is a console framework to visualize game content in a console application.
The base class is a Canvas that contains text/graphics and can be rendered to the console.
The Canvas can use paint() method to draw content.
A Composite Canvas can contain multiple Canvases that can be rendered together.

The Screen Composite Canvas is the main entry point for the application.

There is a minimum size of the console. If the console is smaller than the minimum size, 
a warning Canvas is displayed instead of the actual content.

## Current Framework Implementation Status

### Core Canvas System
- **Canvas**: Abstract base class for all drawable elements with position (x,y), size (width,height), visibility
- **Composite**: Container for multiple child canvases with automatic layout management
- **ScreenCanvas**: Main entry point that manages terminal and minimum size validation
- **TextCanvas**: Simple text display implementation (legacy)
- **Text**: Modern text component with advanced styling and JLine integration
- **Box**: Specialized canvas with border support and single child containment
- **Scroller**: Scrollable container for content larger than viewport

### Graphics and Rendering System
- **Graphics (Abstract Base Class)**: Unified abstract base class for all graphics contexts
- **JLineGraphics**: Enhanced graphics implementation using JLine's AttributedString for superior ANSI support
- **LegacyGraphics**: Backward-compatible implementation using StyledChar buffer
- **ClippingGraphics**: Graphics wrapper that clips drawing operations to specified bounds
- **AnsiColor**: Enum for ANSI foreground/background colors (standard + bright variants)
- **AnsiFormat**: Enum for ANSI text formatting (bold, italic, underline, strikethrough, dim, reverse, blink)

### 3D Graphics System (graphic3d module)
- **Point3D**: 3D point representation with mathematical operations (addition, subtraction, normalization, distance calculation)
- **Matrix4x4**: 4x4 transformation matrix supporting:
  - Translation, rotation (X/Y/Z axes), and scaling transformations
  - Perspective projection matrix generation
  - Matrix multiplication and point transformation
  - View and projection matrix calculations
- **Mesh3D**: 3D mesh objects with triangular faces supporting:
  - Vertex and face management
  - Built-in primitive creation (cube, pyramid)
  - Mesh transformation using matrices
  - Normal vector calculation for lighting
- **Camera3D**: 3D camera system with:
  - Position and rotation (Euler angles) management
  - Movement functions (forward, right, up movement)
  - View matrix and projection matrix generation
  - Look-at functionality for target-based camera positioning
- **Graphic3DCanvas**: Main 3D rendering canvas extending base Canvas with:
  - Three rendering modes: WIREFRAME, FILLED, BOTH
  - Depth testing with Z-buffer for correct depth rendering
  - Backface culling for performance optimization
  - Configurable characters and colors for wireframe and fill rendering
  - Bresenham line algorithm for precise edge rendering
  - Scanline triangle filling for solid surfaces
  - ASCII-based 3D rendering optimized for console output

### Layout System
- **Layout**: Interface for automatic component arrangement
- **NoLayout**: Default layout that preserves manual positioning
- **FlowLayout**: Arranges components in rows with automatic wrapping
- **BorderLayout**: Arranges components in 5 regions (NORTH, SOUTH, EAST, WEST, CENTER)

### Layout Constraints
- **LayoutConstraint**: Interface for positioning hints to layouts
- **PositionConstraint**: Constraint for predefined positions (TOP_LEFT, CENTER, etc.) or absolute coordinates
- **SizeConstraint**: Constraint for sizing (FIXED, PERCENTAGE, FILL, PREFERRED)

### Border System
- **Border**: Interface for drawing borders around components
- **DefaultBorder**: Basic border with single-line characters
- **BorderStyle**: Enum defining different border styles (SINGLE, DOUBLE, THICK, ROUNDED, etc.)

### Focus Management System
- **Focus Support**: Canvas components can receive and manage focus
- **FocusManager**: Central focus management for ScreenCanvas with automatic traversal
- **Focus Navigation**: TAB/SHIFT+TAB for component navigation, arrow keys for directional focus
- **Focus Events**: `onFocusChanged(boolean)` callback for visual feedback

### Size Management and Pack System
- **Canvas Constraints**: minWidth/maxWidth and minHeight/maxHeight with automatic validation
- **Pack System**: `pack()` method for automatic size calculation based on content

### Process Loop and Event System
- **ProcessLoop**: Main processing loop for continuous rendering and non-blocking input handling
- **Event System**: Complete event-driven architecture with keyboard and mouse input processing
- **KeyEvent**: Comprehensive keyboard event handling with special keys, modifiers, and shortcuts
- **MouseEvent**: Complete mouse event handling with buttons, actions, coordinates
- **EventHandler Interface**: Components can implement to receive keyboard and mouse events

### Output Capture System
- **OutputCapture**: Thread-safe stdout/stderr stream redirection system
- **ConsoleOutput Canvas**: Specialized canvas for displaying captured console output with multiple display modes
- **ProcessLoop Integration**: Built-in output capture management

### Terminal Integration
- JLine 3.24.1 for terminal management
- ANSI color and formatting support
- Automatic terminal size detection
- Cross-platform terminal compatibility

### Demo Applications
- **ConsoleMasterDemo**: Basic framework demonstration
- **GraphicsDemo**: Graphics system showcase
- **LayoutDemo**: FlowLayout demonstration with modern components
- **BorderLayoutDemo**: BorderLayout with 5 regions
- **BoxDemo**: Border and Box component showcase
- **FocusDemo**: Focus management system demonstration
- **ProcessLoopDemo**: Interactive ProcessLoop system demonstration
- **OutputCaptureDemo**: Output capture system demonstration
- **MouseDemo**: Mouse event handling demonstration
- **TextDemo**: Text component features demonstration
- **ClippingGraphicsDemo**: Graphics clipping demonstration
- **BeautifulBorderDemo**: Advanced border styling showcase
- **BorderStyleDemo**: Different border styles demonstration
- **ScrollerDemo**: Scrollable content demonstration
- **ConsoleInputDemo**: Console input handling demonstration
- **NativeTerminalDemo**: Native terminal features demonstration
- **Graphic3DDemo**: 3D graphics system demonstration with various 3D objects and rendering modes

### 3D Graphics Usage Examples

```java
// Create a 3D canvas
Graphic3DCanvas canvas3D = new Graphic3DCanvas("3D Scene", 80, 40);

// Configure rendering mode
canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
canvas3D.setWireframeChar('*');
canvas3D.setWireframeColor(AnsiColor.CYAN);

// Position camera
canvas3D.getCamera().setPosition(new Point3D(0, 0, 5));
canvas3D.getCamera().lookAt(new Point3D(0, 0, 0));

// Create and add 3D objects
Mesh3D cube = Mesh3D.createCube(2.0);
canvas3D.addMesh(cube);

// Apply transformations
Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 4);
Mesh3D rotatedCube = cube.transform(rotation);
canvas3D.addMesh(rotatedCube);
```

### Development Guidelines

- Use unified `paint(Graphics graphics)` method for all components
- Leverage polymorphic Graphics design for maximum compatibility
- Implement EventHandler interface for interactive components
- Use pack() system for automatic size calculation
- Prefer composition over inheritance for complex components
- For 3D development, use the graphic3d module with proper depth testing and matrix transformations
- 3D objects should be created using Mesh3D and positioned using Matrix4x4 transformations
- Camera management should use Camera3D for proper view and projection setup
- Core framework development happens in the `core` module
- 3D graphics development happens in the `graphic3d` module with dependency on core
- Demo applications are developed in the `demo` module with dependency on core and graphic3d as needed
