package com.github.tthomas48.thomasfamilyphotos;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.tthomas48.thomasfamilyphotos.adapter.PhotoAdapter;
import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;

import java.util.List;


public class MainActivity extends Activity
{
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

		//recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
		//recyclerView.addItemDecoration(new MarginDecoration(this));
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		}
		recyclerView.setHasFixedSize(true);

		 //specify an adapter (see also next example)
		adapter = new PhotoAdapter(this);
		getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks<List<SmugMugImage>>) adapter);
		recyclerView.setAdapter(adapter);

	}
}
