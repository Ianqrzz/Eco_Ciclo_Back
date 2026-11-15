package br.com.ifba.solicitacao.repository.specification;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaFiltroDto;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoColetaSpecification {

    public static Specification<SolicitacaoColeta> comFiltro(SolicitacaoColetaFiltroDto filtro) {
        return (root, query, criteriaBuilder) -> {
            if (filtro == null) {
                return criteriaBuilder.conjunction();
            }

            // Garante registros distintos ao fazer joins com coleções
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro por Status
            if (filtro.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.getStatus()));
            }

            // 2. Filtro por Tipo de Resíduo
            if (filtro.getTipoResiduo() != null) {
                Join<SolicitacaoColeta, ItemColeta> joinItens = root.join("itens", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(joinItens.get("tipoResiduo"), filtro.getTipoResiduo()));
            }

            // 3. Filtro por Bairro
            if (filtro.getBairro() != null && !filtro.getBairro().isBlank()) {
                Join<SolicitacaoColeta, Endereco> joinEndereco = root.join("enderecoColeta", JoinType.INNER);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(joinEndereco.get("bairro")),
                        "%" + filtro.getBairro().trim().toLowerCase() + "%"
                ));
            }

            // 4. Filtro por Intervalo de Datas (Data da Solicitação)
            if (filtro.getDataInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dataSolicitacao"),
                        filtro.getDataInicio().atStartOfDay()
                ));
            }
            if (filtro.getDataFim() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataSolicitacao"),
                        filtro.getDataFim().atTime(LocalTime.MAX)
                ));
            }

            // 5. Busca Geral por Termo (Protocolo, Morador, Telefone, Logradouro, Bairro)
            if (filtro.getTermo() != null && !filtro.getTermo().isBlank()) {
                String pattern = "%" + filtro.getTermo().trim().toLowerCase() + "%";

                Join<SolicitacaoColeta, Morador> joinMorador = root.join("morador", JoinType.LEFT);
                Join<SolicitacaoColeta, Endereco> joinEnderecoBusca = root.join("enderecoColeta", JoinType.LEFT);

                Predicate protocoloMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("protocolo")), pattern);
                Predicate moradorNomeMatch = criteriaBuilder.like(criteriaBuilder.lower(joinMorador.get("nome")), pattern);
                Predicate moradorEmailMatch = criteriaBuilder.like(criteriaBuilder.lower(joinMorador.get("email")), pattern);
                Predicate moradorTelefoneMatch = criteriaBuilder.like(criteriaBuilder.lower(joinMorador.get("telefone")), pattern);
                Predicate logradouroMatch = criteriaBuilder.like(criteriaBuilder.lower(joinEnderecoBusca.get("logradouro")), pattern);
                Predicate bairroMatch = criteriaBuilder.like(criteriaBuilder.lower(joinEnderecoBusca.get("bairro")), pattern);

                predicates.add(criteriaBuilder.or(
                        protocoloMatch,
                        moradorNomeMatch,
                        moradorEmailMatch,
                        moradorTelefoneMatch,
                        logradouroMatch,
                        bairroMatch
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
