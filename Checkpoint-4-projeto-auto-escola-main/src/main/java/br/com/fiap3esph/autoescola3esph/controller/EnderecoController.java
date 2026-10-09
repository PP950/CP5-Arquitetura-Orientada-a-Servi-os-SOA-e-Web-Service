package br.com.fiap3esph.autoescola3esph.controller;

import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import br.com.fiap3esph.autoescola3esph.service.ViaCepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final ViaCepService service;

    @GetMapping("/cep/{cep}")
    public ResponseEntity<DadosEndereco> buscarPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(service.buscarPorCep(cep));
    }
}
