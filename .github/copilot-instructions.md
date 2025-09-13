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
- **CompositeCanvas**: Container for multiple child canvases with automatic layout management
- **ScreenCanvas**: Main entry point that manages terminal and minimum size validation
- **TextCanvas**: Simple text display implementation (legacy)
- **Text**: Modern text component with advanced styling and JLine integration
- **Box**: Specialized canvas with border support and single child containment

### Focus Management System
- **Focus Support**: Canvas components can receive and manage focus
- **FocusManager**: Central focus management for ScreenCanvas with automatic traversal
- **Focus Navigation**: 
  - `focusNext()` - Navigate to next focusable component (TAB functionality)
  - `focusPrevious()` - Navigate to previous focusable component (SHIFT+TAB functionality)
  - `focusFirst()` / `focusLast()` - Jump to first/last focusable component
- **Focus Events**: `onFocusChanged(boolean)` callback for visual feedback
- **Focus Properties**: 
  - `canFocus` flag to enable/disable focus capability
  - `hasFocus` flag indicating current focus state
- **Automatic Discovery**: Recursive detection of focusable components in canvas tree

### Size Management and Pack System
- **Canvas Constraints**: minWidth/maxWidth and minHeight/maxHeight with automatic validation
- **Pack System**: `pack()` method for automatic size calculation
  - **Canvas.pack()**: Base implementation (no-op, overrideable)
  - **CompositeCanvas.pack()**: Calculates minimum size based on children
  - **ScreenCanvas.pack()**: Updates screen requirements based on content
- **Dynamic Sizing**: Layouts and components automatically adapt to content requirements

### Graphics and Rendering (Refactored Architecture)
- **Graphics (Abstract Base Class)**: Unified abstract base class for all graphics contexts
  - Common interface for `drawChar()`, `drawString()`, `drawStyledString()`, `clear()`, etc.
  - Shared utility methods: `drawHorizontalLine()`, `drawVerticalLine()`, `drawRect()`, `fillRect()`
  - Abstract methods for styling: `setForegroundColor()`, `setBackgroundColor()`, `setFormats()`
- **JLineGraphics (extends Graphics)**: Enhanced graphics implementation using JLine's AttributedString
  - Superior ANSI support with AttributedStyle for rich text formatting
  - Polymorphic compatibility with base Graphics interface
  - Additional JLine-specific methods: `setStyle(AttributedStyle)`, `toAttributedString()`
  - Automatic AnsiColor to JLine color mapping
- **LegacyGraphics (extends Graphics)**: Backward-compatible implementation using StyledChar buffer
  - Maintains compatibility with existing char[][] based code
  - StyledChar buffer for color and formatting information
  - Legacy support methods: `toCharArray()`, `getStyledChar()`, `setStyledChar()`
- **Polymorphic Design**: Single `paint(Graphics graphics)` method works with both implementations
- **AnsiColor**: Enum for ANSI foreground/background colors (standard + bright variants)
- **AnsiFormat**: Enum for ANSI text formatting (bold, italic, underline, strikethrough, dim, reverse, blink)

### Output Capture System
- **OutputCapture**: Thread-safe stdout/stderr stream redirection system
  - Redirects `System.out` and `System.err` to internal buffers during ProcessLoop execution
  - Configurable line limits with automatic trimming (default: 1000 lines per stream)
  - Thread-safe access using `CopyOnWriteArrayList` and `ReadWriteLock`
  - Preserves access to original streams for emergency output
  - Automatic line processing with proper \n/\r handling
- **ConsoleOutput Canvas**: Specialized canvas for displaying captured console output
  - Multiple display modes: STDOUT_ONLY, STDERR_ONLY, BOTH (interleaved), SEPARATED
  - Configurable styling: colors, prefixes, line numbers
  - Scroll functionality: auto-scroll or manual scrolling with scroll indicators
  - Real-time display updates of captured output
- **ProcessLoop Integration**: Built-in output capture management
  - `setCaptureOutput(true)` enables stream redirection
  - `createConsoleOutputCanvas()` convenience method for output display
  - `clearCapturedOutput()` for runtime output clearing
  - Automatic cleanup on ProcessLoop termination

### Enhanced Canvas System
- **Unified Paint Method**: Single `paint(Graphics graphics)` method for all components
  - Polymorphic design eliminates need for separate JLine implementations
  - Automatic delegation: `paint(JLineGraphics)` calls `paint((Graphics)graphics)`
  - Simplified component development with reduced code duplication
- **Legacy Compatibility**: `paintLegacy(char[][])` method for backward compatibility
- **Border System Enhancement**: Updated to work with new Graphics hierarchy
  - Automatic LegacyGraphics wrapper creation for legacy border implementations
  - Seamless integration with both Graphics implementations

### Focus Management System
- **Focus Support**: Canvas components can receive and manage focus
- **FocusManager**: Central focus management for ScreenCanvas with automatic traversal
- **Focus Navigation**: 
  - `focusNext()` - Navigate to next focusable component (TAB functionality)
  - `focusPrevious()` - Navigate to previous focusable component (SHIFT+TAB functionality)
  - `focusFirst()` / `focusLast()` - Jump to first/last focusable component
- **Focus Events**: `onFocusChanged(boolean)` callback for visual feedback
- **Focus Properties**: 
  - `canFocus` flag to enable/disable focus capability
  - `hasFocus` flag indicating current focus state
- **Automatic Discovery**: Recursive detection of focusable components in canvas tree

### Size Management and Pack System
- **Canvas Constraints**: minWidth/maxWidth and minHeight/maxHeight with automatic validation
- **Pack System**: `pack()` method for automatic size calculation
  - **Canvas.pack()**: Base implementation (no-op, overrideable)
  - **CompositeCanvas.pack()**: Calculates minimum size based on children
  - **ScreenCanvas.pack()**: Updates screen requirements based on content
- **Dynamic Sizing**: Layouts and components automatically adapt to content requirements

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

### Terminal Integration
- JLine 3.24.1 for terminal management
- ANSI color and formatting support
- Automatic terminal size detection
- Cross-platform terminal compatibility

### Process Loop and Event System
- **ProcessLoop**: Main processing loop for continuous rendering and non-blocking input handling
- **Event System**: Complete event-driven architecture with keyboard and mouse input processing
- **Input Handler**: Non-blocking input processing in separate thread with terminal raw mode
- **Keyboard Events**: Full keyboard support including special keys, modifiers, and shortcuts
- **Mouse Events**: Complete mouse support including buttons, movement, drag, and wheel events
- **Event Chain**: Hierarchical event processing (Shortcuts → Built-in Navigation → Focused Canvas)

### Event Management
- **Event Interface**: Base event system with consumption tracking
- **KeyEvent**: Comprehensive keyboard event handling with:
  - Special keys (TAB, ENTER, ESC, Arrow keys, Function keys F1-F12)
  - Modifier keys (Shift, Ctrl, Alt) with combination support
  - String representation for shortcuts ("Ctrl+S", "Shift+TAB", "F1")
  - Event consumption to prevent further processing
- **MouseEvent**: Complete mouse event handling with:
  - Mouse buttons (LEFT, RIGHT, MIDDLE, NONE)
  - Mouse actions (PRESS, RELEASE, CLICK, DOUBLE_CLICK, MOVE, DRAG, WHEEL_UP, WHEEL_DOWN)
  - Mouse coordinates and modifier key support
  - Event consumption and timing-based double-click detection
- **EventHandler Interface**: Components can implement to receive keyboard and mouse events
- **Shortcut Registration**: Global shortcut system with customizable key combinations

### Mouse Management System
- **MouseManager Interface**: Strategy pattern for different mouse event handling approaches
- **DefaultMouseManager**: Canvas hierarchy integration with automatic event forwarding
- **Mouse Event Processing**: 
  - Canvas hit-testing to find target components at mouse coordinates
  - Automatic focus management on mouse clicks
  - Hover enter/leave events for visual feedback
  - Click and double-click detection with configurable thresholds
  - Drag and drop support with press/release tracking
- **Terminal Mouse Reporting**: ANSI escape sequence parsing for cross-platform mouse support

### ProcessLoop Features
- **Continuous Rendering**: Configurable FPS with performance monitoring
- **Non-blocking Input**: Separate thread for keyboard input processing
- **Terminal Integration**: Automatic terminal size detection and adaptation
- **Performance Metrics**: Real-time FPS counter and frame statistics
- **Lifecycle Management**: Start/stop with proper resource cleanup
- **Update/Render Callbacks**: Extensible hooks for application logic

### Demo Applications
- **ConsoleMasterDemo**: Basic framework demonstration
- **JLineDemo**: ANSI styling showcase
- **LayoutDemo**: FlowLayout demonstration with modern Box and Text components
- **BorderLayoutDemo**: BorderLayout with 5 regions using Box and Text components
- **BoxDemo**: Border and Box component showcase
- **FocusDemo**: Focus management system demonstration with interactive components
- **ProcessLoopDemo**: Interactive ProcessLoop system demonstration with real-time updates
- **OutputCaptureDemo**: Output capture system demonstration with stdout/stderr redirection

### Modern Graphics Development (Updated)
When creating new components, use the unified Graphics approach:

#### Unified Paint Method Implementation
```java
// Modern component development - single paint method works for all Graphics types
public class ModernComponent extends Canvas {
    @Override
    public void paint(Graphics graphics) {
        // This method works with both JLineGraphics and LegacyGraphics
        graphics.clear();
        graphics.setForegroundColor(AnsiColor.BRIGHT_CYAN);
        graphics.drawStyledString(0, 0, "Modern Component", AnsiColor.CYAN, null, AnsiFormat.BOLD);
        graphics.drawRect(0, 0, getWidth(), getHeight(), '#');
    }
    
    // No need for separate paint(JLineGraphics) implementation!
    // The framework automatically handles polymorphic dispatch
}
```

#### Output Capture Integration
```java
// ProcessLoop with output capture for debugging and logging visualization
ProcessLoop processLoop = new ProcessLoop(screen);
processLoop.setCaptureOutput(true); // Enable stdout/stderr capture

// Create split-screen layout with console output display
CompositeCanvas main = new CompositeCanvas(0, 0, 100, 30, new BorderLayout(1));

// Application content on the left
CompositeCanvas appContent = new CompositeCanvas(0, 0, 0, 0, new FlowLayout(2, 2));
appContent.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_LEFT));

// Console output display on the right
ConsoleOutput consoleOutput = processLoop.createConsoleOutputCanvas(0, 0, 40, 0);
consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.BOTH);
consoleOutput.setShowLineNumbers(true);
consoleOutput.setStdoutColor(AnsiColor.GREEN);
consoleOutput.setStderrColor(AnsiColor.BRIGHT_RED);

Box outputBox = new Box(0, 0, 0, 0, new SimpleBorder());
outputBox.setChild(consoleOutput);
outputBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_RIGHT));

main.addChild(appContent);
main.addChild(outputBox);

// Now all System.out.println() and System.err.println() calls are captured and displayed
System.out.println("This appears in the console output display!");
System.err.println("Errors are shown in red!");

// Interactive control shortcuts
screen.registerShortcut("c", processLoop::clearCapturedOutput);
screen.registerShortcut("1", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.STDOUT_ONLY));
screen.registerShortcut("2", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.STDERR_ONLY));
screen.registerShortcut("3", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.BOTH));
screen.registerShortcut("4", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.SEPARATED));
```

#### Advanced Graphics Usage
```java
// Leveraging the new Graphics hierarchy for maximum compatibility
public class AdvancedComponent extends Canvas {
    @Override
    public void paint(Graphics graphics) {
        graphics.clear();
        
        // Basic drawing operations work with any Graphics implementation
        graphics.drawString(0, 0, "Basic Text");
        graphics.fillRect(0, 2, getWidth(), 3, '=');
        
        // Styled text with automatic color mapping
        graphics.drawStyledString(0, 6, "Styled Text", 
            AnsiColor.BRIGHT_YELLOW, AnsiColor.BLUE, AnsiFormat.BOLD, AnsiFormat.UNDERLINE);
        
        // Use JLine-specific features when available
        if (graphics instanceof JLineGraphics jlineGraphics) {
            // Advanced JLine styling
            AttributedStyle customStyle = AttributedStyle.DEFAULT
                .foreground(AttributedStyle.CYAN)
                .background(AttributedStyle.BLACK)
                .bold();
            jlineGraphics.drawStyledString(0, 8, "JLine Enhanced", customStyle);
        }
        
        // Complex shapes using utility methods
        graphics.drawRectangle(2, 10, 20, 5, '*');
        graphics.drawHorizontalLine(0, getWidth()-1, 16, '-');
        graphics.drawVerticalLine(10, 0, getHeight()-1, '|');
    }
}
```

