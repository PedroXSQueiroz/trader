package br.com.pedroxsqueiroz.trader.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import lombok.extern.java.Log;

@Service
@Log
public class B3FBovespaQuotationPeriodicUpdater extends AbstractQuotationPeriodicUpdaters {

	@Autowired
	private QuotationsService quotationsService;
	
	@Value(value = "${b3bovespa.root_path}")
	private String b3BovespaRoot;
	
	@Value(value="${b3bovespa.quotations_days_earlier}")
	private Integer quotationsDaysEarlier;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private static int OFFSET = 0;
	private static int LIMIT = 1;
	
	private static Map<String, int[]> propertiesMappings = new HashMap<String, int[]>();
	
	@PostConstruct
	public void setupB3FBovespaPeriodicUpdater() throws IOException 
	{
		Resource quotationsConfig = this.resourceLoader.getResource("classpath:quotations.b3fbovespa.config.json");
		InputStream inputStream = quotationsConfig.getInputStream();
		
		ObjectMapper quotationsPropertiesMapper = new ObjectMapper();
		JsonNode mappingsJson = quotationsPropertiesMapper.readTree(inputStream);
		
		for(JsonNode currentMapping : mappingsJson) 
		{
			int[] positions = new int[2];
			positions[OFFSET] = currentMapping.get("offset").asInt(); 
			positions[LIMIT] = currentMapping.get("limit").asInt(); 
			
			String propertyName = currentMapping.get("name").asText();
			
			propertiesMappings.put( propertyName, positions );
		}
		
		
	}
	
	@Override
	protected List<QuotationModel> pullQuotations() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		LocalDate now = LocalDate.now();
		LocalDate quotationsDay = now.minusDays( this.quotationsDaysEarlier );
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
		String quotationsDayStr = quotationsDay.format(dateFormat);
		
		String quotationsURL = String.format("%s/%s/COTAHIST_D%s.zip", this.b3BovespaRoot, "InstDados/SerHist", quotationsDayStr);
		
		ResponseEntity<byte[]> b3BovespaResponse = restTemplate.exchange(quotationsURL , HttpMethod.GET, null, byte[].class);
		
		byte[] quotationsZipContent = b3BovespaResponse.getBody();
		
		ByteArrayInputStream quotationsContentStream = new ByteArrayInputStream(quotationsZipContent);
		ZipInputStream quotationsZipStream = new ZipInputStream(quotationsContentStream);
		
		try {
			
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
					QuotationModel currentQuotation = this.deserializeQuotation(currentQuotationContent);
					quotations.add(currentQuotation);
				}
				
			}
			
			return quotations;
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}

	private QuotationModel deserializeQuotation(String currentQuotationContent) {
		
		QuotationModel quotation = new QuotationModel();
		
		for(Entry<String, int[]> currentMapping : propertiesMappings.entrySet()) 
		{
			String propertyName = currentMapping.getKey();
			int[] mapping = currentMapping.getValue();
			
			String value = currentQuotationContent.substring(mapping[OFFSET], mapping[LIMIT]).trim();
			
			this.quotationsService.setPropertyValueToQuotation(propertyName, value, quotation);
		}
		
		return quotation;
	}

}
