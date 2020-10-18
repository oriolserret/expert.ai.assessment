package ai.expert.ws.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;

import ai.expert.commons.date.DateUtils;

public abstract class BaseController
{
	protected Logger log = LogManager.getLogger(this.getClass());

	protected String getGenericOkJsonResponse()
	{
		return "{\"resultCode\":0}";
	}

	protected Long parseLongParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException numberFormatException)
		{
			throw new Exception("Parameter '" + name + "' value is not a number. Received: " + value, numberFormatException);
		}
	}

	protected Integer parseIntegerParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException numberFormatException)
		{
			throw new Exception("Parameter '" + name + "' value is not a number. Received: " + value, numberFormatException);
		}
	}

	protected Double parseDoubleParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException numberFormatException)
		{
			throw new Exception("Parameter '" + name + "' value is not a number. Received: " + value, numberFormatException);
		}
	}

	protected Boolean parseBooleanParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		if ("true".equals(value.toLowerCase()) == false && "false".equals(value.toLowerCase()) == false)
			throw new Exception("Parameter '" + name + "' value is not a valid boolean value. Expected: true/false. Received: " + value);

		try
		{
			return Boolean.parseBoolean(value);
		}
		catch (NumberFormatException numberFormatException)
		{
			throw new Exception("Parameter '" + name + "' value is not a valid boolean value. Expected: true/false. Received: " + value, numberFormatException);
		}
	}

	protected Date parseDateParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		try
		{
			return DateUtils.stringDateToDate(value);
		}
		catch (IllegalArgumentException illegalArgumentException)
		{
			throw new Exception("Parameter '" + name + "' value is not a valid date value. Expected: dd/mm/yyyy. Received: " + value, illegalArgumentException);
		}
	}

	protected Date parseDateTimeParameter(String value, String name) throws Exception
	{
		if (value == null)
			return null;
		if (value.equals(""))
			return null;

		try
		{
			return DateUtils.stringDatetimeToDate(value);
		}
		catch (IllegalArgumentException illegalArgumentException)
		{
			throw new Exception("Parameter '" + name + "' value is not a valid date value. Expected: dd/mm/yyyy hh:mm:ss. Received: " + value, illegalArgumentException);
		}
	}

	protected void validateNotNullParameter(Object value, String name) throws Exception
	{
		if (value == null)
			throw new Exception("Parameter '" + name + "' must not be null");
	}

	protected String getStringParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value;

		value = httpServletRequest.getParameter(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		return value;
	}

	protected Long getLongParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value;

		value = httpServletRequest.getParameter(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		return this.parseLongParameter(value, name);
	}

	protected Integer getIntegerParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value;

		value = httpServletRequest.getParameter(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		return this.parseIntegerParameter(value, name);
	}

	protected Double getDoubleParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value;

		value = httpServletRequest.getParameter(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		return this.parseDoubleParameter(value, name);
	}

	protected Boolean getBooleanParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value;

		value = httpServletRequest.getParameter(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		return this.parseBooleanParameter(value, name);
	}

	protected Date getDateParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value = this.getStringParameter(httpServletRequest, name, mandatory);
		return this.parseDateParameter(value, name);
	}

	protected Date getDateTimeParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String value = this.getStringParameter(httpServletRequest, name, mandatory);
		return this.parseDateTimeParameter(value, name);
	}

	protected Collection<String> getStringCollectionParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String[] value;

		value = httpServletRequest.getParameterValues(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		if (value == null)
			return null;

		return Arrays.asList(value);
	}

	protected Collection<String> getStringCollectionParameterNoBlanks(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String[] values;

		values = httpServletRequest.getParameterValues(name);
		if (mandatory == true)
			validateNotNullParameter(values, name);

		if (values == null)
			return null;

		return toNonBlankCollection(values);
	}

	private Collection<String> toNonBlankCollection(String[] values)
	{
		ArrayList<String> nonBlankValues;

		nonBlankValues = new ArrayList<String>();
		for (String value : values)
		{
			if (!StringUtils.empty(value))
				nonBlankValues.add(value);
		}

		return nonBlankValues;
	}

	protected Collection<Integer> getIntegerCollectionParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String[] value;
		Collection<Integer> values;

		value = httpServletRequest.getParameterValues(name);
		if (mandatory == true)
			validateNotNullParameter(value, name);

		if (value == null)
			return null;

		values = new ArrayList<Integer>();
		for (String component : value)
		{
			if (StringUtils.empty(component) == false)
				values.add(Integer.parseInt(component));
		}

		return values;
	}

	protected Collection<Long> getLongCollectionParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		return getLongListParameter(httpServletRequest, name, mandatory);
	}

	protected List<Long> getLongListParameter(HttpServletRequest httpServletRequest, String name, boolean mandatory) throws Exception
	{
		String[] values;
		List<Long> paramValues;

		values = httpServletRequest.getParameterValues(name);

		if (mandatory == true)
			validateNotNullParameter(values, name);

		if (values == null)
			return null;

		paramValues = new ArrayList<Long>();
		for (String value : values)
			if (StringUtils.empty(value) == false)
				paramValues.add(parseLongParameter(value, name));

		return paramValues;
	}

	protected Map<String, String> getParametersByPrefix(String prefix, HttpServletRequest httpServletRequest)
	{
		Map<String, String> parameters;
		String paramValue;

		parameters = new HashMap<String, String>();
		for (Map.Entry<String, String[]> entry : httpServletRequest.getParameterMap().entrySet())
		{
			if (entry.getKey().startsWith(prefix))
			{
				paramValue = getFirstValue(entry.getValue());
				if (StringUtils.empty(paramValue) == false)
				{
					parameters.put(entry.getKey().substring(prefix.length()), paramValue);
				}
			}
		}

		return parameters;
	}

	private String getFirstValue(String[] values)
	{
		if (values == null)
			return null;

		if (values.length == 0)
			return null;

		return values[0];
	}

}
