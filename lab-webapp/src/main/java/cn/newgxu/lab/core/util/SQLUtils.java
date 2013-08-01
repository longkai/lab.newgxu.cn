/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */
package cn.newgxu.lab.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过方法调用来生成查询语句，这样比一味的使用封装要来得直接并且好用。
 * 可以直接将生成的查询语句返回给mybatis就好
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-7-30
 * @version 0.1.0.13-7-30
 */
public class SQLUtils {

	private static Pattern pattern = Pattern.compile("\\?");

	public static final String columns(String[] columns) {
		if (columns == null || columns.length == 0) {
			return "*";
		}

		StringBuilder seletion = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			seletion.append(columns[i]).append(",");
		}
		return seletion.substring(0, seletion.length() - 1);
	}

	public static final String where(String where, Object[] args) {
		if (where == null || where.length() == 0) {
			return null;
		}
		if (args == null || args.length == 0) {
			return where;
		}

		StringBuffer _where = new StringBuffer();
		Matcher matcher = pattern.matcher(where);
		for (int i = 0; matcher.find(); i++) {
			matcher.appendReplacement(_where, injectArg(args[i]));
		}
		matcher.appendTail(_where);
		return _where.toString();
	}

	public static final String injectArg(Object arg) {
		if (arg == null) {
			return "null";
		}

		if (arg instanceof Number) {
			return arg.toString();
		}

		if (arg instanceof Boolean) {
			boolean b = (Boolean) arg;
			return b ? "1" : "0";
		}

		StringBuilder value = new StringBuilder("'");
		if (arg instanceof Date) {
			Date d = (Date) arg;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(d.getTime());
			value.append(c.get(Calendar.YEAR)).append("-")
					.append(c.get(Calendar.MONTH) + 1).append("-")
					.append(c.get(Calendar.DATE)).append(" ")
					.append(c.get(Calendar.HOUR)).append(":")
					.append(c.get(Calendar.MINUTE)).append(":")
					.append(c.get(Calendar.SECOND));
		} else {
			value.append(arg.toString());
		}
		return value.append("'").toString();
	}

	public static final String query(String table, String[] columns, String where, Object[] args, String groupBy, String having, String orderBy, String limit) {
		StringBuilder query = new StringBuilder("SELECT ");

		query.append(columns(columns)).append(" FROM ").append(table);

		where = where(where, args);
		if (where != null) {
			query.append(" WHERE ").append(where);
		}

		if (groupBy != null) {
			query.append(" GROUP BY ").append(groupBy);
		}

		if (having != null) {
			query.append(" HAVING ").append(having);
		}

		if (orderBy != null) {
			query.append(" ORDER BY ").append(orderBy);
		}

		if (limit != null) {
			query.append(" LIMIT ").append(limit);
		}
		return query.toString();
	}

	public static final String set(String[] columns, Object[] values) {
		if (columns == null || values == null
				|| columns.length == 0 || columns.length != values.length) {
			throw new IllegalArgumentException("UPDATE columns and values NOT MATCHED or not set!");
		}

		StringBuilder set = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			set.append(columns[i]).append("=").append(injectArg(values[i])).append(",");
		}
		return set.substring(0, set.length() - 1);
	}

	public static final String update(String table, String[] columns, Object[] values, String where, Object[] args) {
		StringBuilder update = new StringBuilder("UPDATE ");
		update.append(table).append(" SET ").append(set(columns, values));

		where = where(where, args);
		if (where != null) {
			update.append(" WHERE ").append(where);
		}

		return update.toString();
	}

	public static final String delete(String table, String where, Object[] atrs) {
		where = where(where, atrs);
		if (where == null) {
			throw new SecurityException("NOT ALLOWED to delete the whole table!");
		}
		StringBuilder delete = new StringBuilder("DELETE FROM ");
		delete.append(table).append(" WHERE ").append(where);
		return delete.toString();
	}

	public static final String toSelectCount(String query) {
		String[] strs = query.split("(?i)FROM")[1].split("(?i)WHERE");

		if (strs.length > 2) {
			throw new IllegalArgumentException("INVALID SQL! --> " + query);
		}

		StringBuilder selection = new StringBuilder("SELECT COUNT(1) FROM ");

		selection.append(strs[0]);

//		has 'where' clause.
		if (strs.length == 2) {
			String where = strs[1].split("(?i)LIMIT")[0].split("(?i)ORDER BY")[0]
				.split("(?i)HAVING")[0].split("(?i)GROUP BY")[0];
			selection.append(" WHERE ").append(where);
		}
		return selection.toString();
	}

	public static final String selectCount(String table, String where, Object[] args) {
		StringBuilder selectCount = new StringBuilder("SELECT COUNT(1) FROM ").append(table);
		where = where(where, args);
		if (where != null) {
			selectCount.append(" WHERE ").append(where);
		}
		return selectCount.toString();
	}

	public static final String association(String tableAlias, String columnPrefix, String[] columns) {
		if (columns == null || columns.length == 0) {
			return "";
		}
//
		StringBuilder association = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			association.append(tableAlias).append(".").append(columns[i])
					.append(" as ").append(columnPrefix).append(columns[i]).append(",");
		}
		return association.toString();
	}
//  select A.xx a, B.xx b from t1 A where xx=xx join t2 B on A.id = B.id ...
//	select A.xx a, B.xx b from t1 A, t2 B where A.id = B.id ...
	public static final String query(String table,
	                          String[] associationTalbes, String[] alias,
	                          String[] columns, String[] associationColumns,
	                          String from, String where, Object[] args,
	                          String groupBy, String having,
	                          String orderBy, String limit) {
		StringBuilder query = new StringBuilder("SELECT ");

		return query.toString();
	}

}
