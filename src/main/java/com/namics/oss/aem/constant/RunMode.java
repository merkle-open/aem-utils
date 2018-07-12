package com.namics.oss.aem.constant;

/**
 * Constant class with enumerations for specific run mode
 * categories.
 */
public class RunMode {

    /**
     * Basic enum category to differentiate
     * between the basic modes author
     * and publish.
     */
    public enum Type implements Mode {
        AUTHOR("author"),
        PUBLISH("publish");

        private final String mode;

        Type(String mode) {
            this.mode = mode;
        }

        @Override
        public String getMode() {
            return mode;
        }
    }

    /**
     * Generic interface to get the string representation
     * of a run mode from.
     * <p>
     * Every run mode category should implement this interface.
     */
    public interface Mode {

        String getMode();

    }

}
