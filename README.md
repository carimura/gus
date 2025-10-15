# Gus - AI CLI Assistant

Gus is a friendly neighborhood AI CLI assistant that supports both OpenAI and Ollama models.

## TODO

- Read file tool
- Javadoc tool or Javadoc MCP


## Prerequisites

- Java with preview features support
- Maven
- OpenAI API key (for OpenAI mode) or Ollama running locally (for Ollama mode)

## Build

Build the project with all dependencies:

```bash
mvn -Pcomplete clean package
```

## Run

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

## Notes

- OpenAI mode currently uses GPT-5 model (update to a valid model like `gpt-4o` if needed)
- Ollama mode uses the `gemma3` model by default
- Chat memory maintains the last 20 messages