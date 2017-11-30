package com.bjghhnt.app.treatmentdevice.utils.models;

import java.io.File;

/**
 * This is the model class for video information.
 * Created by Q on 17/02/2016.
 */
public class VideoInfo {

	private final String name;

	private final String absolutePath;

	private int lastPosition;

	public VideoInfo(File file) {
		this.lastPosition = 0;
		this.name = file.getName();
		this.absolutePath = file.getAbsolutePath();
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(int lastPosition) {
		this.lastPosition = lastPosition;
	}

	public String getName() {
		return name;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

}
