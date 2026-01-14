package com.merkle.oss.aem.utils.constants;

import com.merkle.oss.aem.utils.constants.RunMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunModeTest {

    @Test
    void authorIsAuthor() {
        assertEquals("author", RunMode.Type.AUTHOR.getMode());
    }

    @Test
    void publishIsPublish() {

        assertEquals("publish", RunMode.Type.PUBLISH.getMode());
    }

}
