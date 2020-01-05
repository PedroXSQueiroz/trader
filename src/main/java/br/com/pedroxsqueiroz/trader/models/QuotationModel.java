package br.com.pedroxsqueiroz.trader.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.pedroxsqueiroz.trader.converters.json.QuotationJsonSerializer;
import lombok.Data;

@Table(name = "quotations")
@Entity()
@Data
@JsonSerialize(using = QuotationJsonSerializer.class)
public class QuotationModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_quotation")
	private Integer id;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_quotation")
	private List<QuotationPropertyValueModel> propertiesValues;
	
}
