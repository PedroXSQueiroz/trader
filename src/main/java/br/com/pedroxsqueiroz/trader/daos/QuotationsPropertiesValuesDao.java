package br.com.pedroxsqueiroz.trader.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.pedroxsqueiroz.trader.models.QuotationPropertyValueModel;

@Repository
public interface QuotationsPropertiesValuesDao extends JpaRepository<QuotationPropertyValueModel, Integer>, JpaSpecificationExecutor<QuotationPropertyValueModel>{
	
}
