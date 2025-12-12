package com.example.backend.checkin;

import com.example.backend.checkin.dto.CheckInRequest;
import com.example.backend.checkin.dto.CheckInResponse;
import java.util.List;

public interface CheckInService {
    List<CheckInResponse> getAllCheckIns();

    CheckInResponse getCheckInById(Integer id);

    CheckInResponse createCheckIn(CheckInRequest request);

    void deleteCheckIn(Integer id);

    List<CheckInResponse> getCheckInsByEvento(Integer eventoId);

    CheckInResponse realizarCheckInPorCodigo(String codigoUnicoAPI, String ipAddress);
}
