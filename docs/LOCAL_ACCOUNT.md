# 마이페이지 및 내 정보 조회

고객 프론트의 `/mypage`에 장바구니·내 정보 보기·주문정보(준비 중) 메뉴를 제공합니다. `/user/profile`에서 회원정보를 조회한 뒤 수정 버튼으로 기존 비밀번호 재확인 → 수정 절차를 진행합니다. 주문정보는 비활성 버튼이며 주문 API는 추가하지 않습니다.

`GET /api/user/info`는 기존 Authorization 및 서비스 키 헤더를 사용하는 USER 전용 조회 API입니다. 검증된 토큰의 회원 아이디로 조회하며 요청의 회원 번호를 받지 않습니다. 응답 필드는 `userId`, `userName`, `userPhone`만 포함하고 `Cache-Control: no-store`를 반환합니다. 비밀번호나 회원 내부 번호는 반환하지 않습니다.

기존 `POST /api/user/password-confirm`, `PUT /api/user/info`의 비밀번호 검증과 수정 계약은 유지합니다. DB 스키마 변경은 없습니다. 로컬 개발 범위이며 운영 배포는 별도 승인이 필요합니다.
