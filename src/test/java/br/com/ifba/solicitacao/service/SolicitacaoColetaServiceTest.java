package br.com.ifba.solicitacao.service;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.repository.MoradorIRepository;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.repository.SolicitacaoColetaIRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoColetaServiceTest {

    @Mock
    private SolicitacaoColetaIRepository solicitacaoColetaRepository;

    @Mock
    private MoradorIRepository moradorRepository;

    @InjectMocks
    private SolicitacaoColetaService solicitacaoColetaService;

    private Morador morador;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
        endereco.setCep("40000000");
        endereco.setLogradouro("Rua das Acácias");
        endereco.setNumero("123");
        endereco.setBairro("Centro");

        morador = new Morador();
        morador.setId(1L);
        morador.setNome("João da Silva");
        morador.setEmail("joao@email.com");
        morador.setTelefone("71999998888");
        morador.setEndereco(endereco);
    }

    @Test
    @DisplayName("Deve criar solicitação de coleta com sucesso para itens válidos")
    void testCriarSolicitacaoComSucesso() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.MOVEIS)
                .descricao("Sofá de 3 lugares")
                .quantidade(1)
                .build());
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ELETRONICOS)
                .descricao("Computador antigo")
                .quantidade(2)
                .build());
        solicitacao.setItens(itens);

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitacaoColeta resultado = solicitacaoColetaService.criarSolicitacao(solicitacao, 1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getProtocolo());
        assertTrue(resultado.getProtocolo().startsWith("CV-"));
        assertEquals(StatusSolicitacao.PENDENTE, resultado.getStatus());
        assertEquals(morador, resultado.getMorador());
        assertEquals("Centro", resultado.getEnderecoColeta().getBairro());
        assertEquals(2, resultado.getItens().size());
        verify(solicitacaoColetaRepository, times(1)).save(any(SolicitacaoColeta.class));
    }

    @Test
    @DisplayName("Deve criar solicitação de entulho de reforma quando volume estiver dentro do limite de 1 m³")
    void testCriarSolicitacaoComEntulhoLimiteValido() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ENTULHO_REFORMA)
                .descricao("Restos de azulejos e cimento")
                .quantidade(1)
                .volumeEstimadoM3(0.8)
                .build());
        solicitacao.setItens(itens);

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitacaoColeta resultado = solicitacaoColetaService.criarSolicitacao(solicitacao, 1L);

        assertNotNull(resultado);
        assertEquals(0.8, resultado.getItens().get(0).getVolumeEstimadoM3());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando volume de entulho de reforma exceder 1 m³")
    void testCriarSolicitacaoComEntulhoExcedendoLimite() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ENTULHO_REFORMA)
                .descricao("Entulho de parede demolida")
                .quantidade(1)
                .volumeEstimadoM3(1.5)
                .build());
        solicitacao.setItens(itens);

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                solicitacaoColetaService.criarSolicitacao(solicitacao, 1L)
        );

        assertTrue(exception.getMessage().contains("excede o limite máximo permitido de 1.0 m³"));
        verify(solicitacaoColetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando a soma de múltiplos itens de entulho exceder 1 m³")
    void testCriarSolicitacaoComMultiplosItensEntulhoExcedendoLimiteTotal() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ENTULHO_REFORMA)
                .descricao("Sacos de argamassa")
                .quantidade(1)
                .volumeEstimadoM3(0.6)
                .build());
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ENTULHO_REFORMA)
                .descricao("Tijolos quebrados")
                .quantidade(1)
                .volumeEstimadoM3(0.5)
                .build());
        solicitacao.setItens(itens);

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                solicitacaoColetaService.criarSolicitacao(solicitacao, 1L)
        );

        assertTrue(exception.getMessage().contains("excede o limite máximo permitido de 1.0 m³"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando item de entulho não possuir volume informado")
    void testCriarSolicitacaoEntulhoSemVolume() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ENTULHO_REFORMA)
                .descricao("Entulho sem volume informado")
                .quantidade(1)
                .volumeEstimadoM3(null)
                .build());
        solicitacao.setItens(itens);

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                solicitacaoColetaService.criarSolicitacao(solicitacao, 1L)
        );

        assertTrue(exception.getMessage().contains("obrigatório informar o volume estimado"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando a lista de itens for vazia")
    void testCriarSolicitacaoSemItens() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        solicitacao.setItens(new ArrayList<>());

        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));

        assertThrows(BusinessException.class, () ->
                solicitacaoColetaService.criarSolicitacao(solicitacao, 1L)
        );
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o morador não for encontrado")
    void testCriarSolicitacaoMoradorNaoEncontrado() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        when(moradorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                solicitacaoColetaService.criarSolicitacao(solicitacao, 99L)
        );
    }

    @Test
    @DisplayName("Deve buscar solicitação por protocolo")
    void testBuscarPorProtocolo() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        solicitacao.setProtocolo("CV-20261115-ABC123");

        when(solicitacaoColetaRepository.findByProtocolo("CV-20261115-ABC123")).thenReturn(Optional.of(solicitacao));

        SolicitacaoColeta encontrada = solicitacaoColetaService.findByProtocolo("CV-20261115-ABC123");
        assertNotNull(encontrada);
        assertEquals("CV-20261115-ABC123", encontrada.getProtocolo());
    }

    @Test
    @DisplayName("Deve permitir cancelamento de solicitação pendente pelo próprio morador")
    void testCancelarPeloMorador() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        solicitacao.setId(10L);
        solicitacao.setMorador(morador);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setProtocolo("CV-20261115-ABC123");

        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta cancelada = solicitacaoColetaService.cancelarPeloMorador(10L, 1L, "Já doei para um vizinho");

        assertEquals(StatusSolicitacao.CANCELADA, cancelada.getStatus());
        assertEquals("Já doei para um vizinho", cancelada.getMotivoCancelamento());
    }
}
