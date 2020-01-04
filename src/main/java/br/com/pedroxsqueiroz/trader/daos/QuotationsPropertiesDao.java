package br.com.pedroxsqueiroz.trader.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.pedroxsqueiroz.trader.models.QuotationPropertyModel;

@Repository
public interface QuotationsPropertiesDao extends JpaRepository<QuotationPropertyModel, Integer>, JpaSpecificationExecutor<QuotationPropertyModel>{

}
