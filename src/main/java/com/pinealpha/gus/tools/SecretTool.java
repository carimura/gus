package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class SecretTool extends Tools {
    @Tool("Just returns our secret code - a single string. Nothing else.")
    public String secret() {
        preToolHook("secret", "");
        return "Gandalf";
    }
}
