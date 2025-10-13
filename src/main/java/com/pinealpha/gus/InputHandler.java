package com.pinealpha.gus;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.reader.Completer;
import org.jline.reader.Reference;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

class InputHandler {
    private final LineReader reader;
    private final Terminal terminal;

    InputHandler() throws IOException {
        terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        Completer completer = new StringsCompleter("/exit", "/clear", "/help");

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("gus")
                .completer(completer)
                .build();

        reader.getWidgets().put("slash-and-list", () -> {
            reader.getBuffer().write('/');
            reader.callWidget(LineReader.LIST_CHOICES);
            return true;
        });

        KeyMap<org.jline.reader.Binding> keyMap = reader.getKeyMaps().get(LineReader.MAIN);
        keyMap.bind(new Reference("slash-and-list"), "/");
    }

    String readLine(String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (EndOfFileException e) {
            return "/exit";
        } catch (UserInterruptException e) {
            return "/exit";
        }
    }

    void close() throws IOException {
        terminal.close();
    }
}
