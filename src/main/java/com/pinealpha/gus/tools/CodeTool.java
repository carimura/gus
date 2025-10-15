package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class CodeTool extends Tools {
    @Tool("Returns our secret code.")
    public String code() {
        preToolHook("code", "");
        return "Gandalf";
    }
}
