package com.generation.blogPessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogPessoal.model.Usuario;
import com.generation.blogPessoal.repository.UsuarioRepository;
import com.generation.blogPessoal.service.UsuarioService;
import com.generation.blogPessoal.util.JwtHelper;
import com.generation.blogPessoal.util.TestBuilder;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String BASE_URL = "/usuarios";
	private static final String USUARIO = "root@root.com";
	private static final String SENHA = "rootroot";
	
	@BeforeAll
	void inicio() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	}
	
	@Test
	@DisplayName("01 - Deve Cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario() {
		//Given Definir o cenário
		Usuario usuario = TestBuilder.criarUsuario(null, "Thuany", "thuany@email.com.br", "12345678");
		
		//When Fazer o cadastro
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);		
		
		//Then checar o resultado
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("02 - Não deve Cadastrar usuário duplicado")
	void naoDeveCadastrarUsuarioDuplicado() {
		
		//Given Definir o cenário
		Usuario usuario = TestBuilder.criarUsuario(null, "Rafaela Lemes",
				"rafa_lemes@email.com.br", "12345678");
		usuarioService.cadastrarUsuario(usuario); //esse que vai duplicar o usuario
				
		//When Fazer o cadastro
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);		
		
		//Then checar o resultado
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("03 - Deve atualizar os dados do usuário com sucesso")
	void deveAtualizarUmUsuario() {
		
		//Given Definir o cenário
		Usuario usuario = TestBuilder.criarUsuario(null, "Nadia Caricatto",
				"nadia@email.com.br", "12345678");
		Optional<Usuario>usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Nadia Caricatto",
				"nadia@email.com.br", "abc12345");
		
		//When Fazer o cadastro
		// Gerar o token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA );
		// Criar a Requisicao com o token
		HttpEntity<Usuario> requisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);
		//Enviar a requisição PUT
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/atualizar", HttpMethod.PUT,
				requisicao, Usuario.class);		
		
		//Then checar o resultado
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("04 - Deve listar todos os usuários com sucesso")
	void deveListarTodosUsuario() {
		
		//Given Definir o cenário
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Ana Silva",
                "ana_lima@email.com.br", "12345678"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Bruno Souza", 
				"bruno_souza@email.com.br", "12345678"));
				
		//When Fazer o cadastro
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA );
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario[]> resposta = testRestTemplate.exchange(BASE_URL + "/all", HttpMethod.GET, requisicao,
				Usuario[].class);
		
		//Then checar o resultado
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		
	}
	
	@Test
	@DisplayName("05- Deve buscar usuário por ID com sucesso")
	void deveBuscarUsuarioPorId() {
		
		//Given Definir o cenário
		Usuario usuario = TestBuilder.criarUsuario(
				null, "Carlos Eduardo","carlos_eduardo@email.com.br", "12345678");
		Optional<Usuario> usuarioCriado = usuarioService.cadastrarUsuario(usuario);
		
		// Garantir que o usuário foi criado
		assertTrue(usuarioCriado.isPresent());
				
		//When Fazer o cadastro
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA );
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/" + usuarioCriado.get().getId(), 
				HttpMethod.GET, requisicao,
				Usuario.class);
		
		//Then checar o resultado
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		assertEquals(usuarioCriado.get().getId(),resposta.getBody().getId());
		assertEquals("Carlos Eduardo",resposta.getBody().getNome());
		
	}
	
	
	
}
