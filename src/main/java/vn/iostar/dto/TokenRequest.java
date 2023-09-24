package vn.iostar.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequest {
	private String accessToken;
	private String refreshToken;
}
