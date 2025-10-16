package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;

public final class SecretTool extends Tools {
    @Tool("Returns our secret code.")
    public String secret() {
        preToolHook("secret", "");
        return "Gandalf";
    }
}
