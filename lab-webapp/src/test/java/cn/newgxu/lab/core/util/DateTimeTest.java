/*
 * Copyright (c) 2001-2013 newgxu.cn <the original author or authors>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cn.newgxu.lab.core.util;

import static cn.newgxu.lab.core.util.DateTime.getRelativeTime;
import static java.util.Calendar.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-4-18
 * @version 0.1
 */
public class DateTimeTest {

	private static Logger L = LoggerFactory.getLogger(DateTimeTest.class);

	private Calendar now;
	private Calendar seconds;
	private Calendar minutes;
	private Calendar hours;
	private Calendar days;
	private Calendar months;
	private Calendar years;
	
	@Before
	public void init() {
		now = Calendar.getInstance();
		
		seconds = Calendar.getInstance();
		seconds.setTimeInMillis(now.getTimeInMillis() - 20000);
		
		minutes = Calendar.getInstance();
		minutes.set(now.get(YEAR),
			 now.get(MONTH), now.get(DATE),
			     now.get(HOUR_OF_DAY), now.get(MINUTE) - 23, now.get(SECOND));
		
		hours = Calendar.getInstance();
		hours.set(now.get(YEAR),
				now.get(MONTH), now.get(DATE),
				now.get(HOUR_OF_DAY) - 13, now.get(MINUTE), now.get(SECOND));
		
		days = Calendar.getInstance();
		days.set(now.get(YEAR),
				now.get(MONTH), now.get(DATE) - 3,
				now.get(HOUR_OF_DAY) - 13, now.get(MINUTE), now.get(SECOND));
		
		months =  Calendar.getInstance();
		months.set(now.get(YEAR),
				now.get(MONTH) - 7, now.get(DATE),
				now.get(HOUR_OF_DAY), now.get(MINUTE), now.get(SECOND));
		
		years = Calendar.getInstance();
		years.set(now.get(YEAR) - 43,
				now.get(MONTH), now.get(DATE),
				now.get(HOUR_OF_DAY) - 13, now.get(MINUTE), now.get(SECOND));
	}
	
	@Test
	public void testGetRelativeTime() {
		L.debug("now is: [}", now.getTime());
		assertThat(getRelativeTime(now.getTimeInMillis()).contains("刚"), is(true));
		assertThat(getRelativeTime(seconds.getTimeInMillis()).contains("秒"), is(true));
		assertThat(getRelativeTime(minutes.getTimeInMillis()).contains("分钟"), is(true));
		assertThat(getRelativeTime(hours.getTimeInMillis()).contains("小时"), is(true));
		assertThat(getRelativeTime(days.getTimeInMillis()).contains("天"), is(true));
		assertThat(getRelativeTime(months.getTimeInMillis()).contains("月"), is(true));
		assertThat(getRelativeTime(years.getTimeInMillis()).contains("年"), is(true));
	}

}
