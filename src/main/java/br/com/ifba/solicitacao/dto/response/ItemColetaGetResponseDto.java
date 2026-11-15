package br.com.ifba.solicitacao.dto.response;

import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemColetaGetResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("tipoResiduo")
    private TipoResiduo tipoResiduo;

    @JsonProperty("tipoResiduoDescricao")
    private String tipoResiduoDescricao;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("quantidade")
    private Integer quantidade;

    @JsonProperty("volumeEstimadoM3")
    private Double volumeEstimadoM3;

    public static ItemColetaGetResponseDto fromEntity(ItemColeta item) {
        if (item == null) {
            return null;
        }
        return ItemColetaGetResponseDto.builder()
                .id(item.getId())
                .tipoResiduo(item.getTipoResiduo())
                .tipoResiduoDescricao(item.getTipoResiduo() != null ? item.getTipoResiduo().getDescricao() : null)
                .descricao(item.getDescricao())
                .quantidade(item.getQuantidade())
                .volumeEstimadoM3(item.getVolumeEstimadoM3())
                .build();
    }
}
