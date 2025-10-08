package com.pinealpha.gus;

import dev.langchain4j.agent.tool.Tool;

public class Tools {

        @Tool("Calculates the length of a string")
        int stringLength(String s) {
            IO.println("[Calling tool stringLength() with s='" + s + "']");
            return s.length();
        }

        @Tool("Calculates the sum of two numbers")
        int add(int a, int b) {
            IO.println("[Calling tool add() with a=" + a + ", b=" + b + "]");
            return a + b;
        }

        @Tool("Calculates the square root of a number")
        double sqrt(int x) {
            IO.println("[Calling tool ssqrt() with x=" + x + "]");
            return Math.sqrt(x);
        }

        @Tool("Returns our secret code")
        String code() {
            IO.println("[Calling code tool]");
            return "Gandalf";
        }

}