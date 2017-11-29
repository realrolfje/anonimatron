package com.rolfje.anonimatron.file;

import java.io.File;
import java.io.FileFilter;

public class AcceptAllFilter implements FileFilter {

	private static int acceptcount = 0;

	@Override
	public boolean accept(File pathname) {
		acceptcount += 1;
		return true;
	}

	public static int getAcceptCount(){
		return acceptcount;
	}
}
