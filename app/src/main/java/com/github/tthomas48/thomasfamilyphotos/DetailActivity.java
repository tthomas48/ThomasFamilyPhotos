package com.github.tthomas48.thomasfamilyphotos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tthomas48.thomasfamilyphotos.model.SmugMugImage;
import com.squareup.picasso.Picasso;

/**
 * Created by tthomas on 6/19/15.
 */
public class DetailActivity extends Activity
{
	public static final String SMUGMUG_IMAGE = "SmugMugImage";
	public static final String TEXT_COLOR = "color.text";
	public static final String BACKGROUND_COLOR = "color.background";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		setContentView(R.layout.activity_detail);


		SmugMugImage image = (SmugMugImage) getIntent().getParcelableExtra(SMUGMUG_IMAGE);
		int textColor = getIntent().getIntExtra(TEXT_COLOR, -1);
		int backgroundColor = getIntent().getIntExtra(BACKGROUND_COLOR, -1);

		LinearLayout detailBackground = (LinearLayout) this.findViewById(R.id.detailBackground);
		detailBackground.setBackgroundColor(backgroundColor);
		TextView imageLargeLabel = (TextView) this.findViewById(R.id.imageLargeLabel);
		imageLargeLabel.setTextColor(textColor);
		imageLargeLabel.setText(image.getTitle());

		TextView imageLargeDateTaken = (TextView) this.findViewById(R.id.imageLargeDateTaken);
		imageLargeDateTaken.setTextColor(textColor);
		imageLargeDateTaken.setText("Taken on " + image.getExifDate().toString());


		ImageView imageLarge = (ImageView) this.findViewById(R.id.imageLarge);
		Picasso.with(this).load(image.getImageLinks().getLarge())
				.into(imageLarge);
	}
}
