# 회원 장바구니

로컬 개발용 기능이며 운영 DB 적용 및 배포는 별도 승인이 필요합니다.

## DB

`src/main/resources/db/local/cart_schema.sql`을 로컬 DB에 실행합니다. 부모 테이블 `user_info.USER_NO`는 INT, `goods_info.GOODS_NO`는 BIGINT이어야 합니다. `cart_item`의 회원·상품 복합 기본 키가 중복 행을 방지합니다. 기존 회원·상품 테이블은 변경하지 않습니다.

## API

모든 요청은 기존 Authorization 토큰과 x-auth-user-service-key 헤더를 사용하며 USER 역할만 허용합니다. 회원 번호는 요청에서 받지 않고 검증된 세션에서 식별합니다.

| 메서드 | 경로 | 동작 |
| --- | --- | --- |
| GET | /api/user/cart | 본인 상품 목록과 현재 DB 가격·판매 가능 여부 조회 |
| POST | /api/user/cart/{goodsNo} | `{ "quantity": 1 }` 수량 추가, 기존 상품이면 합산 |
| PUT | /api/user/cart/{goodsNo} | `{ "quantity": 1 }` 수량 교체 |
| DELETE | /api/user/cart/{goodsNo} | 본인 상품 삭제, 이미 없으면 성공 |

수량은 양수이며 담기·변경 시 현재 재고를 초과할 수 없습니다. 상품 행을 먼저 잠가 동시 담기·변경의 수량 유실을 방지합니다. 장바구니는 재고를 예약하지 않습니다. 품절·비공개·전시기간 외 상품은 구매 불가로 반환하며 비공개 상품 상세 정보는 노출하지 않습니다. 가격은 클라이언트 입력을 사용하지 않습니다.

화면 합계는 구매 가능한 재고 이내 상품 금액이며 배송비를 포함하지 않습니다. 주문·결제는 범위에 포함하지 않습니다. 비회원은 로그인 후 장바구니 또는 원래 상품 상세로 복귀합니다.

## 검증

- `sh ./gradlew test`: 서비스·컨트롤러 단위 테스트와 기존 테스트
- `RUN_CART_INTEGRATION=true sh ./gradlew test --tests '*Cart*'`: 로컬 DB·MyBatis·Spring Security 통합 검증. 로컬 접속임을 먼저 확인하고 생성한 회원·장바구니 및 상품 상태 변경은 테스트 트랜잭션으로 롤백합니다. 토큰 검증기는 대체하므로 실제 Redis 로그인 토큰 발급은 별도 검증 범위입니다.
