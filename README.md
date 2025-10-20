# Gus - Terminal Agent in Java

Gus is a terminal agent CLI written in Java that supports both OpenAI and Ollama models. Think of Gus as v0.01-alpha of Claude Code.

Gus is also my great pyrenees dog. He's friendly and needs a bath. Like this repo.


## TODO

- Read file tool
- Javadoc tool or Javadoc MCP
- getToolByName is a error-prong String, need to make strongly typed maybe with enum


## Modern Features
- Records
- Patterns
- Sealed classes (Tools)
- exhaustive switch (ChatService)
- text blocks (Gus.java)



## Prerequisites

- Java 25 (might work down to 21 but no promise)
- Maven
- Jpackage has only been tested on MacOS Sequioa
- OpenAI API key (for OpenAI mode) or Ollama running locally (for Ollama mode)

## Build

Build the project with all dependencies.

NOTE: To run on your machine, you'll need to modify the maven configuration to either a) *not* sign the app, or b) use your cert (on a mac).

```bash
mvn clean package
```

## Run

- Maven builds an executable using Jpackage, so you should be able to run like so:

```
❯ which gus
/users/you/bin/gus

❯ cat `which gus`
#!/bin/bash
/users/you/gus/target/jpackage/gus.app/Contents/MacOS/gus "$@"
```

### OpenAI Mode

Set your OpenAI API key as an environment variable:
```bash
export OPENAI_API_KEY=your-api-key-here
```

Run Gus with OpenAI:
```bash
java --enable-preview -cp target/gus-1.0-jar-with-dependencies.jar com.pinealpha.gus.Gus --openai
```

### Ollama Mode (Default)

Make sure Ollama is running locally on `http://localhost:11434`, then:

```bash
java --enable-preview -cp target/gus-1.0-jar-with-dependencies.jar com.pinealpha.gus.Gus --ollama
```

Or simply (since Ollama is the default):
```bash
java --enable-preview -cp target/gus-1.0-jar-with-dependencies.jar com.pinealpha.gus.Gus
```

## Commands

- `/exit` or `Ctrl+D` - Quit the application
- `/clear` - Clear chat memory
- `/help` - Show help text
