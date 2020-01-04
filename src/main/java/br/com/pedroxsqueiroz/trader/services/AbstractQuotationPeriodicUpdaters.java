package br.com.pedroxsqueiroz.trader.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedroxsqueiroz.trader.daos.QuotationsDao;
import br.com.pedroxsqueiroz.trader.models.QuotationModel;

public abstract class AbstractQuotationPeriodicUpdaters {

	@Autowired
	private QuotationsDao quotationDao;
	
	protected abstract List<QuotationModel> pullQuotations();
	
	public void updateQuotations() 
	{
		List<QuotationModel> quotations = this.pullQuotations();
		
		for( QuotationModel quotation : quotations ) 
		{
			this.quotationDao.save(quotation);
		}
		
	}
	
}
