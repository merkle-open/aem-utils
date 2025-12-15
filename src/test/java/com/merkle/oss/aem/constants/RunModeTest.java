package com.merkle.oss.aem.constants;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RunModeTest {

    @Test
    void authorIsAuthor() {
        assertThat(RunMode.Type.AUTHOR.getMode(), equalTo("author"));
    }

    @Test
    void publishIsPublish() {

        assertThat(RunMode.Type.PUBLISH.getMode(), equalTo("publish"));
    }

}
