package com.generation.blogPessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogPessoal.model.Postagem;
import com.generation.blogPessoal.repository.PostagemRepository;
import com.generation.blogPessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*") // liberar o acesso de qualquer origem ( de qualquer outro servidor
													// front-end) e liberar o header tbm.
public class PostagemController {

	@Autowired // injeção de dependência
	private PostagemRepository postagemRepository;

	@Autowired
	private TemaRepository temaRepository;

	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() { // método para trazer todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {
		return postagemRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}

	@PostMapping
	public ResponseEntity<Postagem> postPostagem(@Valid @RequestBody Postagem postagem) {
		if (temaRepository.existsById(postagem.getTema().getId())) {

			// para deixar o id nulo para o front-end não gerar id para não dar conflito com
			// o auto increment do banco
			postagem.setId(null);
			return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!", null);
	}

	@PutMapping
	public ResponseEntity<Postagem> putPostagem(@Valid @RequestBody Postagem postagem) {

		if (postagemRepository.existsById(postagem.getId())) {
			if (temaRepository.existsById(postagem.getTema().getId())) {

				return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!", null);
		}
		return ResponseEntity.notFound().build();
	}

	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void deletePostagem(@PathVariable Long id) {
		// verificar se a postagem existe
		Optional<Postagem> postagem = postagemRepository.findById(id);

		if (postagem.isEmpty())
			// Quando lançamos uma exceção não precisamos usar o else, pois a exceção
			// interrompe o fluxo normal do programa
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		postagemRepository.deleteById(id);

	}

}
