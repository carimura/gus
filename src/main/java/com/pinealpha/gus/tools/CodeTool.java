package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class CodeTool extends Tools {
    @Tool("Returns our secret code.")
    public String code() {
        IO.println("[Calling code tool]");
        return "Gandalf";
    }
}
