package com.example.fingerprint_backend.domain.fingerprint.util;


import java.util.List;

public final class RankingCalculator {

    private RankingCalculator() {
        throw new AssertionError("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    /**
     * 특정 클래스의 리스트로 변환
     *
     * @param list  변환할 리스트
     * @param <T>   변환할 클래스
     * @param clazz 변환할 클래스
     * @return 변환된 리스트
     */
    public static <T> List<T> convertListType(List<?> list, Class<T> clazz) {

        if (list == null) {
            return List.of();
        }

        return list.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();

    }
}
