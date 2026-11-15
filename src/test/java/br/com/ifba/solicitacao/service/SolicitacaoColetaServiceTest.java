package br.com.ifba.solicitacao.service;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.repository.MoradorIRepository;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.entity.FotoSolicitacao;
import br.com.ifba.solicitacao.repository.FotoSolicitacaoIRepository;
import br.com.ifba.solicitacao.repository.SolicitacaoColetaIRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private FotoSolicitacaoIRepository fotoSolicitacaoRepository;

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

        assertTrue(exception.getMessage().contains("excede o limite máximo permitido de 1,0 m³"));
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

        assertTrue(exception.getMessage().contains("excede o limite máximo permitido de 1,0 m³"));
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

    // ---------- RF09: cancelamento só antes da aceitação ----------

    private SolicitacaoColeta solicitacaoComStatus(StatusSolicitacao status) {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        solicitacao.setId(10L);
        solicitacao.setMorador(morador);
        solicitacao.setStatus(status);
        solicitacao.setProtocolo("CV-20261115-ABC123");
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        return solicitacao;
    }

    @Test
    @DisplayName("Deve permitir cancelamento de solicitação em análise (ainda não aceita)")
    void testCancelarEmAnalise() {
        solicitacaoComStatus(StatusSolicitacao.EM_ANALISE);
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta cancelada = solicitacaoColetaService.cancelarPeloMorador(10L, 1L, null);

        assertEquals(StatusSolicitacao.CANCELADA, cancelada.getStatus());
        assertEquals("Cancelado pelo morador.", cancelada.getMotivoCancelamento());
    }

    @ParameterizedTest
    @EnumSource(value = StatusSolicitacao.class, names = {"ACEITA", "AGENDADA", "EM_ANDAMENTO"})
    @DisplayName("Não deve permitir que o morador cancele depois que a prefeitura aceitou")
    void testNaoCancelaDepoisDeAceita(StatusSolicitacao status) {
        solicitacaoComStatus(status);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> solicitacaoColetaService.cancelarPeloMorador(10L, 1L, null));

        assertEquals("A solicitação já foi aceita pela prefeitura e não pode mais ser cancelada.", ex.getMessage());
        verify(solicitacaoColetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve permitir cancelar solicitação de outro morador")
    void testNaoCancelaDeOutroMorador() {
        solicitacaoComStatus(StatusSolicitacao.PENDENTE);

        assertThrows(BusinessException.class, () -> solicitacaoColetaService.cancelarPeloMorador(10L, 2L, null));
        verify(solicitacaoColetaRepository, never()).save(any());
    }

    // ---------- Foto do material ----------

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0};

    private SolicitacaoColeta solicitacaoValida() {
        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        List<ItemColeta> itens = new ArrayList<>();
        itens.add(ItemColeta.builder().tipoResiduo(TipoResiduo.RECICLAVEIS).descricao("Garrafas PET").quantidade(10).build());
        solicitacao.setItens(itens);
        return solicitacao;
    }

    @Test
    @DisplayName("Deve criar solicitação e guardar a foto JPG")
    void testCriarComFotoJpeg() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta criada = solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/jpeg", JPEG);

        ArgumentCaptor<FotoSolicitacao> foto = ArgumentCaptor.forClass(FotoSolicitacao.class);
        verify(fotoSolicitacaoRepository).save(foto.capture());
        assertSame(criada, foto.getValue().getSolicitacaoColeta());
        assertEquals("image/jpeg", foto.getValue().getContentType());
        assertArrayEquals(JPEG, foto.getValue().getConteudo());
        assertEquals(StatusSolicitacao.PENDENTE, criada.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar foto PNG")
    void testCriarComFotoPng() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/png", PNG);

        verify(fotoSolicitacaoRepository).save(any(FotoSolicitacao.class));
    }

    @Test
    @DisplayName("Não deve criar solicitação quando a foto não é imagem")
    void testCriarComArquivoQueNaoEImagem() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/jpeg", "%PDF-1.7".getBytes()));

        assertEquals("A foto deve ser JPG ou PNG.", ex.getMessage());
        verify(solicitacaoColetaRepository, never()).save(any());
        verify(fotoSolicitacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve aceitar tipo declarado diferente do conteúdo")
    void testCriarComTipoDeclaradoErrado() {
        assertThrows(BusinessException.class,
                () -> solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/png", JPEG));
        verify(solicitacaoColetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar solicitação sem foto ou com foto acima de 5 MB")
    void testCriarSemFotoOuFotoGrande() {
        BusinessException semFoto = assertThrows(BusinessException.class,
                () -> solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/jpeg", new byte[0]));
        assertEquals("Adicione uma foto do material.", semFoto.getMessage());

        byte[] grande = new byte[SolicitacaoColetaService.TAMANHO_MAXIMO_FOTO + 1];
        System.arraycopy(JPEG, 0, grande, 0, JPEG.length);
        BusinessException fotoGrande = assertThrows(BusinessException.class,
                () -> solicitacaoColetaService.criarSolicitacao(solicitacaoValida(), 1L, "image/jpeg", grande));
        assertEquals("A foto deve ter no máximo 5 MB.", fotoGrande.getMessage());

        verify(solicitacaoColetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve informar quando a solicitação não tem foto")
    void testBuscarFotoInexistente() {
        when(fotoSolicitacaoRepository.findBySolicitacaoColetaId(10L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> solicitacaoColetaService.buscarFoto(10L));
        assertEquals("Esta solicitação não possui foto.", ex.getMessage());
    }
}
