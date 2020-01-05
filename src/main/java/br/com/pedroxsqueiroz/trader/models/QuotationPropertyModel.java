package br.com.pedroxsqueiroz.trader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.pedroxsqueiroz.trader.constants.QuotationPropertyTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "quotations_properties")
public class QuotationPropertyModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_quotation_property")
	private Integer id;
	
	@Column(name = "quotation_property_name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "quotation_property_type")
	private QuotationPropertyTypeEnum type;
	
	@Override
	public int hashCode() 
	{
		return this.getName().hashCode();
	}
	
}
