package com.github.tthomas48.thomasfamilyphotos.library;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.github.tthomas48.thomasfamilyphotos.library.parser.FeedParser;
import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tthomas on 6/19/15.
 */
public class SmugMugLibraryLoader extends Loader<List<SmugMugImage>>
{
	private static final String TAG = "SmugMugLibraryLoader";
	private FeedParser parser;

	/**
	 * Stores away the application context associated with context. Since Loaders can be used
	 * across multiple activities it's dangerous to store the context directly.
	 *
	 * @param context used to retrieve the application context.
	 */
	public SmugMugLibraryLoader(Context context)
	{
		super(context);
	}

	@Override
	protected void onForceLoad()
	{
		load();
	}

	@Override
	protected void onStopLoading()
	{
		if (parser != null)
		{
			parser.stopLoading();
		}
	}

	@Override
	protected void onReset()
	{
		load();
	}

	@Override
	protected void onStartLoading()
	{
		load();
	}

	private void load() {

		new AsyncTask<String, ProgressBar, List<SmugMugImage>>() {
			@Override
			protected List<SmugMugImage> doInBackground(String... params)
			{
				List<SmugMugImage> images = new ArrayList<SmugMugImage>();
				try
				{
					Log.d(TAG, "Starting to parse gallery feed.");
					parser = new FeedParser();
					images.addAll(parser.parseGallery(new URL(FeedParser.GALLERY_TO_LOAD)));
					Log.d(TAG, "Done parsing gallery feed.");
				} catch(Exception e) {
					Log.e(TAG, "Unable to load and parse gallery.", e);
				}
				return images;
			}

			@Override
			protected void onPostExecute(List<SmugMugImage> smugMugImages)
			{
				SmugMugLibraryLoader.this.deliverResult(smugMugImages);
			}
		}.execute(FeedParser.GALLERY_TO_LOAD);
	}
}
