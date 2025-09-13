# Copilot Instructions for Java Development

## General Guidelines

- the Base Package is 'com.consolemaster'
- Use Java 21 or higher.
- Use english for all code, comments and documentation.

## Libraries and Frameworks

- Use lombok for boilerplate code reduction.
- Use JUnit 5 for unit testing.
- Use Mockito for mocking dependencies in tests.
- Use jline for working with console input and output.

## Project

The project is a console framework to visualize game content in a console application.
The base class is a Canvas that contains text/graphics and can be rendered to the console.
The Canvas can use paint() method to draw content.
A Composite Canvas can contain multiple Canvases that can be rendered together.

The Screen Composite Canvas is the main entry point for the application.

There is a minimum size of the console. If the console is smaller than the minimum size, 
a warning Canvas is displayeed instead of the actual content.

## Current Framework Implementation Status

### Core Canvas System
- **Canvas**: Abstract base class for all drawable elements with position (x,y), size (width,height), visibility
- **CompositeCanvas**: Container for multiple child canvases with automatic layout management
- **ScreenCanvas**: Main entry point that manages terminal and minimum size validation
- **TextCanvas**: Simple text display implementation
- **Box**: Specialized canvas with border support and single child containment

### Graphics and Rendering
- **Graphics**: Legacy graphics context using char[][] buffer
- **JLineGraphics**: Enhanced graphics context using JLine's AttributedString for ANSI support
- **StyledChar**: Character with color and formatting information
- **AnsiColor**: Enum for ANSI foreground/background colors (standard + bright variants)
- **AnsiFormat**: Enum for ANSI text formatting (bold, italic, underline, etc.)

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
- **SimpleBorder**: Basic border with configurable characters and styling
- **ThickBorder**: Emphasized border using '#' characters

### Size Management
- Canvas supports minWidth/maxWidth and minHeight/maxHeight constraints
- Automatic size validation and clamping
- Layout-aware size calculations

### Terminal Integration
- JLine 3.24.1 for terminal management
- ANSI color and formatting support
- Automatic terminal size detection
- Cross-platform terminal compatibility

### Demo Applications
- **ConsoleMasterDemo**: Basic framework demonstration
- **JLineDemo**: ANSI styling showcase
- **LayoutDemo**: FlowLayout demonstration
- **BorderLayoutDemo**: BorderLayout with 5 regions
- **BoxDemo**: Border and Box component showcase

### Testing
- 40+ unit tests covering all major components
- Canvas size constraints and layout constraint integration
- Layout system functionality
- Mock-based testing for complex interactions

### Key Design Patterns
- Strategy Pattern: Layout and Border interfaces
- Composite Pattern: CompositeCanvas containing children
- Template Method: Canvas paint() methods with Graphics/JLineGraphics variants
- Builder Pattern: Various constraint creation methods

### Usage Examples
```java
// Basic setup
ScreenCanvas screen = new ScreenCanvas(80, 25);
CompositeCanvas main = new CompositeCanvas(0, 0, 80, 25, new BorderLayout());

// Add components with constraints
main.addChild(header, new PositionConstraint(Position.TOP_CENTER));
main.addChild(sidebar, new PositionConstraint(Position.CENTER_LEFT));

// Box with border
Box infoBox = new Box(10, 10, 30, 15, new SimpleBorder());
infoBox.setChild(new TextCanvas(0, 0, 0, 0, "Content"));

// Styling
graphics.setForegroundColor(AnsiColor.BRIGHT_GREEN);
graphics.setFormats(AnsiFormat.BOLD);
```

The framework is production-ready for console-based game development and UI applications.
