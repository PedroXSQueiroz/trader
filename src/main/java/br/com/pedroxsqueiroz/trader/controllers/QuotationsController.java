package br.com.pedroxsqueiroz.trader.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.pedroxsqueiroz.trader.models.QuotationModel;
import br.com.pedroxsqueiroz.trader.services.QuotationsService;

@RestController
@RequestMapping(value = "/quotations")
@CrossOrigin("*")
public class QuotationsController {

	@Autowired
	private QuotationsService quotationsService;
	
	@GetMapping
	public Page<QuotationModel> list(
			@RequestParam(required = false, defaultValue = "0") Integer offset, 
			@RequestParam(required = false, defaultValue = "15") Integer limit
		)
	{
		return this.quotationsService.list(offset, limit);
	}
	
}
