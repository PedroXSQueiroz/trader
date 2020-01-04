package br.com.pedroxsqueiroz.trader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.pedroxsqueiroz.trader.constants.QuotationPropertyTypeEnum;
import lombok.Data;

@Data
@Entity
@Table(name = "quotation_property_model")
public class QuotationPropertyModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_property_model_id")
	private Integer id;
	
	@Column(name = "quotation_property_model_name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "quotation_property_model_type")
	private QuotationPropertyTypeEnum type;
	
}
