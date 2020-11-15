package com.salesforce.filters;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

public class MethodsFilter implements Filter {

	private String className;
	private String methodName;

	public MethodsFilter(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public boolean isFilterMatch(Method method, Filter filter) {

		AtomicBoolean isMatch = new AtomicBoolean(false);
		MethodsFilter methodsFilter = (MethodsFilter) filter;

		if (StringUtils.isEmpty(methodsFilter.getClassName())) {
			if (StringUtils.isNotEmpty(methodsFilter.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(methodsFilter.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			}
		} else if (StringUtils.equalsIgnoreCase(methodsFilter.getClassName(),method.getDeclaringClass().getCanonicalName())) {
			if (StringUtils.isNotEmpty(methodsFilter.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(methodsFilter.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			} else { // No method defined by the user in filter --> all methods in class will match
				isMatch.set(true);
			}
		}

		return isMatch.get();
	}
}

