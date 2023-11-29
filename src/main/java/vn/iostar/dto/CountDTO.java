package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountDTO {
	private long countToday;
	private long countInWeek;
	private long countIn1Month;
	private long countIn3Month;
	private long countIn6Month;
	private long countIn9Month;
	private long countIn1Year;
}
