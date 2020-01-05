package br.com.pedroxsqueiroz.trader.converters.json;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.com.pedroxsqueiroz.trader.constants.QuotationPropertyTypeEnum;
import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

@JsonComponent
public class QuotationJsonSerializer extends JsonSerializer<QuotationModel> {

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
					
					float currentValuFloatParsed = currentValue.isEmpty() ? 0 : Float.parseFloat(currentValue);
					gen.writeNumberField(currentPropertyName, currentValuFloatParsed);				
					
					break;
				
				case DATE:
				
					//TODO: decidir como lidar formatações diferentes
					
				default:
				case STRING:
					
					gen.writeStringField(currentPropertyName, currentValue);
					
					break;
					
				
			}
		};
		
		gen.writeEndObject();
		
	}
	
	

}
