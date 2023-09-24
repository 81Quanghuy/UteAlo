package vn.iostar.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.RefreshToken;

public interface RefreshTokenService {
	<S extends RefreshToken> S save(S entity);

	ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken);

	void revokeRefreshToken(String userId);

	ResponseEntity<?> logout(String refreshToken);
}
