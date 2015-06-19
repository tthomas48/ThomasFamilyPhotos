package com.github.tthomas48.thomasfamilyphotos.adapter;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.tthomas48.thomasfamilyphotos.DetailActivity;
import com.github.tthomas48.thomasfamilyphotos.R;
import com.github.tthomas48.thomasfamilyphotos.library.SmugMugLibraryLoader;
import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tthomas on 6/18/15.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> implements LoaderManager.LoaderCallbacks<List<SmugMugImage>>
{
	private static final String TAG = "PhotoAdapter";
	private SmugMugLibraryLoader libraryLoader;
	private List<SmugMugImage> images;
	private Context context;
	private Picasso picasso;


	public PhotoAdapter(Context context)
	{
		this.context = context;
		this.libraryLoader = new SmugMugLibraryLoader(context);
		picasso = new Picasso.Builder(context)
				.build();
	}

	@Override
	public Loader<List<SmugMugImage>> onCreateLoader(int id, Bundle args)
	{
		return libraryLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<SmugMugImage>> loader, List<SmugMugImage> images)
	{
		this.images = images;
		notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<SmugMugImage>> loader)
	{
		libraryLoader.reset();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		// each data item is just a string in this case
		public Palette.Swatch swatch;
		public SmugMugImage smugMugImage;
		public ImageView imageView;
		public TextView imageLabel;
		public ProgressBar progressBar;

		public ViewHolder(View v)
		{
			super(v);

			imageView = (ImageView) v.findViewById(R.id.imageThumbnail);
			imageView.setOnClickListener(this);

			imageLabel = (TextView) v.findViewById(R.id.imageLabel);
			imageLabel.setOnClickListener(this);

			progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		}

		@Override
		public void onClick(View v)
		{

			if (v.getContext() instanceof Activity == false)
			{
				return;
			}
			Activity activity = (Activity) v.getContext();
			Intent intent = new Intent(activity, DetailActivity.class);
			intent.putExtra(DetailActivity.SMUGMUG_IMAGE, smugMugImage);
			intent.putExtra(DetailActivity.BACKGROUND_COLOR, ((ColorDrawable) imageLabel.getBackground()).getColor());
			intent.putExtra(DetailActivity.TEXT_COLOR, imageLabel.getCurrentTextColor());

			String transitionName = activity.getString(R.string.transition_image);

			ActivityOptionsCompat options =
					ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
							imageView,
							transitionName
					);
			ActivityCompat.startActivity(activity, intent, options.toBundle());
		}
	}

	@Override
	public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.thumbnail, parent, false);
		return new PhotoAdapter.ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final PhotoAdapter.ViewHolder holder, int position)
	{
		try
		{

			holder.progressBar.setVisibility(View.VISIBLE);

			SmugMugImage image = images.get(position);
			holder.smugMugImage = image;

			holder.imageLabel.setText(image.getTitle());

			picasso.load(images.get(position).getImageLinks().getLarge())
					.into(holder.imageView, new Callback()
					{
						@Override
						public void onSuccess()
						{

							Palette.generateAsync(((BitmapDrawable) holder.imageView.getDrawable()).getBitmap(),
									new Palette.PaletteAsyncListener()
									{
										@Override
										public void onGenerated(Palette palette)
										{
											Palette.Swatch vibrant =
													palette.getVibrantSwatch();
											if (vibrant != null)
											{
												holder.imageLabel.setBackgroundColor(
														vibrant.getRgb());
												holder.imageLabel.setTextColor(
														vibrant.getTitleTextColor());
											}
										}
									});
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onError()
						{
							// TODO: perhaps a broken image

						}
					});
		} catch (Exception e)
		{
			Log.e(TAG, "Error loading thumbnail", e);
		}
	}

	@Override
	public int getItemCount()
	{
		if (images == null)
		{
			return 0;
		}
		return images.size();
	}
}
