CREATE TABLE IF NOT EXISTS quotations_properties(
	id_quotation_property INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quotation_property_name NVARCHAR(256) NOT NULL,
	quotation_property_type NVARCHAR(100) NOT NULL,
	description NVARCHAR(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS quotations(
	id_quotation INT NOT NULL PRIMARY KEY AUTO_INCREMENT 
);

CREATE TABLE IF NOT EXISTS quotation_property_value(
	id_quotation_property_value INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	property_value NVARCHAR(256) NOT NULL,
	id_quotation_property INT NOT NULL,
	CONSTRAINT fk_Value_Property
		FOREIGN KEY (id_quotation_property)
		REFERENCES quotations_properties(id_quotation_property),
	id_quotation INT,
	CONSTRAINT fk_Value_quotation
		FOREIGN KEY (id_quotation)
		REFERENCES quotations(id_quotation)
);
