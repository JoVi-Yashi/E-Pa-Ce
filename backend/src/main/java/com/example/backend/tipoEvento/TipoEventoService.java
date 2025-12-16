package com.example.backend.tipoEvento;

import com.example.backend.tipoEvento.dto.TipoEventoDTO;
import org.springframework.lang.NonNull;
import java.util.List;

public interface TipoEventoService {
    List<TipoEventoDTO> getAllTiposEvento();

    TipoEventoDTO getTipoEventoById(@NonNull Integer id);

    TipoEventoDTO createTipoEvento(TipoEventoDTO tipoEventoDTO);

    TipoEventoDTO updateTipoEvento(@NonNull Integer id, TipoEventoDTO tipoEventoDTO);

    void deleteTipoEvento(@NonNull Integer id);
}
