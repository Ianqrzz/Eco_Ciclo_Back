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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitacaoColetaService implements SolicitacaoColetaIService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoColetaService.class);
    private static final double LIMITE_MAX_ENTULHO_M3 = 1.0;

    private final SolicitacaoColetaIRepository solicitacaoColetaRepository;
    private final MoradorIRepository moradorRepository;

    @Override
    @Transactional
    public SolicitacaoColeta criarSolicitacao(SolicitacaoColeta solicitacao, Long moradorId) {
        logger.info("Iniciando criação de solicitação de coleta para o morador ID: {}", moradorId);

        if (moradorId == null) {
            throw new BusinessException("O identificador do morador é obrigatório.");
        }

        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new BusinessException("Morador não encontrado para o ID: " + moradorId));

        if (solicitacao.getItens() == null || solicitacao.getItens().isEmpty()) {
            throw new BusinessException("A solicitação deve conter ao menos um item para coleta.");
        }

        // Validação da regra de negócio de entulho de reforma (máx 1 m³) e consistência dos itens
        validarItens(solicitacao);

        // Se não foi informado um endereço específico de coleta, adota o endereço padrão do morador
        if (solicitacao.getEnderecoColeta() == null) {
            if (morador.getEndereco() == null) {
                throw new BusinessException("O morador não possui endereço cadastrado e nenhum endereço de coleta foi fornecido.");
            }
            Endereco enderecoPadrao = new Endereco();
            enderecoPadrao.setCep(morador.getEndereco().getCep());
            enderecoPadrao.setLogradouro(morador.getEndereco().getLogradouro());
            enderecoPadrao.setNumero(morador.getEndereco().getNumero());
            enderecoPadrao.setBairro(morador.getEndereco().getBairro());
            solicitacao.setEnderecoColeta(enderecoPadrao);
        }

        solicitacao.setMorador(morador);
        solicitacao.setProtocolo(gerarProtocoloUnico());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        // Associa cada item à solicitação
        for (ItemColeta item : solicitacao.getItens()) {
            item.setSolicitacaoColeta(solicitacao);
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                item.setQuantidade(1);
            }
        }

        SolicitacaoColeta salva = solicitacaoColetaRepository.save(solicitacao);
        logger.info("Solicitação de coleta criada com sucesso. Protocolo: {}", salva.getProtocolo());
        return salva;
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitacaoColeta findById(Long id) {
        if (id == null) {
            throw new BusinessException("O identificador da solicitação é obrigatório.");
        }
        return solicitacaoColetaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Solicitação de coleta não encontrada para o ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitacaoColeta findByProtocolo(String protocolo) {
        if (protocolo == null || protocolo.isBlank()) {
            throw new BusinessException("O protocolo de busca não pode ser vazio.");
        }
        return solicitacaoColetaRepository.findByProtocolo(protocolo.trim().toUpperCase())
                .orElseThrow(() -> new BusinessException("Solicitação não encontrada para o protocolo: " + protocolo));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitacaoColeta> findByMorador(Long moradorId, Pageable pageable) {
        if (moradorId == null) {
            throw new BusinessException("O identificador do morador é obrigatório.");
        }
        return solicitacaoColetaRepository.findByMoradorId(moradorId, pageable);
    }

    @Override
    @Transactional
    public SolicitacaoColeta cancelarPeloMorador(Long id, Long moradorId, String motivo) {
        SolicitacaoColeta solicitacao = findById(id);

        if (!solicitacao.getMorador().getId().equals(moradorId)) {
            throw new BusinessException("Você não tem permissão para cancelar esta solicitação.");
        }

        if (solicitacao.getStatus().isTerminal()) {
            throw new BusinessException("A solicitação não pode ser cancelada pois já está " + solicitacao.getStatus().getDescricao() + ".");
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        solicitacao.setMotivoCancelamento(motivo != null && !motivo.isBlank() ? motivo : "Cancelado pelo morador.");
        logger.info("Solicitação protocolo {} cancelada pelo morador ID {}", solicitacao.getProtocolo(), moradorId);
        return solicitacaoColetaRepository.save(solicitacao);
    }

    /**
     * Valida os itens da coleta com ênfase na regra de negócio de volume de entulho de reforma.
     */
    private void validarItens(SolicitacaoColeta solicitacao) {
        double totalVolumeEntulho = 0.0;
        boolean temEntulho = false;

        for (ItemColeta item : solicitacao.getItens()) {
            if (item.getTipoResiduo() == null) {
                throw new BusinessException("Todos os itens devem possuir um tipo de resíduo especificado.");
            }
            if (item.getDescricao() == null || item.getDescricao().trim().isBlank()) {
                throw new BusinessException("A descrição do item de resíduo é obrigatória.");
            }

            if (item.getTipoResiduo() == TipoResiduo.ENTULHO_REFORMA) {
                temEntulho = true;
                if (item.getVolumeEstimadoM3() == null || item.getVolumeEstimadoM3() <= 0.0) {
                    throw new BusinessException("Para o resíduo 'Entulho de reforma', é obrigatório informar o volume estimado em m³ (maior que zero).");
                }
                totalVolumeEntulho += item.getVolumeEstimadoM3();
            }
        }

        if (temEntulho && totalVolumeEntulho > LIMITE_MAX_ENTULHO_M3) {
            throw new BusinessException(String.format(
                    "O volume total de entulho de reforma (%.2f m³) excede o limite máximo permitido de %.1f m³ por solicitação.",
                    totalVolumeEntulho, LIMITE_MAX_ENTULHO_M3
            ));
        }
    }

    private String gerarProtocoloUnico() {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sufixo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "CV-" + data + "-" + sufixo;
    }
}
