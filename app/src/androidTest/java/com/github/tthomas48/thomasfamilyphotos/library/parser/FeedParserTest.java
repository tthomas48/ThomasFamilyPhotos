package com.github.tthomas48.thomasfamilyphotos.library.parser;

import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;

import junit.framework.TestCase;

import java.net.URL;
import java.util.List;

/**
 * Created by tthomas on 6/18/15.
 */
public class FeedParserTest extends TestCase
{

	public void testParser() throws Exception {
		FeedParser parser = new FeedParser();
		List<SmugMugImage> images = parser.parseGallery(new URL(FeedParser.GALLERY_TO_LOAD));

		assertNotNull(images);
		assertTrue(images.size() > 0);
		for (SmugMugImage image : images) {
			assertFalse(image.isGallery());
			assertNotNull(image.getTitle());
			assertNotNull(image.getLink());
			assertNotNull(image.getImageLinks());
			assertNotNull(image.getImageLinks().getOriginal());
			assertNotNull(image.getImageLinks().getThumbnail());
			assertNotNull(image.getExifDate());
		}
	}
}
