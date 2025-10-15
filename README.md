# Gus - AI CLI Assistant

Gus is a friendly neighborhood AI CLI assistant that supports both OpenAI and Ollama models.

## TODO

- Read file tool
- Javadoc tool or Javadoc MCP


## Prerequisites

- Java 25 (might work down to 21 but no promise)
- Maven
- Jpackage has only been tested on MacOS Sequioa
- OpenAI API key (for OpenAI mode) or Ollama running locally (for Ollama mode)

## Build

Build the project with all dependencies:

```bash
mvn -Pcomplete clean package
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
