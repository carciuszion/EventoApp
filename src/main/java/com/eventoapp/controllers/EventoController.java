package com.eventoapp.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.models.Convidado;
import com.eventoapp.models.Evento;
import com.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {
	
	@Autowired
	private EventoRepository er;
	
	@Autowired
	private ConvidadoRepository cr;
	
	@RequestMapping(value="/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}
	
	@RequestMapping(value="/cadastrarEvento", method = RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes atribAttributes) {
		if(result.hasErrors()) {
			atribAttributes.addFlashAttribute("mensagem", "Preencha todos os campos");
		}else {
			er.save(evento);		
		}
		return "redirect:/cadastrarEvento";
	}

	
	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("evento/lista");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}

	@RequestMapping(value="/{codigo}", method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		Evento evento = er.findByCodigo(codigo);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		mv.addObject("evento", evento);
		
		Iterable<Convidado> convidados = cr.findByEvento(evento);
		mv.addObject("convidados", convidados);
		return mv;
	}
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long codigo, RedirectAttributes atribAttributes) {
		Evento evento = er.findByCodigo(codigo);
		try {
			er.delete(evento);
		} catch (Exception e) {
			atribAttributes.addFlashAttribute("mensagem", "Remova primeiro os convidados");
		}
		
		return "redirect:/eventos";
	}
	@RequestMapping("/deletarConviado")
	public String deletarConvidado(String rg, RedirectAttributes atribAttributes) {
		Convidado convidado = cr.findByRg(rg);
		try {
			cr.delete(convidado);
		} catch (Exception e) {
			atribAttributes.addFlashAttribute("mensagem", e);
		}
		Evento evento = convidado.getEvento();
		
		return "redirect:/" + evento.getCodigo();
	}
	@RequestMapping(value="/{codigo}", method=RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable("codigo") long codigo, @Valid Convidado convidado, BindingResult result, RedirectAttributes atribAttributes ) {
		if(result.hasErrors()) {
			atribAttributes.addFlashAttribute("mensagem", "Preencha todos os campos");
			return "redirect:/{codigo}";
		}else {
			Evento evento = er.findByCodigo(codigo);
			convidado.setEvento(evento);
			cr.save(convidado);
			atribAttributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso");
		}
		return "redirect:/{codigo}";
	}
	
	
}
