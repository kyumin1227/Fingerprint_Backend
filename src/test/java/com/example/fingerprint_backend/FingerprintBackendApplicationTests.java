package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.service.GoogleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SpringBootTest
class FingerprintBackendApplicationTests {

	@Test
	void contextLoads() {
	}

//	구글 토큰 검증 서비스 테스트
	@Test
	void googleTokenCheck() throws IOException, GeneralSecurityException {

		MemberRepository memberRepository = new MemberRepository() {
			@Override
			public Optional<MemberEntity> findByEmail(String email) {
				return Optional.empty();
			}

			@Override
			public Optional<MemberEntity> findByStudentNumber(String studentNumber) {
				return Optional.empty();
			}

			@Override
			public Optional<MemberEntity> findByKakao(String kakao) {
				return Optional.empty();
			}

			@Override
			public void flush() {

			}

			@Override
			public <S extends MemberEntity> S saveAndFlush(S entity) {
				return null;
			}

			@Override
			public <S extends MemberEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
				return null;
			}

			@Override
			public void deleteAllInBatch(Iterable<MemberEntity> entities) {

			}

			@Override
			public void deleteAllByIdInBatch(Iterable<Long> longs) {

			}

			@Override
			public void deleteAllInBatch() {

			}

			@Override
			public MemberEntity getOne(Long aLong) {
				return null;
			}

			@Override
			public MemberEntity getById(Long aLong) {
				return null;
			}

			@Override
			public MemberEntity getReferenceById(Long aLong) {
				return null;
			}

			@Override
			public <S extends MemberEntity> List<S> findAll(Example<S> example) {
				return null;
			}

			@Override
			public <S extends MemberEntity> List<S> findAll(Example<S> example, Sort sort) {
				return null;
			}

			@Override
			public <S extends MemberEntity> List<S> saveAll(Iterable<S> entities) {
				return null;
			}

			@Override
			public List<MemberEntity> findAll() {
				return null;
			}

			@Override
			public List<MemberEntity> findAllById(Iterable<Long> longs) {
				return null;
			}

			@Override
			public <S extends MemberEntity> S save(S entity) {
				return null;
			}

			@Override
			public Optional<MemberEntity> findById(Long aLong) {
				return Optional.empty();
			}

			@Override
			public boolean existsById(Long aLong) {
				return false;
			}

			@Override
			public long count() {
				return 0;
			}

			@Override
			public void deleteById(Long aLong) {

			}

			@Override
			public void delete(MemberEntity entity) {

			}

			@Override
			public void deleteAllById(Iterable<? extends Long> longs) {

			}

			@Override
			public void deleteAll(Iterable<? extends MemberEntity> entities) {

			}

			@Override
			public void deleteAll() {

			}

			@Override
			public List<MemberEntity> findAll(Sort sort) {
				return null;
			}

			@Override
			public Page<MemberEntity> findAll(Pageable pageable) {
				return null;
			}

			@Override
			public <S extends MemberEntity> Optional<S> findOne(Example<S> example) {
				return Optional.empty();
			}

			@Override
			public <S extends MemberEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
				return null;
			}

			@Override
			public <S extends MemberEntity> long count(Example<S> example) {
				return 0;
			}

			@Override
			public <S extends MemberEntity> boolean exists(Example<S> example) {
				return false;
			}

			@Override
			public <S extends MemberEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
				return null;
			}
		};

		GoogleService googleService = new GoogleService(memberRepository);

		String credential = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjM2UzZTU1ODExMWM3YzdhNzVjNWI2NTEzNGQyMmY2M2VlMDA2ZDAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0NDE3ODg3Njc3ODItMTgzbmRlYnA3YWRnN2RzaWdqcW9mcGo1NmJiN2MzbXAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0NDE3ODg3Njc3ODItMTgzbmRlYnA3YWRnN2RzaWdqcW9mcGo1NmJiN2MzbXAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDcwMTE0MjQ3OTc3NjI5NDI5NzIiLCJoZCI6ImcueWp1LmFjLmtyIiwiZW1haWwiOiJreXVtaW4xMjI3MTIyN0BnLnlqdS5hYy5rciIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYmYiOjE3MTQ2NDMwMjYsIm5hbWUiOiLquYDqt5zrr7wiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jSzNwOGxiUFAwMjhIekc1YURYOG15Mjl4UndwN0FHaUc0OVdTa0QtejdPb2R1UjlBPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Iuq3nOuvvCIsImZhbWlseV9uYW1lIjoi6rmAIiwiaWF0IjoxNzE0NjQzMzI2LCJleHAiOjE3MTQ2NDY5MjYsImp0aSI6IjY4ZjA4NDM2ZDQyYjI5MWZkYTNjZGMwOWE0NzdjZDcwMTg4YzBjNmEifQ.Lua7p21biqHHn668GSO_LGqB9vzYAjy_8-bZoc6d5pGo077tb6Uz8eVcss24zv3voDNueg6W-iHN2wIJG0W4kzeurtD4GFpnU6VqdNeTxBr9sPJYccMbWbbmqJqDcPDUBh3_760RPCA5ARUckvVREZz1Cds-EfeM8GbcEK1lor4sxLAOisRM3uuCqQRhpRtzdkX8uned8L_z84nPY0qBbq5QnaA7-WdggdpoQG7GTJA_V7n0ZxH0dAWfakQ3JbcJANlh0nkTLDPcR_7TJHxyvCAD78G-PHdZY7SYBC0oXXchTFjBFuXhDVtdg_Lk1m8AqNYyykJmhfMNPvra9tSAug";

		googleService.googleTokenCheck(credential);
	}

}
