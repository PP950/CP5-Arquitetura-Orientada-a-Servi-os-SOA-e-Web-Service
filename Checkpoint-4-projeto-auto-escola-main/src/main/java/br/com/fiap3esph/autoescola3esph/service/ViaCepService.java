package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.agenda.ValidacaoException;
import br.com.fiap3esph.autoescola3esph.domain.endereco.CepNaoEncontradoException;
import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import br.com.fiap3esph.autoescola3esph.domain.endereco.ServicoExternoIndisponivelException;
import br.com.fiap3esph.autoescola3esph.domain.endereco.ViaCepResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ViaCepService {

    private final RestClient restClient;

    public ViaCepService(@Value("${viacep.url:https://viacep.com.br/ws}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public DadosEndereco buscarPorCep(String cep) {
        String cepLimpo = cep == null ? "" : cep.replaceAll("\\D", "");
        if (cepLimpo.length() != 8) {
            throw new ValidacaoException("CEP inválido. Informe 8 dígitos.");
        }

        ViaCepResponse resposta;
        try {
            resposta = restClient.get()
                    .uri("/{cep}/json/", cepLimpo)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException e) {
            throw new ServicoExternoIndisponivelException("Serviço ViaCEP indisponível no momento.");
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNaoEncontradoException("CEP não encontrado.");
        }

        return new DadosEndereco(
                resposta.logradouro(),
                null,
                resposta.complemento(),
                resposta.bairro(),
                resposta.localidade(),
                resposta.uf(),
                cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5));
    }
}
