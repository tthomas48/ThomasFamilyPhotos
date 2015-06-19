package com.github.tthomas48.thomasfamilyphotos.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by tthomas on 6/18/15.
 */
public class SmugMugImage implements Parcelable
{
	private boolean isGallery;
	private ImageLinks imageLinks;
	private String link;
	private String title;
	private Date exifDate;

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public ImageLinks getImageLinks()
	{
		return imageLinks;
	}

	public void setImageLinks(ImageLinks imageLinks)
	{
		this.imageLinks = imageLinks;
	}

	public SmugMugImage(boolean isGallery, String link, ImageLinks imageLinks, String title, Date exifDate) {
		this.isGallery = isGallery;
		this.link = link;
		this.imageLinks = imageLinks;
		this.title = title;
		this.exifDate = exifDate;
	}

	public boolean isGallery()
	{
		return isGallery;
	}

	public void setIsGallery(boolean isGallery)
	{
		this.isGallery = isGallery;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Date getExifDate()
	{
		return exifDate;
	}

	public void setExifDate(Date exifDate)
	{
		this.exifDate = exifDate;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeByte(isGallery ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.imageLinks, flags);
		dest.writeString(this.link);
		dest.writeString(this.title);
		dest.writeLong(exifDate != null ? exifDate.getTime() : -1);
	}

	protected SmugMugImage(Parcel in)
	{
		this.isGallery = in.readByte() != 0;
		this.imageLinks = in.readParcelable(ImageLinks.class.getClassLoader());
		this.link = in.readString();
		this.title = in.readString();
		long tmpExifDate = in.readLong();
		this.exifDate = tmpExifDate == -1 ? null : new Date(tmpExifDate);
	}

	public static final Parcelable.Creator<SmugMugImage> CREATOR = new Parcelable.Creator<SmugMugImage>()
	{
		public SmugMugImage createFromParcel(Parcel source)
		{
			return new SmugMugImage(source);
		}

		public SmugMugImage[] newArray(int size)
		{
			return new SmugMugImage[size];
		}
	};
}
