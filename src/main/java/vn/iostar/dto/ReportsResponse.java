package vn.iostar.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Report;

@Data
public class ReportsResponse {
	
	private int reportId;
	private Date postTime;
	private String content;
	private String photos;
	private String files;
	private String userName;
	private String avatarUser;
	private RoleName roleName;
	private PrivacyLevel privacyLevel;
	
	public ReportsResponse(Report report) {
		this.privacyLevel = report.getPrivacyLevel();
		this.reportId = report.getReportId();
		this.content = report.getContent();
		this.files = report.getFiles();
		this.photos = report.getPhotos();
		this.userName = report.getUser().getUserName();
		this.avatarUser = report.getUser().getProfile().getAvatar();
		this.roleName = report.getUser().getRole().getRoleName();
	}
}
