package com.merkle.oss.aem.utils.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link FileType} class.
 */
class FileTypeTest {

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link FileType#getMimeType()}
     *   <li>{@link FileType#getExtension()}
     *   <li>{@link FileType#toString()}
     * </ul>
     */
    @Test
    void gettersAndSetters() {
        FileType valueOfResult = FileType.valueOf("HTML");

        assertEquals("text/html", valueOfResult.getMimeType());
        assertEquals("html", valueOfResult.getExtension());
        assertEquals("html", valueOfResult.toString());
    }

    /**
     * Method under test: {@link FileType#toDotExtension()}
     */
    @Test
    void toDotExtension() {
        assertEquals(".html", FileType.HTML.toDotExtension());
    }

}
