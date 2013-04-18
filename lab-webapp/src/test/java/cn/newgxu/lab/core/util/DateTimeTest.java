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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-4-18
 * @version 0.1
 */
public class DateTimeTest {

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
		minutes.set(2013, Calendar.APRIL, 18, 20, 30, 0);
		
		hours = Calendar.getInstance();
		hours.set(2013, Calendar.APRIL, 18, 17, 00, 00);
		
		days = Calendar.getInstance();
		days.set(2013, Calendar.APRIL, 17, 20, 30, 0);
		
		months =  Calendar.getInstance();
		months.set(2013, Calendar.JANUARY, 19, 23, 1, 1);
		
		years = Calendar.getInstance();
		years.set(2010, Calendar.DECEMBER, 17, 00, 00, 00);
	}
	
	@Test
	public void testGetRelativeTime() {
		assertThat(DateTime.getRelativeTime(now.getTimeInMillis()).contains("刚"), is(true));
		assertThat(DateTime.getRelativeTime(seconds.getTimeInMillis()).contains("秒"), is(true));
		assertThat(DateTime.getRelativeTime(minutes.getTimeInMillis()).contains("分钟"), is(true));
		assertThat(DateTime.getRelativeTime(hours.getTimeInMillis()).contains("小时"), is(true));
		assertThat(DateTime.getRelativeTime(days.getTimeInMillis()).contains("天"), is(true));
		assertThat(DateTime.getRelativeTime(months.getTimeInMillis()).contains("月"), is(true));
		assertThat(DateTime.getRelativeTime(years.getTimeInMillis()).contains("年"), is(true));
	}

}
