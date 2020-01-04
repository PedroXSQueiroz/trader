package br.com.pedroxsqueiroz.trader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.services.AbstractQuotationPeriodicUpdaters;
import br.com.pedroxsqueiroz.trader.services.QuotationsPropertiesService;
import lombok.extern.java.Log;

@SpringBootApplication
@EnableScheduling
public class TraderApplication {

	private static boolean DELETE_CASCADE_ON_REFRESH_QUOTATIONS_PROPERTIES = false;
	
	public static void main(String[] args) throws ParseException {
		SpringApplication.run(TraderApplication.class, args);
		
		DefaultParser commandLineParser = new DefaultParser();
		
		Options options = new Options();
		options.addOption("dcrqp", "Delete Cascade on Refresh Quaotations Properties");
		CommandLine commandLine = commandLineParser.parse( options , args);
		
		DELETE_CASCADE_ON_REFRESH_QUOTATIONS_PROPERTIES = Boolean.parseBoolean(commandLine.getOptionValue("dcrqp"));
		
	}
	
	@Autowired
	private QuotationsPropertiesService quotationsPropertiesService;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@PostConstruct
	private void setup() throws IOException 
	{
		Resource quotationsConfig = this.resourceLoader.getResource("classpath:quotations.config.json");
		InputStream inputStream = quotationsConfig.getInputStream();
		
		ObjectMapper quotationsPropertiesMapper = new ObjectMapper();
		QuotationPropertyModel[] quotationProperties = quotationsPropertiesMapper.readValue(inputStream, QuotationPropertyModel[].class);
		
		this.quotationsPropertiesService.refreshQuotationProperties(quotationProperties, DELETE_CASCADE_ON_REFRESH_QUOTATIONS_PROPERTIES);
		
	}
}
