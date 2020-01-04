package br.com.pedroxsqueiroz.trader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "quotation_property_value")
@Data
public class QuotationPropertyValueModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_quotation_property_value")
	private Integer id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_quotation_property")
	private QuotationPropertyModel property;
	
	@Column(name = "property_value")
	private String value;
	
}
