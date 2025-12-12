package com.example.backend.tipoEvento;

import com.example.backend.tipoEvento.dto.TipoEventoDTO;
import java.util.List;

public interface TipoEventoService {
    List<TipoEventoDTO> getAllTiposEvento();

    TipoEventoDTO getTipoEventoById(Integer id);

    TipoEventoDTO createTipoEvento(TipoEventoDTO tipoEventoDTO);

    TipoEventoDTO updateTipoEvento(Integer id, TipoEventoDTO tipoEventoDTO);

    void deleteTipoEvento(Integer id);
}
