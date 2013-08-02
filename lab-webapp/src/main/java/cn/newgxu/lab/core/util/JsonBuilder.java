/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */
package cn.newgxu.lab.core.util;

import javax.json.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static cn.newgxu.lab.core.util.TextUtils.getter;

/**
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-8-2
 * @version 0.1.0.13-8-2
 */
public class JsonBuilder {

	public static final JsonObject toJsonObject(Object obj) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		addObject(builder, obj);
		return builder.build();
	}

	public static void addObject(JsonObjectBuilder builder, Object bean) {
		Class<?> c = bean.getClass();
		Field[] fields = c.getFields();
		for (Field f : fields) {
			String fieldName = f.getName();
			try {
				Object result = c.getDeclaredMethod(getter(fieldName)).invoke(bean);
				addValue(builder, fieldName, result);
			} catch (NoSuchMethodException e) {
				builder.addNull(fieldName);
			} catch (InvocationTargetException e) {
				builder.addNull(fieldName);
			} catch (IllegalAccessException e) {
				builder.addNull(fieldName);
			}
		}
	}

	private static void addValue(JsonObjectBuilder builder, String name, Object value) {
		if (value == null) {
			builder.addNull(name);
		} else if (value instanceof String) {
			builder.add(name, value.toString());
		} else if (value instanceof Long) {
			builder.add(name, (Long) value);
		} else if (value instanceof Boolean) {
			builder.add(name, (Boolean) value);
		} else if (value instanceof Integer) {
			builder.add(name, (Integer) value);
		} else if (value instanceof Double) {
			builder.add(name, (Double) value);
		} else if (value instanceof Float) {
			builder.add(name, (Float) value);
		} else if (value instanceof BigInteger) {
			builder.add(name, (BigInteger) value);
		} else if (value instanceof BigDecimal) {
			builder.add(name, (BigDecimal) value);
		} else if (value.getClass().getComponentType() != null) {
			addArray(builder, name, value);
		} else {
			addObject(builder, name);
		}
	}

	private static void addArray(JsonObjectBuilder builder, String name, Object value) {
		JsonArrayBuilder array = Json.createArrayBuilder();
		for (int i = 0; i < Array.getLength(value); i++) {
			Object obj = Array.get(array, i);
			if (obj == null) {
				array.addNull();
			} else if (obj instanceof String) {
				array.add(obj.toString());
			} else if (obj instanceof Long) {
				array.add((Long) obj);
			} else if (obj instanceof Boolean) {
				array.add((Boolean) obj);
			} else if (obj instanceof Integer) {
				array.add((Integer) obj);
			} else if (obj instanceof Double) {
				array.add((Double) obj);
			} else if (obj instanceof Float) {
				array.add((Float) obj);
			} else if (obj instanceof BigInteger) {
				array.add((BigInteger) obj);
			} else if (obj instanceof BigDecimal) {
				array.add((BigDecimal) obj);
			} else if (obj.getClass().getComponentType() != null) {
//              todo implement Multidimensional array.
			}
		}
		builder.add(name, array);
	}

	private JsonObjectBuilder builder;

	public JsonBuilder() {
		builder = Json.createObjectBuilder();
	}

	public JsonBuilder(JsonObjectBuilder builder) {
		this.builder = builder;
	}

	public JsonBuilder eager(String name, String vlaue) {
		if (vlaue == null)
			builder.addNull(name);
		else
			builder.add(name, vlaue);
		return this;
	}

	public JsonBuilder lazy(String name, String value) {
		if (value != null)
			builder.add(name, value);
		return this;
	}

	public JsonBuilder eager(String name, Date value) {
		if (value == null)
			builder.addNull(name);
		else
			builder.add(name, value.getTime());
		return this;
	}

	public JsonBuilder lazy(String name, Date value) {
		if (value != null)
			builder.add(name, value.getTime());
		return this;
	}

	public JsonBuilder eager(String name, JsonValue value) {
		if (value != null)
			builder.add(name, value);
		else
			builder.addNull(name);
		return this;
	}

	public JsonBuilder lazy(String name, JsonValue value) {
		if (value != null)
			builder.add(name, value);
		return this;
	}

	public JsonBuilder eager(String name, JsonObjectBuilder objectBuilder) {
		if (builder != null)
			builder.add(name, objectBuilder);
		else
			builder.addNull(name);
		return this;
	}

	public JsonBuilder lazy(String name, JsonObjectBuilder objectBuilder) {
		if (builder != null)
			builder.add(name, objectBuilder);
		return this;
	}

	public JsonBuilder eager(String name, JsonArrayBuilder arrayBuilder) {
		if (builder != null)
			builder.add(name, arrayBuilder);
		else
			builder.addNull(name);
		return this;
	}

	public JsonBuilder lazy(String name, JsonArrayBuilder arrayBuilder) {
		if (builder != null)
			builder.add(name, arrayBuilder);
		return this;
	}

	public JsonBuilder eager(String name, BigInteger value) {
		if (value != null)
			builder.add(name, value);
		else
			builder.addNull(name);
		return this;
	}

	public JsonBuilder lazy(String name, BigDecimal value) {
		if (value != null)
			builder.add(name, value);
		else
			builder.addNull(name);
		return this;
	}

	public JsonBuilder addNull(String name) {
		builder.addNull(name);
		return this;
	}

	public JsonBuilder add(String name, int value) {
		builder.add(name, value);
		return this;
	}

	public JsonBuilder add(String name, long value) {
		builder.add(name, value);
		return this;
	}

	public JsonBuilder add(String name, boolean value) {
		builder.add(name, value);
		return this;
	}

	public JsonBuilder add(String name, double value) {
		builder.add(name, value);
		return this;
	}

	public JsonObjectBuilder getInternalBuilder() {
		return builder;
	}

	public JsonObject build() {
		return builder.build();
	}

}
