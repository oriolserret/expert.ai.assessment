package ai.expert.commons.json;

import java.io.IOException;
import java.util.Date;

import ai.expert.commons.date.DateUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonUtils
{
	public static String toJson(Object object) throws JsonProcessingException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(Date.class, new Json.DateSerializer());
		objectMapper.registerModule(module);

		return objectMapper.writeValueAsString(object);
	}

	public static Object fromJson(String json) throws JsonParseException, JsonMappingException, IOException
	{
		return (new ObjectMapper()).readValue(json, Object.class);
	}

	public static <T> T fromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Date.class, new DateDeserializer());
		objectMapper.registerModule(module);

		return objectMapper.readValue(json, clazz);
	}

	public static String throwableToJson(Throwable throwable)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (throwable.getMessage() == null)
			stringBuilder.append("{\"error\":\"").append(throwable.getClass().getCanonicalName().replace("\"", "\\\"").replace("\n", "<br/>")).append("\"}");
		else
			stringBuilder.append("{\"error\":\"").append(throwable.getMessage().replace("\"", "\\\"").replace("\n", "<br/>")).append("\"}");
		return stringBuilder.toString();
	}

	/* Custom Date serializer/deserializer */
	private static class DateSerializer extends StdSerializer<Date> implements ContextualSerializer
	{
		private static final long serialVersionUID = 2692772258332727980L;

		private JsonDateFormat.Format format;

		public DateSerializer()
		{
			this(JsonDateFormat.Format.DATETIME);
		}

		public DateSerializer(JsonDateFormat.Format format)
		{
			this(null, format);
		}

		protected DateSerializer(Class<Date> t, JsonDateFormat.Format format)
		{
			super(t);
			this.format = format;
		}

		@Override
		public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException
		{
			if (this.format.equals(JsonDateFormat.Format.DATE))
				jsonGenerator.writeString(DateUtils.formatDate(date));
			else if (this.format.equals(JsonDateFormat.Format.DATETIME))
				jsonGenerator.writeString(DateUtils.formatTime(date));
			else
				jsonGenerator.writeString(DateUtils.formatTimeWithMilis(date));
		}

		@Override
		public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException
		{
			JsonDateFormat dateType;

			if (beanProperty == null)
				return this;

			dateType = beanProperty.getAnnotation(JsonDateFormat.class);
			if (dateType == null)
				return this;

			return new Json.DateSerializer(dateType.format());
		}
	}

	private static class DateDeserializer extends StdDeserializer<Date> implements ContextualDeserializer
	{
		private static final long serialVersionUID = -9107212085773603556L;

		private JsonDateFormat.Format format;

		public DateDeserializer()
		{
			this(JsonDateFormat.Format.DATETIME);
		}

		public DateDeserializer(JsonDateFormat.Format format)
		{
			this(null, format);
		}

		protected DateDeserializer(Class<?> dateClass, JsonDateFormat.Format format)
		{
			super(dateClass);
			this.format = format;
		}

		@Override
		public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
		{
			if (this.format.equals(JsonDateFormat.Format.DATE))
				return DateUtils.stringDateToDate(jsonParser.getText());
			else if (this.format.equals(JsonDateFormat.Format.DATETIME))
				return DateUtils.stringDatetimeToDate(jsonParser.getText());
			else
				return DateUtils.stringDatetimeWithMilisToDate(jsonParser.getText());
		}

		@Override
		public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException
		{
			JsonDateFormat dateType;

			if (beanProperty == null)
				return this;

			dateType = beanProperty.getAnnotation(JsonDateFormat.class);
			if (dateType == null)
				return this;

			return new Json.DateDeserializer(dateType.format());
		}
	}
}
