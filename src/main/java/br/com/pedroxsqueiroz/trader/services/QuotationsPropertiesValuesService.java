package br.com.pedroxsqueiroz.trader.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedroxsqueiroz.trader.daos.QuotationsPropertiesValuesDao;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

@Service
public class QuotationsPropertiesValuesService {

	@Autowired
	private QuotationsPropertiesValuesDao quotationsPropertiesValuesDao;
	
	public List<QuotationPropertyValueModel> getByProperty(QuotationPropertyModel property)
	{
		return this.quotationsPropertiesValuesDao.findAll((root, query, cb) -> {
			return cb.equal(root.get("property"), property);
		});
	}

	public void delete(QuotationPropertyValueModel value) {
		
		this.quotationsPropertiesValuesDao.delete(value);
		
	}
	
	
}
