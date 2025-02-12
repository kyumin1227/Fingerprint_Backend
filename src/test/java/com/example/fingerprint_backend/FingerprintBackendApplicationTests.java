package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.KakaoEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.KakaoRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.service.GoogleService;
import com.example.fingerprint_backend.service.KakaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@SpringBootTest
// 환경 변수 추가 필요 ("DB_URL, DB_USER, DB_PASSWORD")
@TestPropertySource(properties = {

})
class FingerprintBackendApplicationTests {

	@Autowired
	private KakaoService kakaoService;

	@Autowired
	private KakaoRepository kakaoRepository;

	@Autowired
	private DateRepository dateRepository;


	@Test
	void contextLoads() {
	}

	@Test
	public void checkKakao() {

		Optional<KakaoEntity> admin = kakaoRepository.findById("0");

		String refreshToken = admin.get().getRefreshToken();

		String accessToken = kakaoService.getAccessToken(refreshToken);

		System.out.println("accessToken = " + accessToken);

		kakaoService.sendKakaoMessage(accessToken, "안녕하세요", "Lx0kFCwUIREjDz4GMAQ3ADkNOhYnFyARKR0vfg");

	}

	@Test
	@Transactional
	public void getDate() {

		Optional<DateEntity> byId = dateRepository.findById(LocalDate.now());

		Set<String> membersList = byId.get().getMembers();

		System.out.println("membersList.size() = " + membersList.size());

		for (String stdNum : membersList) {
			System.out.println("stdNUm = " + stdNum);
		}
	}

	@Test
	@Transactional
	public void setAccessToken() {

		Optional<KakaoEntity> admin = kakaoRepository.findById("0");

		String refreshToken = admin.get().getRefreshToken();
		System.out.println("accessToken = " + admin.get().getAccessToken());

		String accessToken = kakaoService.getAccessToken(refreshToken);

		System.out.println("newAccessToken = " + accessToken);

		admin.get().setAccessToken(accessToken);

		System.out.println("admin.get().getAccessToken() = " + admin.get().getAccessToken());

		KakaoEntity saved = kakaoRepository.save(admin.get());

		if (saved.getAccessToken().equals(accessToken)) {
			kakaoService.sendKakaoMessage(accessToken, "토큰 갱신 성공", saved.getUuid());
		}
	}

}
