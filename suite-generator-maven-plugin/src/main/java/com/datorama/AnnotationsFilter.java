package com.datorama;

import java.lang.annotation.Annotation;
import java.util.Map;

public class AnnotationsFilter implements Filter {

	private Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap; // contains annotations with attributes to filter

	AnnotationsFilter(Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap) {
		this.annotationsFilterMap = annotationsFilterMap;
	}

	public Map<Class<? extends Annotation>, Map<String, String>> getAnnotationsFilterMap() {
		return annotationsFilterMap;
	}

	public void setAnnotationsFilterMap(Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap) {
		this.annotationsFilterMap = annotationsFilterMap;
	}
}
