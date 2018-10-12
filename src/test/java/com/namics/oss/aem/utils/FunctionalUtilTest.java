package com.namics.oss.aem.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionalUtilTest {

	@Test
	public void testAsStream() {
		final String test = "test";
		final Iterator<String> iterator1 = Arrays.asList(test, null).iterator();
		final List<String> nullCollection = FunctionalUtil.asStream(iterator1).collect(Collectors.toList());
		Assert.assertEquals(2, nullCollection.size());
		Assert.assertTrue(nullCollection.contains(test));

		final Iterator<String> iterator2 = Arrays.asList(test, null).iterator();
		final List<String> nonNullCollection = FunctionalUtil.asStream(iterator2).filter(Objects::nonNull).collect(Collectors.toList());
		Assert.assertEquals(1, nonNullCollection.size());
		Assert.assertTrue(nonNullCollection.contains(test));
	}
}