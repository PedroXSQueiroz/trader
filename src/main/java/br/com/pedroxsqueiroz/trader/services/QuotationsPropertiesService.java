package br.com.pedroxsqueiroz.trader.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedroxsqueiroz.trader.daos.QuotationsPropertiesDao;
import br.com.pedroxsqueiroz.trader.exceptions.QuotationPropertyCouldNotBeDeletedException;
import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;
import lombok.extern.java.Log;

@Service
@Log
public class QuotationsPropertiesService {
	
	@Autowired
	private QuotationsPropertiesDao quotationsPropertiesDao;
	
	@Autowired
	private QuotationsPropertiesValuesService quotationsPropertiesValuesService;
	
	@Autowired
	private QuotationsService quotationsService;
	
	@Transactional
	public void refreshQuotationProperties(QuotationPropertyModel[] quotationProperties, boolean deleteInCascade) 
	{
		
		List<QuotationPropertyModel> allProperties = new ArrayList<QuotationPropertyModel>(this.quotationsPropertiesDao.findAll());
		
		for(QuotationPropertyModel property : quotationProperties) 
		{
			String name = property.getName();
			Optional<QuotationPropertyModel> propertyAlreadyExists = allProperties.stream().filter(q -> q.getName().equals(name)).findFirst();
			
			if(propertyAlreadyExists.isPresent()) 
			{
				QuotationPropertyModel managedProperty = propertyAlreadyExists.get();
				
				this.updateProperty(property, managedProperty);
				
				allProperties.remove(managedProperty);
				
			}
			else
			{
				this.saveProperty(property);
			}
		}
		
		for(QuotationPropertyModel remainingProperty : allProperties ) 
		{
			try 
			{
				
				this.delete(remainingProperty, deleteInCascade);
			
			} catch (QuotationPropertyCouldNotBeDeletedException e) {
				
				e.printStackTrace();
				
				this.log.warning(String.format(	"The property %s could not be deleted, "
												+ "contains values and quotations assotiated to it. "
												+ "Delete them or execute refresh with dcrqp option (Delete Cascade on Refresh Quaotations Properties) equals to true", 
											remainingProperty.getName()));
			}
			
		}
		
	}
	
	public void delete(QuotationPropertyModel remainingProperty) throws QuotationPropertyCouldNotBeDeletedException 
	{
		this.delete(remainingProperty, false);
	}

	public void delete( 
				QuotationPropertyModel remainingProperty,
				boolean deleteInCascade)
		throws QuotationPropertyCouldNotBeDeletedException {
		List<QuotationPropertyValueModel> values = this.quotationsPropertiesValuesService.getByProperty(remainingProperty);
		
		boolean containsValues = !values.isEmpty();
		
		if(containsValues && !deleteInCascade) 
		{
			throw new QuotationPropertyCouldNotBeDeletedException();
		
		}else if(containsValues)
		{
			for(QuotationPropertyValueModel value: values) 
			{
				this.quotationsPropertiesValuesService.delete(value);
			}
			
			List<QuotationModel> quotations = this.quotationsService.findWithProperty(remainingProperty);
			
			for(QuotationModel currentQuotation: quotations) 
			{
				this.quotationsService.delete(currentQuotation);
			}
		
		}
		
		this.quotationsPropertiesDao.delete(remainingProperty);
	}

	private QuotationPropertyModel saveProperty(QuotationPropertyModel property) {
		return this.quotationsPropertiesDao.save(property);
	}

	private void updateProperty(QuotationPropertyModel property, QuotationPropertyModel managedProperty) {
		
		managedProperty.setName(property.getName());
		managedProperty.setDescription(property.getDescription());
		
		saveProperty(managedProperty);
	}

	public QuotationPropertyModel findByName(String propertyName) {
		
		return this.quotationsPropertiesDao.findOne((root, query, cb) -> 
			cb.equal(root.get("name"), propertyName)
		).get();
	
	}

}
