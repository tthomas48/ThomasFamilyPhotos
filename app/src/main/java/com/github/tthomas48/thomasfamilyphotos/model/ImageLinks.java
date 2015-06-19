package com.github.tthomas48.thomasfamilyphotos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tthomas on 6/18/15.
 */
public class ImageLinks implements Parcelable
{
	private String tiny;

	private String thumbnail;

	private String small;

	private String medium;

	private String large;

	private String extraLarge;

	private String extraExtraLarge;

	private String extraExtraExtraLarge;

	private String original;

	public String getExtraExtraExtraLarge()
	{
		return extraExtraExtraLarge;
	}

	public void setExtraExtraExtraLarge(String extraExtraExtraLarge)
	{
		this.extraExtraExtraLarge = extraExtraExtraLarge;
	}

	public String getExtraExtraLarge()
	{
		return extraExtraLarge;
	}

	public void setExtraExtraLarge(String extraExtraLarge)
	{
		this.extraExtraLarge = extraExtraLarge;
	}

	public String getExtraLarge()
	{
		return extraLarge;
	}

	public void setExtraLarge(String extraLarge)
	{
		this.extraLarge = extraLarge;
	}

	public String getLarge()
	{
		return large;
	}

	public void setLarge(String large)
	{
		this.large = large;
	}

	public String getMedium()
	{
		return medium;
	}

	public void setMedium(String medium)
	{
		this.medium = medium;
	}

	public String getSmall()
	{
		return small;
	}

	public void setSmall(String small)
	{
		this.small = small;
	}

	public String getTiny()
	{
		return tiny;
	}

	public void setTiny(String tiny)
	{
		this.tiny = tiny;
	}

	public String getThumbnail()
	{
		return thumbnail;
	}

	public void setThumbnail(String thumbnail)
	{
		this.thumbnail = thumbnail;
	}

	public String getOriginal()
	{
		return original;
	}

	public void setOriginal(String original)
	{
		this.original = original;
	}


	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(this.tiny);
		dest.writeString(this.thumbnail);
		dest.writeString(this.small);
		dest.writeString(this.medium);
		dest.writeString(this.large);
		dest.writeString(this.extraLarge);
		dest.writeString(this.extraExtraLarge);
		dest.writeString(this.extraExtraExtraLarge);
		dest.writeString(this.original);
	}

	public ImageLinks()
	{
	}

	protected ImageLinks(Parcel in)
	{
		this.tiny = in.readString();
		this.thumbnail = in.readString();
		this.small = in.readString();
		this.medium = in.readString();
		this.large = in.readString();
		this.extraLarge = in.readString();
		this.extraExtraLarge = in.readString();
		this.extraExtraExtraLarge = in.readString();
		this.original = in.readString();
	}

	public static final Parcelable.Creator<ImageLinks> CREATOR = new Parcelable.Creator<ImageLinks>()
	{
		public ImageLinks createFromParcel(Parcel source)
		{
			return new ImageLinks(source);
		}

		public ImageLinks[] newArray(int size)
		{
			return new ImageLinks[size];
		}
	};
}
