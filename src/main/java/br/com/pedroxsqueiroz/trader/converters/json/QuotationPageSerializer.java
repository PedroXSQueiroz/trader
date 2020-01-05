package br.com.pedroxsqueiroz.trader.converters.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

@JsonComponent
public class QuotationPageSerializer extends JsonSerializer<Page<QuotationModel>>{

	@Override
	public void serialize(
			Page value, 
			JsonGenerator gen, 
			SerializerProvider serializers) throws IOException {
		
		ObjectMapper defaultSerializer = new ObjectMapper();
		JsonNode tree = defaultSerializer.valueToTree(value);
		
		
		List<QuotationModel> quotations = value.getContent();
		
		List<QuotationPropertyModel> properties = quotations
													.stream()
													.flatMap( quotation -> 
																quotation
																.getPropertiesValues()
																.stream()
																.map( val -> 
																	val.getProperty() ) )
													.distinct()
													.collect(Collectors.toList());
		
		((ObjectNode) tree).put("contentProperties", defaultSerializer.valueToTree(properties));
		
		gen.writeObject(tree);
	}

}
