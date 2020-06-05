package farsight.testing.jbehave.steps;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import farsight.testing.jbehave.jbehave.InterceptPoint;

public class AopAdvice {
	
	public static enum Scope {
		GLOBAL, SESSION, USER
	}
	
	public static final Scope DEFAULT_SCOPE = Scope.SESSION;

	public static class Builder {
		private String adviceId = null;
		private InterceptPoint interceptPoint = InterceptPoint.invoke;
		private String service = null;
		private String condition = null;
		private String parent = null;
		private Scope scope = DEFAULT_SCOPE;
		private boolean noConditionQualifier = false;


		public Builder(String adviceId) {
			this.adviceId = adviceId.trim();
		}

		public AopAdvice build() {
			return new AopAdvice(adviceId, interceptPoint, service, condition, noConditionQualifier, parent, scope);
		}
		
		public Builder forService(String service) {
			this.service = service;
			return this;
		}

		public Builder when(String condition) {
			this.condition = condition;
			return this;
		}

		public Builder at(InterceptPoint interceptPoint) {
			this.interceptPoint = interceptPoint;
			return this;
		}

		public Builder noQualifier() {
			noConditionQualifier = true;
			return this;
		}
	}
	
	
	// Sections
	
	public static final String MOCK = "mock";
	public static final String CAPTURE = "capture";
	public static final String ASSERTION = "assertion";


	public final String adviceId;
	public final InterceptPoint interceptPoint;
	public final String service;
	public final String condition;
	public final boolean noConditionQualifier;
	public final String parent;
	public final Scope scope;

	public AopAdvice(String adviceId, InterceptPoint interceptPoint, String service, String condition, boolean noConditionQualifier, String parent, Scope scope) {
		this.adviceId = adviceId;
		this.interceptPoint = interceptPoint;
		this.service = service;
		this.condition = condition;
		this.noConditionQualifier = noConditionQualifier;
		this.parent = parent;
		this.scope = scope;
	}

	public static Builder create(String adviceId) {
		return new Builder(adviceId);
	}

	public boolean hasCondition() {
		return condition != null;
	}
	
	public String createQualifier(String potentialQualifier) {
		return (hasCondition() && !noConditionQualifier) ? potentialQualifier : null;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
