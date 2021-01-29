package com.datorama.oss.filters;

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
	public boolean isFilterMatch(Method method) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		if (StringUtils.isEmpty(this.getClassName())) {
			if (StringUtils.isNotEmpty(this.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(this.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			}
		} else if (StringUtils.equalsIgnoreCase(this.getClassName(),method.getDeclaringClass().getCanonicalName())) {
			if (StringUtils.isNotEmpty(this.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(this.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			} else { // No method defined by the user in filter --> all methods in class will match
				isMatch.set(true);
			}
		}

		return isMatch.get();
	}
}

