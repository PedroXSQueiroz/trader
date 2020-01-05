package br.com.pedroxsqueiroz.trader.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.pedroxsqueiroz.trader.daos.QuotationsDao;
import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;
import lombok.extern.java.Log;

@Service
@Log
public class QuotationsService {
	
	@Autowired
	private QuotationsDao quotationsDao;
	
	@Autowired
	private QuotationsPropertiesService quotationsPropertiesService;
	
	@Autowired
	private List<? extends AbstractQuotationPeriodicUpdaters> quotationsPeriodicUpdaters;
	
	@Scheduled(fixedRate = 1000)
	public void updateQuotationsPeriodically() 
	{
		this.log.log( Level.FINE , "Updating quotations");
		
		for(AbstractQuotationPeriodicUpdaters updater : this.quotationsPeriodicUpdaters ) 
		{
			updater.updateQuotations();
		}
		
		this.log.log( Level.FINE,  "Finished Updating quotations");
	}

	public List<QuotationModel> findWithProperty(QuotationPropertyModel remainingProperty) {

		return this.quotationsDao.findAll((root, query, cb) -> {
			
			return root.join("propertiesValues").get("property").in(remainingProperty);
			
		});
	}
	
	public void setPropertyValueToQuotation(String propertyName, String propertyValue, QuotationModel quotation) 
	{
		
		List<QuotationPropertyValueModel> propertiesValues = quotation.getPropertiesValues();
		
		QuotationPropertyValueModel value = new QuotationPropertyValueModel();

		if(propertiesValues != null) 
		{
			Optional<QuotationPropertyValueModel> propQuery = propertiesValues
					.stream()
					.filter(propVal -> 
								propVal
								.getProperty()
								.getName()
								.equals(propertyName)
					).findFirst();
			
			if( propQuery.isPresent() ) 
			{
				value = propQuery.get();
			}
			else
			{
				QuotationPropertyModel property = this.quotationsPropertiesService.findByName(propertyName);
				value.setProperty(property);
				propertiesValues.add(value);
			}			
		}
		else
		{
			propertiesValues = new ArrayList<QuotationPropertyValueModel>();
			QuotationPropertyModel property = this.quotationsPropertiesService.findByName(propertyName);
			value.setProperty(property);
			propertiesValues.add(value);
			quotation.setPropertiesValues(propertiesValues);
		}
		
		
		value.setValue(propertyValue);
		
	}

	public void delete(QuotationModel currentQuotation) {
		
		this.quotationsDao.delete(currentQuotation);
		
	}

	public Page<QuotationModel> list(Integer offset, Integer limit) {
		
		return this.quotationsDao.findAll(PageRequest.of(offset, limit));
	
	}
	
}
