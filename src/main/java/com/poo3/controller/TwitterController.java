package com.poo3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.poo3.service.TwitterService;

@RestController("/")
public class TwitterController {

	@Autowired
	public TwitterService twitterService;

	@GetMapping("/status")
	public ModelAndView status() {
		ModelAndView mv = new ModelAndView("Status");
		String status = "Rodando";
		mv.addObject("status", status);
		return mv;
	}

	@GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> consultar() {
		try {
			// String msg =
			twitterService.postar();
			return new ResponseEntity<Object>("ok", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
