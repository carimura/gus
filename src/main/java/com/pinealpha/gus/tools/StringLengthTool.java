package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class StringLengthTool extends Tools {
    @Tool("Calculates the length of a string")
    public int stringLength(String s) {
        IO.println("[Calling tool stringLength() with s='" + s + "']");
        return s.length();
    }
}
