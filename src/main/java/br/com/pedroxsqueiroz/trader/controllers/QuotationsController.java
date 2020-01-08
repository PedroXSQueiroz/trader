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
			@RequestParam(required = false, defaultValue = "0", name="page") Integer page, 
			@RequestParam(required = false, defaultValue = "50", name="size") Integer size
		)
	{
		return this.quotationsService.list(page, size);
	}
	
}
