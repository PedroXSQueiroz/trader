package br.com.pedroxsqueiroz.trader.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.stream.Collectors;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedroxsqueiroz.trader.daos.QuotationsPropertiesValuesDao;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;
import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

@Service
public class QuotationsPropertiesValuesService {

	public class ValueSearchStatement
	{
		private final String propertyTargetName;		
		private final Map<String, String> conditionals;
		private final QuotationsPropertiesValuesDao dao;
		
		ValueSearchStatement(final String propertyTargetName, QuotationsPropertiesValuesDao dao)
		{
			this.propertyTargetName = propertyTargetName;
			this.conditionals = new HashMap<String , String>();
			this.dao = dao;
		}
		
		ValueSearchStatement with(String propertyName, String propertyValue)
		{
			this.conditionals.put(propertyName, propertyValue);
			return this;
		}
		
		public <R> List<R> fetch()
		{
			
			return 
				(List<R>) this.dao.findAll((root, query, cb ) -> {
					
					Predicate predicates = cb.equal(root.get("property").get("name"), this.propertyTargetName);
					
					for(Entry<String, String> conditional : this.conditionals.entrySet())
					{
						String propertyName = conditional.getKey();
						
						predicates = cb.and(
								cb.equal(
									root.get( propertyName ), 
									conditional.getValue()
								),
								predicates
							);
					}
					
					query.distinct(true);
					
					return predicates;
				})
				.stream()
				.map( val -> val.getValueContent())
				.distinct()
				.collect(Collectors.toList());
		}
	}
	
	@Autowired
	private QuotationsPropertiesValuesDao quotationsPropertiesValuesDao;
	
	public List<QuotationPropertyValueModel> getByProperty(QuotationPropertyModel property)
	{
		return this.quotationsPropertiesValuesDao.findAll((root, query, cb) -> {
			return cb.equal(root.get("property"), property);
		});
	}

	public void delete(QuotationPropertyValueModel value) 
	{
		
		this.quotationsPropertiesValuesDao.delete(value);
		
	}

	public ValueSearchStatement getValues(String propertyTarget) 
	{
		
		return new ValueSearchStatement(propertyTarget, this.quotationsPropertiesValuesDao);
		
	}
	
	
}
