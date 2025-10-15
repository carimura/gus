package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class StringLengthTool extends Tools {
    @Tool("Calculates the length of a string")
    public int stringLength(String s) {
        preToolHook("stringLength", "string='" + s + "'");
        return s.length();
    }
}
