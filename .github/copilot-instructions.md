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
- Observer Pattern: Focus change notifications

### Modern Component Development
When creating new components, prefer the modern approach:

#### Text Component Usage
```java
// Modern Text component with styling
Text styledText = new Text(0, 0, width, height, "Content", Text.Alignment.CENTER);
styledText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
styledText.setBold(true);
styledText.setBackgroundColor(AnsiColor.BLUE);
```

#### Box Component with Focus
```java
// Box with focus capability and visual feedback
Box focusableBox = new Box(x, y, width, height, new SimpleBorder()) {
    @Override
    protected void onFocusChanged(boolean focused) {
        super.onFocusChanged(focused);
        Text content = (Text) getChild();
        if (content != null) {
            if (focused) {
                content.setBackgroundColor(AnsiColor.BLUE);
                content.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            } else {
                content.setBackgroundColor(null);
                content.setForegroundColor(AnsiColor.WHITE);
            }
        }
    }
};
focusableBox.setCanFocus(true);
```

#### Layout Integration
```java
// BorderLayout with modern components
CompositeCanvas main = new CompositeCanvas(0, 0, width, height, new BorderLayout(1));

// Header
Box header = new Box(0, 0, 0, 3, new SimpleBorder());
header.setChild(new Text(0, 0, 0, 0, "Header", Text.Alignment.CENTER));
header.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

// Content with FlowLayout
CompositeCanvas content = new CompositeCanvas(0, 0, 0, 0, new FlowLayout(2, 2));
content.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

main.addChild(header);
main.addChild(content);
```

#### Focus Management Integration
```java
// Setup focus management
ScreenCanvas screen = new ScreenCanvas(80, 25);
screen.setContentCanvas(mainContent);

// Focus navigation
screen.focusFirst();        // Set initial focus
screen.focusNext();         // TAB functionality
screen.focusPrevious();     // SHIFT+TAB functionality

// Automatic size calculation
screen.pack();              // Recalculate minimum sizes
```

#### ProcessLoop Integration
```java
// ProcessLoop setup with event handling and continuous rendering
ScreenCanvas screen = new ScreenCanvas(80, 25);
ProcessLoop processLoop = new ProcessLoop(screen);
processLoop.setTargetFPS(60);

// Register custom shortcuts
screen.registerShortcut("Ctrl+S", () -> saveGame());
screen.registerShortcut("F1", () -> showHelp());
screen.registerShortcut("SPACE", () -> performAction());

// Update callback for continuous logic updates
processLoop.setUpdateCallback(() -> {
    updateGameState();
    updateAnimations();
});

// Render callback for custom rendering
processLoop.setRenderCallback(() -> {
    drawCustomOverlays();
});

// Start the process loop (blocks until stopped)
processLoop.start();
```

### Event-Driven Component Development
```java
// Component with custom event handling
Box interactiveBox = new Box(x, y, width, height, new SimpleBorder()) {
    @Override
    protected void onFocusChanged(boolean focused) {
        super.onFocusChanged(focused);
        updateVisualState(focused);
    }
} implements EventHandler {
    @Override
    public void handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.isSpecialKey() && 
                keyEvent.getSpecialKey() == KeyEvent.SpecialKey.ENTER) {
                performAction();
                keyEvent.consume(); // Prevent further processing
            }
        }
    }
};
interactiveBox.setCanFocus(true);
```

#### Keyboard Event Processing
```java
// Custom keyboard event handling
screen.registerShortcut("Ctrl+Q", processLoop::stop);
screen.registerShortcut("Shift+TAB", () -> screen.focusPrevious());
screen.registerShortcut("F5", () -> processLoop.requestRedraw());

// KeyEvent properties and methods
KeyEvent keyEvent = // ... received from input
String keyString = keyEvent.getKeyString(); // "Ctrl+S", "TAB", "F1"
boolean hasModifier = keyEvent.hasModifier(KeyEvent.Modifier.CTRL);
boolean isSpecial = keyEvent.isSpecialKey();
boolean isChar = keyEvent.isCharacter();
```

### ProcessLoop Lifecycle Management
```java
// Synchronous ProcessLoop (blocks current thread)
ProcessLoop processLoop = new ProcessLoop(screen);
processLoop.start(); // Blocks until stopped

// Asynchronous ProcessLoop (runs in separate thread)
Thread processThread = processLoop.startAsync();
// ... do other work
processLoop.stop(); // Stop the loop
processThread.join(); // Wait for cleanup

// Convenience methods
ProcessLoop processLoop = ProcessLoop.createAndStart(screen, updateCallback);
ProcessLoop asyncLoop = ProcessLoop.createAndStartAsync(screen, updateCallback);
```

### Advanced Event System Features
```java
// Event consumption chain
screen.processKeyEvent(keyEvent); // Processes through:
// 1. Registered shortcuts (screen.registerShortcut())
// 2. Built-in navigation (TAB/SHIFT+TAB for focus)
// 3. Focused canvas event handler (EventHandler.handleEvent())

// Global event monitoring
processLoop.setUpdateCallback(() -> {
    // Check for custom conditions
    if (shouldShowNotification()) {
        processLoop.requestRedraw();
    }
});

// Performance monitoring
int currentFPS = processLoop.getCurrentFPS();
long totalFrames = processLoop.getFrameCount();
boolean isRunning = processLoop.isRunning();
```

### Terminal and Display Management
```java
// Automatic terminal size handling
processLoop.checkTerminalSizeChange(); // Built-in, called automatically
screen.updateSize(); // Updates canvas dimensions
screen.pack(); // Recalculates layout requirements

// Manual redraw requests
processLoop.requestRedraw(); // Forces render on next frame
processLoop.setTargetFPS(30); // Adjusts rendering frequency
processLoop.setLimitFrameRate(false); // Unlimited rendering
```

### Best Practices
- Use modern Text component instead of legacy TextCanvas
- Implement focus support for interactive components
- Use pack() after content changes for optimal sizing
- Combine BorderLayout and FlowLayout for complex layouts
- Provide visual feedback for focus changes
- Use PositionConstraint for layout positioning
- Leverage automatic canvas discovery for focus management
- **Use ProcessLoop for interactive applications requiring continuous updates**
- **Implement EventHandler interface for components needing keyboard input**
- **Register global shortcuts at the ScreenCanvas level for application-wide commands**
- **Use event consumption to control event propagation through the component hierarchy**
- **Monitor performance metrics for optimization in complex applications**

### Production-Ready Features
The framework is production-ready for console-based development with:
- **Interactive Console Games**: Real-time input, continuous rendering, focus management
- **Terminal User Interfaces (TUI)**: Professional UI components with keyboard navigation
- **Development Tools**: Interactive dashboards, file browsers, text editors
- **System Monitoring Applications**: Real-time data display with user interaction
- **Educational Software**: Interactive tutorials and learning applications

### Framework Architecture Summary
The Console Master framework provides a complete solution for modern console application development:
- **Component-Based Architecture**: Modular canvas system with hierarchical composition
- **Event-Driven Design**: Complete keyboard input processing with customizable shortcuts
- **Real-Time Rendering**: Continuous display updates with performance optimization
- **Focus Management**: Professional keyboard navigation with visual feedback
- **Layout System**: Automatic component arrangement with flexible constraints
- **Modern Styling**: ANSI color support with rich text formatting
- **Cross-Platform**: JLine integration for terminal compatibility

The framework is production-ready for console-based game development and interactive UI applications with full focus management, automatic sizing capabilities, and professional-grade event processing.
