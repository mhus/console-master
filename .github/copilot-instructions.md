# Copilot Instructions for Java Development

## General Guidelines

- the Base Package is 'com.consolemaster'
- Use Java 21 or higher.
- Use english for all code, comments and documentation.

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

### Development Guidelines

- Use unified `paint(Graphics graphics)` method for all components
- Leverage polymorphic Graphics design for maximum compatibility
- Implement EventHandler interface for interactive components
- Use pack() system for automatic size calculation
- Prefer composition over inheritance for complex components
