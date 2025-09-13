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
