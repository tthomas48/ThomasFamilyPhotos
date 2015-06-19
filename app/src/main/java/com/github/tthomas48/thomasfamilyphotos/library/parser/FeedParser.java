package com.github.tthomas48.thomasfamilyphotos.library.parser;

import android.util.Xml;

import com.github.tthomas48.thomasfamilyphotos.model.ImageLinks;
import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tthomas on 6/18/15.
 */
public class FeedParser
{
	private static final String TAG = "FeedParser";
	public static final String GALLERY_TO_LOAD = "http://thomasfamily.smugmug.com/hack/feed.mg?Type=gallery&Data=48877814_pXHwQ9&format=rss200";
	private static final String RECENT_GALLERIES = "http://thomasfamily.smugmug.com/hack/feed.mg?Type=nickname&Data=thomasfamily&format=rss200&Size=Medium";
	public static final String MEDIA_NS = "http://search.yahoo.com/mrss/";
	private static final String EXIF_NS = "http://www.exif.org/specifications.html";
	private static final DateFormat exifDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private boolean loading;

	public void stopLoading()
	{
		loading = false;
	}

	public List<SmugMugImage> parseGalleries() throws XmlPullParserException, IOException, ParseException
	{
		return parse(RECENT_GALLERIES);
	}

	public List<SmugMugImage> parseGallery(URL url) throws XmlPullParserException, IOException, ParseException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try
		{
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			return parse(in);
		} finally
		{
			urlConnection.disconnect();
		}
	}

	public List<SmugMugImage> parseGallery(SmugMugImage image) throws XmlPullParserException, IOException, ParseException
	{

		if (!image.isGallery())
		{
			// TODO: I really don't want to throw a runtime exception
			throw new RuntimeException("Unable to parse images as galleries.");
		}
		URL url = new URL(image.getLink());

		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try
		{
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String rssFeed = parseRssFeed(in);
			if (rssFeed == null)
			{
				return null;
			}
			return parse(rssFeed);
		} finally
		{
			urlConnection.disconnect();
		}
		// hmm... we actually have to get this out of the feed.

	}

	private String parseRssFeed(InputStream in) throws XmlPullParserException, IOException, ParseException
	{
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature(Xml.FEATURE_RELAXED, true);

			XmlPullParser parser = factory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readHtml(parser);
		} finally
		{
			in.close();
		}
	}

	private String readHtml(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException
	{
		parser.require(XmlPullParser.START_TAG, "", "html");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("link") && "application/rss+xml".equals(parser.getAttributeValue("", "type")))
			{
				return parser.getAttributeValue("", "href");
			} else
			{
				skip(parser);
			}
		}
		return null;
	}


	private List<SmugMugImage> parse(String path) throws XmlPullParserException, IOException, ParseException
	{

		URL url = new URL(path);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try
		{
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			return parse(in);
		} finally
		{
			urlConnection.disconnect();
		}
	}

	private List<SmugMugImage> parse(InputStream in) throws XmlPullParserException, IOException, ParseException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			parser.setInput(in, null);
			parser.nextTag();

			loading = true;
			return readFeed(parser);
		} finally
		{
			in.close();
		}
	}

	private List<SmugMugImage> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException
	{
		parser.require(XmlPullParser.START_TAG, "", "rss");
		while (loading && parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("channel"))
			{
				return readChannel(parser);
			} else
			{
				skip(parser);
			}
		}
		return null;
	}

	private List<SmugMugImage> readChannel(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException
	{
		List<SmugMugImage> images = new ArrayList<SmugMugImage>();
		while (loading && parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("item"))
			{
				images.add(readItem(parser));
			} else
			{
				skip(parser);
			}
		}
		return images;
	}

	private SmugMugImage readItem(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException
	{
		parser.require(XmlPullParser.START_TAG, "", "item");
		String title = null;
		String link = null;
		ImageLinks imageLinks = null;
		Date exifDate = null;
		while (loading && (parser.next() != XmlPullParser.END_TAG || "item".equals(parser.getName()) == false))
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			if (name.equals("title") && "".equals(parser.getNamespace()))
			{
				title = readTag(parser, "", "title");
			} else if (name.equals("link") && "".equals(parser.getNamespace()))
			{
				link = readTag(parser, "", "link");
			} else if (name.equals("group") && MEDIA_NS.equals(parser.getNamespace()))
			{
				if (imageLinks == null)
				{
					imageLinks = new ImageLinks();
				}
				readImage(parser, imageLinks);
			} else if (name.equals("DateTimeOriginal") && EXIF_NS.equals(parser.getNamespace()))
			{

				String dateString = readTag(parser, EXIF_NS, "DateTimeOriginal");
				exifDate = exifDateFormat.parse(dateString);
			} else
			{
				skip(parser);
			}
		}
		return new SmugMugImage(imageLinks == null, link, imageLinks, title, exifDate);
	}

	private void readImage(XmlPullParser parser, ImageLinks imageLinks) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, MEDIA_NS, "group");
		while (loading && (parser.next() != XmlPullParser.END_TAG || "group".equals(parser.getName()) == false))
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			if (name.equals("content"))
			{
				String url = parser.getAttributeValue("", "url");
				if (url == null)
				{
					continue;
				}
				if (url.endsWith("-Ti.jpg"))
				{
					imageLinks.setTiny(url);
				} else if (url.endsWith("-Th.jpg"))
				{
					imageLinks.setThumbnail(url);
				} else if (url.endsWith("-S.jpg"))
				{
					imageLinks.setSmall(url);
				} else if (url.endsWith("-M.jpg"))
				{
					imageLinks.setMedium(url);
				} else if (url.endsWith("-L.jpg"))
				{
					imageLinks.setLarge(url);
				} else if (url.endsWith("-XL.jpg"))
				{
					imageLinks.setExtraLarge(url);
				} else if (url.endsWith("-X2.jpg"))
				{
					imageLinks.setExtraExtraLarge(url);
				} else if (url.endsWith("-X3.jpg"))
				{
					imageLinks.setExtraExtraExtraLarge(url);
				} else
				{
					imageLinks.setOriginal(url);
				}

			} else
			{
				skip(parser);
			}
		}
	}

	private void readThumbnail(XmlPullParser parser, ImageLinks imageLinks) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, MEDIA_NS, "thumbnail");
		imageLinks.setThumbnail(parser.getAttributeValue("", "url"));
	}

	private String readTag(XmlPullParser parser, String namespace, String tagName) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, namespace, tagName);
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, tagName);
		return title;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		String result = "";
		if (parser.next() == XmlPullParser.TEXT)
		{
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if (parser.getEventType() != XmlPullParser.START_TAG)
		{
			throw new IllegalStateException();
		}
		int depth = 1;
		while (loading && depth != 0)
		{
			switch (parser.next())
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}
