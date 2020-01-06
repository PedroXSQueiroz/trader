package br.com.pedroxsqueiroz.trader.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import lombok.extern.java.Log;

@Service
@Log
public class B3FBovespaQuotationPeriodicUpdater extends AbstractQuotationPeriodicUpdaters {
	
	
	private static final Integer REGISTER_TYPE_OFFSET = 0;
	private static final Integer REGISTER_TYPE_LIMIT = 2;
	private static final Integer INTERVAL_QUOTATIONS_DAYS = 1;
	private static LocalDate currentQuotationsPullDate;
	
	private static final String PROPERTIES_CONFIG_KEY = "propertiesConfig";
	private static final String LIMIT_PULL_DATE_KEY = "limitPullDate";
	
	private static LocalDate limitPullDate;

	private static final String HISTORIC_SERIES_REGISTER_TYPE = "01";
	
	private JsonNode configuration;
	
	@Autowired
	private QuotationsService quotationsService;
	
	@Autowired
	private QuotationsPropertiesValuesService quotationsPropertiesValuesService;
	
	@Value(value = "${b3bovespa.root_path}")
	private String b3BovespaRoot;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	
	@PostConstruct
	public void setupB3FBovespaPeriodicUpdater() throws IOException 
	{
		Resource quotationsConfig = this.resourceLoader.getResource("classpath:quotations.b3fbovespa.config.json");
		InputStream inputStream = quotationsConfig.getInputStream();
		
		ObjectMapper quotationsPropertiesMapper = new ObjectMapper();
		this.configuration = quotationsPropertiesMapper.readTree(inputStream);
		
		String limitPullDate = this.configuration.get(LIMIT_PULL_DATE_KEY).asText();
		this.limitPullDate = LocalDate.from( DateTimeFormatter.ISO_DATE.parse(limitPullDate) ); 
		
	}
	
	@Override
	protected List<QuotationModel> pullQuotations() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		String quotationsDayStr = this.getQuotationDay();
		
		String quotationsURL = String.format("%s/%s/COTAHIST_D%s.zip", this.b3BovespaRoot, "InstDados/SerHist", quotationsDayStr);
		
		this.log.info(String.format("getting %s.zip quotations batch", quotationsDayStr));
		
		try {
			
			ResponseEntity<byte[]> b3BovespaResponse = restTemplate.exchange(quotationsURL , HttpMethod.GET, null, byte[].class);
			
			byte[] quotationsZipContent = b3BovespaResponse.getBody();
			
			ByteArrayInputStream quotationsContentStream = new ByteArrayInputStream(quotationsZipContent);
			ZipInputStream quotationsZipStream = new ZipInputStream(quotationsContentStream);
			
			List<QuotationModel> quotations =  new ArrayList<QuotationModel>();
			ZipEntry currentFile = null;
			
			while((currentFile = quotationsZipStream.getNextEntry()) != null) 
			{
				ByteArrayOutputStream currentFileOut = new ByteArrayOutputStream();
				IOUtils.copy(quotationsZipStream, currentFileOut);
				
				String currentFileContent = currentFileOut.toString();
				
				String[] quotationsContent = currentFileContent.split("\n");
				
				for(String currentQuotationContent : quotationsContent) 
				{
					if ( this.isHistoricSeriesRegistry(currentQuotationContent) )
					{

						QuotationModel currentQuotation = this.deserializeQuotation(currentQuotationContent);
						quotations.add(currentQuotation);
						
					}
					
				}
				
			}
			
			return quotations;
		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch(HttpClientErrorException e) 
		{
			this.log.severe(String.format("fail on get quotations batch %s", quotationsDayStr));
			currentQuotationsPullDate = currentQuotationsPullDate.minusDays( INTERVAL_QUOTATIONS_DAYS );
		}
		
		return new ArrayList<QuotationModel>();
	}

	private String getQuotationDay() {
		
		if( currentQuotationsPullDate == null || currentQuotationsPullDate.isBefore( limitPullDate ) )
		{
			currentQuotationsPullDate = LocalDate.now();
		}
		
		List<Date> registeredDays = this
				.quotationsPropertiesValuesService
				.getValues("DTPREG")
				.fetch();
		
		while(registeredDays.contains(currentQuotationsPullDate)) 
		{
			currentQuotationsPullDate = currentQuotationsPullDate.minusDays( INTERVAL_QUOTATIONS_DAYS );
			
		}
		
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
		String quotationsDayStr = currentQuotationsPullDate.format(dateFormat);
		
		return quotationsDayStr;
	}

	private boolean isHistoricSeriesRegistry(String currentFileContent) {
		
		String registerType = currentFileContent.substring(
				REGISTER_TYPE_OFFSET, 
				REGISTER_TYPE_LIMIT);
		
		return registerType.equals(HISTORIC_SERIES_REGISTER_TYPE);
	}

	private QuotationModel deserializeQuotation(String currentQuotationContent) {
		
		QuotationModel quotation = new QuotationModel();
		
		JsonNode propertiesConfig = this.configuration.get(PROPERTIES_CONFIG_KEY);
		
		for(JsonNode currentPropertyConfig : propertiesConfig) 
		{
			int offset = currentPropertyConfig.get("offset").asInt();
			int limit = currentPropertyConfig.get("limit").asInt();
			String propertyName = currentPropertyConfig.get("name").asText();
			
			String value = currentQuotationContent.substring(offset, limit).trim();
			
			JsonNode dateFormatPattern = null;
			
			if( ( dateFormatPattern = currentPropertyConfig.get("dateFormat") ) != null) 
			{
				TemporalAccessor valueAsDate = DateTimeFormatter.ofPattern( dateFormatPattern.asText() ).parse( value );
				value = DateTimeFormatter.ISO_DATE.format( valueAsDate );
			}
			
			
			this.quotationsService.setPropertyValueToQuotation(propertyName, value, quotation);
		}
		
		return quotation;
	}

}
