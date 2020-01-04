package br.com.pedroxsqueiroz.trader.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.pedroxsqueiroz.trader.models.QuotationModel;

@Repository
public interface QuotationsDao extends JpaRepository<QuotationModel, Integer>, JpaSpecificationExecutor<QuotationModel>{

}
