package com.siemens.scr.avt.ad.query.model;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface LoadingStrategy<T>{
	public void loadFromFile(T t, File file);
	public void loadFromFiles(T t, List<File> files);
	public void loadFromInputStream(T t, InputStream in);
	public void loadFromInputStreams(T t, List<InputStream> in);
	
	public void loadDefault(T t);
	
	public static interface JoinTreeLoadingStrategy<T extends JoinTree<?>> extends LoadingStrategy<T>{
	}
	
	public static interface MappingDictionaryLoadingStrategy<M extends MappingDictionary> extends LoadingStrategy<M>{
	}
}

