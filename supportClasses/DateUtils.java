package ai.expert.commons.date;

import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
	public static String formatDate(Date date)
	{
		StringBuilder stringBuilder;
		Calendar calendar;

		if (date == null)
			return null;

		stringBuilder = new StringBuilder();
		calendar = Calendar.getInstance();
		calendar.setTime(date);

		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.DAY_OF_MONTH), 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MONTH) + 1, 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.YEAR), 4));

		return stringBuilder.toString();
	}

	public static String formatTime(Date date)
	{
		StringBuilder stringBuilder;
		Calendar calendar;

		if (date == null)
			return null;

		stringBuilder = new StringBuilder();
		calendar = Calendar.getInstance();
		calendar.setTime(date);

		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.DAY_OF_MONTH), 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MONTH) + 1, 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.YEAR), 4));
		stringBuilder.append(" ");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.HOUR_OF_DAY), 2));
		stringBuilder.append(":");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MINUTE), 2));
		stringBuilder.append(":");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.SECOND), 2));

		return stringBuilder.toString();
	}

	public static String formatTimeWithMilis(Date date)
	{
		StringBuilder stringBuilder;
		Calendar calendar;

		if (date == null)
			return null;

		stringBuilder = new StringBuilder();
		calendar = Calendar.getInstance();
		calendar.setTime(date);

		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.DAY_OF_MONTH), 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MONTH) + 1, 2));
		stringBuilder.append("/");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.YEAR), 4));
		stringBuilder.append(" ");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.HOUR_OF_DAY), 2));
		stringBuilder.append(":");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MINUTE), 2));
		stringBuilder.append(":");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.SECOND), 2));
		stringBuilder.append(".");
		stringBuilder.append(DateUtils.formatLeftZeroPadded(calendar.get(Calendar.MILLISECOND), 3));

		return stringBuilder.toString();
	}

	/*
	 * Parses a string with format "dd/mm/yyyy" to date
	 */
	public static Date stringDateToDate(String stringDate)
	{
		Calendar calendar;

		if (stringDate == null)
			return null;

		if (stringDate.length() != 10)
			throw new IllegalArgumentException("Incorrect date format: '" + stringDate + "'. Expected: 'dd/mm/yyyy'");

		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Integer.parseInt(stringDate.substring(6, 10)), Integer.parseInt(stringDate.substring(3, 5)) - 1, Integer.parseInt(stringDate.substring(0, 2)), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/*
	 * Parses a string with format "dd/mm/yyyy hh:mi:ss" to date
	 */
	public static Date stringDatetimeToDate(String stringDate)
	{
		Calendar calendar;

		if (stringDate == null)
			return null;

		if (stringDate.length() != 19)
			throw new IllegalArgumentException("Incorrect date format: '" + stringDate + "'. Expected: 'dd/mm/yyyy hh:mi:ss'");

		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Integer.parseInt(stringDate.substring(6, 10)), Integer.parseInt(stringDate.substring(3, 5)) - 1, Integer.parseInt(stringDate.substring(0, 2)), Integer.parseInt(stringDate.substring(11, 13)), Integer.parseInt(stringDate.substring(14, 16)), Integer.parseInt(stringDate.substring(17, 19)));
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/*
	 * Parses a string with format "dd/mm/yyyy hh:mi:ss.SSS" to date
	 */
	public static Date stringDatetimeWithMilisToDate(String stringDate)
	{
		Calendar calendar;

		if (stringDate == null)
			return null;

		if (stringDate.length() != 23)
			throw new IllegalArgumentException("Incorrect date format: '" + stringDate + "'. Expected: 'dd/mm/yyyy hh:mi:ss.SSS'");

		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Integer.parseInt(stringDate.substring(6, 10)), Integer.parseInt(stringDate.substring(3, 5)) - 1, Integer.parseInt(stringDate.substring(0, 2)), Integer.parseInt(stringDate.substring(11, 13)), Integer.parseInt(stringDate.substring(14, 16)), Integer.parseInt(stringDate.substring(17, 19)));
		calendar.set(Calendar.MILLISECOND, Integer.parseInt(stringDate.substring(20, 23)));

		return calendar.getTime();
	}

	private static String formatLeftZeroPadded(int number, int size)
	{
		StringBuilder stringBuilder;
		String numberAsString;

		numberAsString = String.valueOf(number);

		stringBuilder = new StringBuilder();
		while (numberAsString.length() < size--)
			stringBuilder.append("0");

		return stringBuilder.toString() + numberAsString;
	}

	public static Date clearTimeComponent(Date date)
	{
		Calendar calendar;

		calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static Date add(Date date, int field, int amount)
	{
		Calendar calendar;

		calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);

		return calendar.getTime();
	}

	public static void main(String[] args)
	{
		
	}
}
