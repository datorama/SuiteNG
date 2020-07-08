/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.filters;

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
