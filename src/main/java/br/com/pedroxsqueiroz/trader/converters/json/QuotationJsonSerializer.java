package br.com.pedroxsqueiroz.trader.converters.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.com.pedroxsqueiroz.trader.constants.QuotationPropertyTypeEnum;
import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

public class QuotationJsonSerializer extends StdSerializer<QuotationModel> {

	public QuotationJsonSerializer() 
	{
		this(null);
	}
	
	public QuotationJsonSerializer(Class<QuotationModel> t) {
		super(t);
	}

	@Override
	public void serialize(
			QuotationModel value, 
			JsonGenerator gen, 
			SerializerProvider provider) throws IOException 
	{
		
		gen.writeStartObject();
		
		Integer id = value.getId();
		gen.writeNumberField("id", id);
		
		List<QuotationPropertyValueModel> propertiesValues = value.getPropertiesValues();
		for(QuotationPropertyValueModel propValue : propertiesValues ) 
		{
			
			QuotationPropertyModel currentProperty = propValue.getProperty();
			String currentPropertyName = currentProperty.getName();
			
			QuotationPropertyTypeEnum propertyType = currentProperty.getType();
			
			String currentValue = propValue.getValue();
			
			switch(propertyType) 
			{
				case FLOAT:
					
					gen.writeNumberField(currentPropertyName, Float.parseFloat(currentValue));				
					
					break;
				
				case DATE:
				
					//TODO: decidir como lidar formatações diferentes
					
				default:
				case STRING:
					
					gen.writeStringField(currentPropertyName, currentValue);
					
					break;
					
				
			}
			
			if(propertyType == QuotationPropertyTypeEnum.STRING) 
			{
				
			}
			
			
		};
		
		gen.writeEndObject();
		
	}
	
	

}
