package com.generation.blogPessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogPessoal.model.Postagem;
import com.generation.blogPessoal.repository.PostagemRepository;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*") //liberar o acesso de qualquer origem ( de qualquer outro servidor front-end) e liberar o header tbm.
public class PostagemController {
	
	@Autowired //injeção de dependência
	private PostagemRepository postagemRepository;
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() { // método para trazer todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
}
