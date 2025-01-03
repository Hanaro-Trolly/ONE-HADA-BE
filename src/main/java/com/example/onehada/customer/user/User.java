package com.example.onehada.customer.user;

import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "user_name", length = 127, nullable = false)
	private String userName;

	@Column(name = "user_gender", length = 1, nullable = false)
	private String userGender;

	@Column(name = "user_email", length = 127, nullable = false)
	private String userEmail;

	@Column(name = "phone_number", length = 20, nullable = false)
	private String phoneNumber;

	@Column(name = "user_address")
	private String userAddress;

	@Column(name = "user_birth", length = 8, nullable = false)
	private String userBirth;

	@CreationTimestamp
	@Column(name = "user_registered_date", nullable = false, updatable = false)
	private LocalDate userRegisteredDate;

	@Column(name = "user_google_id")
	private String userGoogleId;

	@Column(name = "user_kakao_id")
	private String userKakaoId;

	@Column(name = "user_naver_id")
	private String userNaverId;

	@Column(name = "simple_password", length = 8, nullable = false)
	private String simplePassword;

}
