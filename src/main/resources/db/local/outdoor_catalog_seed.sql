-- 폴라베어상점 아웃도어 카탈로그 로컬 시드
-- 기존 상품과 스키마를 유지하면서 브랜드, 계층형 카테고리, 상품 100개를 추가합니다.
-- 같은 GOODS_CODE와 브랜드명은 갱신하고 카테고리 및 상품 이미지는 중복 생성하지 않습니다.

START TRANSACTION;

INSERT INTO brand_info (BRAND_NAME_KO, BRAND_NAME_EN, BRAND_COUNTRY, DISPLAY_YN, USE_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
VALUES
    ('폴라', 'POLA', '노르웨이', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('그리즐리', 'GRIZZLY', '캐나다', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('코디악', 'KODIAK', '미국', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('브라운베어', 'BROWN BEAR', '핀란드', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('그린란드', 'GREENLAND', '그린란드', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('핀란드', 'FINLAND', '핀란드', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('노르웨이', 'NORWAY', '노르웨이', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('스웨덴', 'SWEDEN', '스웨덴', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('아이슬란드', 'ICELAND', '아이슬란드', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('캐나다', 'CANADA', '캐나다', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('알래스카', 'ALASKA', '미국', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('라플란드', 'LAPLAND', '핀란드', 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed')
ON DUPLICATE KEY UPDATE
    BRAND_NAME_EN = VALUES(BRAND_NAME_EN),
    BRAND_COUNTRY = VALUES(BRAND_COUNTRY),
    DISPLAY_YN = 'Y',
    USE_YN = 'Y',
    UPDT_DATE = NOW(),
    UPDT_ID = 'catalog_seed';

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '캠핑', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '캠핑' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '텐트/타프', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '텐트/타프' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '침낭/매트', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '침낭/매트' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '캠핑가구', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '캠핑가구' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '조명/랜턴', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '조명/랜턴' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '화로/난방', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '화로/난방' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '취사/식기', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '취사/식기' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '수납/운반', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '수납/운반' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '전기/충전', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '캠핑' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '전기/충전' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '등산/트레킹', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '등산/트레킹' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '등산가방', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '등산가방' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '등산화', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '등산화' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '등산의류', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '등산의류' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '트레킹 스틱', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '트레킹 스틱' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '아이젠/스패츠', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '아이젠/스패츠' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '헤드램프', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '헤드램프' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '안전/응급', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '안전/응급' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '등산소품', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '등산/트레킹' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '등산소품' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚시', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '낚시' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚싯대', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '낚싯대' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '릴', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '릴' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚싯줄/채비', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '낚싯줄/채비' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '루어/미끼', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '루어/미끼' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚시가방/박스', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '낚시가방/박스' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚시의자/받침틀', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '낚시의자/받침틀' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '낚시의류', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '낚시의류' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '어군탐지/계측', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '낚시' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '어군탐지/계측' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '수상/해양', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '수상/해양' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '카약/SUP', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '수상/해양' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '카약/SUP' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '구명조끼', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '수상/해양' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '구명조끼' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '방수백', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '수상/해양' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '방수백' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '스노클링', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '수상/해양' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '스노클링' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '해양안전', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '수상/해양' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '해양안전' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '피크닉/여행', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '피크닉/여행' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '피크닉매트', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '피크닉/여행' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '피크닉매트' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '쿨러/보냉', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '피크닉/여행' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '쿨러/보냉' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '해먹', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '피크닉/여행' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '해먹' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '여행가방', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '피크닉/여행' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '여행가방' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '보온병', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '피크닉/여행' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '보온병' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '아웃도어 의류', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '아웃도어 의류' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '재킷', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 의류' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '재킷' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '베이스레이어', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 의류' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '베이스레이어' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '팬츠', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 의류' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '팬츠' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '모자/장갑', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 의류' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '모자/장갑' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '아웃도어 신발', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '아웃도어 신발' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '하이킹화', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 신발' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '하이킹화' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '캠핑슈즈', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 신발' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '캠핑슈즈' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '레인부츠', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '아웃도어 신발' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '레인부츠' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '안전/서바이벌', 1, NULL, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
WHERE NOT EXISTS (
    SELECT 1 FROM category_info WHERE CATEGORY_NAME = '안전/서바이벌' AND CATEGORY_DEPTH = 1 AND PARENT_CATEGORY_NO IS NULL
);

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '응급키트', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '안전/서바이벌' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '응급키트' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '나이프/멀티툴', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '안전/서바이벌' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '나이프/멀티툴' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '나침반/GPS', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '안전/서바이벌' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '나침반/GPS' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO category_info (CATEGORY_NAME, CATEGORY_DEPTH, PARENT_CATEGORY_NO, USE_YN, DISPLAY_YN, UPDT_DATE, UPDT_ID, REG_DATE, REG_ID)
SELECT '방한/보온', 2, P.CATEGORY_NO, 'Y', 'Y', NOW(), 'catalog_seed', NOW(), 'catalog_seed'
FROM category_info P
WHERE P.CATEGORY_NAME = '안전/서바이벌' AND P.CATEGORY_DEPTH = 1 AND P.PARENT_CATEGORY_NO IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM category_info C WHERE C.CATEGORY_NAME = '방한/보온' AND C.CATEGORY_DEPTH = 2 AND C.PARENT_CATEGORY_NO = P.CATEGORY_NO
  )
LIMIT 1;

INSERT INTO goods_info (
    GOODS_CODE, GOODS_NAME_KO, GOODS_NAME_EN, BRAND_NO, CATEGORY_NO,
    SELLER_NAME, MANUFACTURER, ORIGIN_COUNTRY, COST_PRICE, SALE_PRICE, DISCOUNT_PRICE,
    STOCK_QUANTITY, SAFETY_STOCK_QUANTITY, TAX_TYPE, SHIPPING_TYPE, SHIPPING_FEE,
    GOODS_STATUS, USE_YN, DISPLAY_YN, DISPLAY_START_DATE, DISPLAY_END_DATE,
    SHORT_DESCRIPTION, DETAIL_DESCRIPTION, SEARCH_KEYWORDS, REG_DATE, REG_ID, UPDT_DATE, UPDT_ID
)
VALUES
    ('PB-OUT-001', '설원 돔 텐트 2인', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '텐트/타프' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 16800, 29000, 25500,
        20, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '설원 돔 텐트 2인은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 텐트/타프, 설원 돔 텐트 2인, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-002', '툰드라 리빙쉘 텐트 4인', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '텐트/타프' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 22600, 39000, NULL,
        21, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '툰드라 리빙쉘 텐트 4인은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 텐트/타프, 툰드라 리빙쉘 텐트 4인, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-003', '오로라 헥사 타프', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '텐트/타프' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 28400, 49000, NULL,
        22, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '오로라 헥사 타프은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 텐트/타프, 오로라 헥사 타프, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-004', '빙하 원터치 텐트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '텐트/타프' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 34200, 59000, NULL,
        23, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '빙하 원터치 텐트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 텐트/타프, 빙하 원터치 텐트, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-005', '피오르드 차박 어닝', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '텐트/타프' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 40000, 69000, 60700,
        24, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '피오르드 차박 어닝은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 텐트/타프, 피오르드 차박 어닝, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-006', '라플란드 머미 침낭 800', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '침낭/매트' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 45800, 79000, NULL,
        25, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 머미 침낭 800은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 침낭/매트, 라플란드 머미 침낭 800, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-007', '아틱 사계절 침낭 500', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '침낭/매트' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 51600, 89000, NULL,
        26, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아틱 사계절 침낭 500은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 침낭/매트, 아틱 사계절 침낭 500, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-008', '그린란드 자충 매트 싱글', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '침낭/매트' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 57400, 99000, NULL,
        27, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 자충 매트 싱글은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 침낭/매트, 그린란드 자충 매트 싱글, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-009', '폴라 에어 매트 더블', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '침낭/매트' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 63200, 109000, 95900,
        28, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 에어 매트 더블은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 침낭/매트, 폴라 에어 매트 더블, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-010', '스노우 폴딩 폼 매트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '침낭/매트' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 69000, 119000, NULL,
        29, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스노우 폴딩 폼 매트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 침낭/매트, 스노우 폴딩 폼 매트, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-011', '코디악 릴렉스 체어', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑가구' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 19700, 34000, NULL,
        30, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 릴렉스 체어은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 캠핑가구, 코디악 릴렉스 체어, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-012', '그리즐리 경량 체어', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑가구' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 25500, 44000, NULL,
        31, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 경량 체어은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 캠핑가구, 그리즐리 경량 체어, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-013', '노르딕 IGT 테이블', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑가구' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 31300, 54000, 47500,
        32, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르딕 IGT 테이블은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 캠핑가구, 노르딕 IGT 테이블, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-014', '툰드라 롤 테이블', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑가구' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 37100, 64000, NULL,
        33, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '툰드라 롤 테이블은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 캠핑가구, 툰드라 롤 테이블, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-015', '캠프 키친 스탠드', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑가구' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 42900, 74000, NULL,
        34, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캠프 키친 스탠드은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 캠핑가구, 캠프 키친 스탠드, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-016', '오로라 충전식 랜턴', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '조명/랜턴' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 48700, 84000, NULL,
        35, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '오로라 충전식 랜턴은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 조명/랜턴, 오로라 충전식 랜턴, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-017', '폴라 미니 랜턴', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '조명/랜턴' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 54500, 94000, 82700,
        36, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 미니 랜턴은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 조명/랜턴, 폴라 미니 랜턴, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-018', '피오르드 LED 스트링 라이트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '조명/랜턴' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 60300, 104000, NULL,
        37, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '피오르드 LED 스트링 라이트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 조명/랜턴, 피오르드 LED 스트링 라이트, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-019', '그리즐리 스테인리스 화로대', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '화로/난방' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 66100, 114000, NULL,
        38, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 스테인리스 화로대은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 화로/난방, 그리즐리 스테인리스 화로대, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-020', '라플란드 휴대용 히터', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '화로/난방' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 71900, 124000, NULL,
        39, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 휴대용 히터은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 화로/난방, 라플란드 휴대용 히터, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-021', '노르웨이 코펠 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '취사/식기' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 22600, 39000, 34300,
        40, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 코펠 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 취사/식기, 노르웨이 코펠 세트, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-022', '알래스카 캠핑 주전자', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '취사/식기' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 28400, 49000, NULL,
        41, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '알래스카 캠핑 주전자은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 취사/식기, 알래스카 캠핑 주전자, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-023', '폴라 티타늄 머그', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '취사/식기' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 34200, 59000, NULL,
        42, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 티타늄 머그은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 취사/식기, 폴라 티타늄 머그, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-024', '브라운베어 버너 테이블', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '취사/식기' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 40000, 69000, NULL,
        43, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '브라운베어 버너 테이블은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 취사/식기, 브라운베어 버너 테이블, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-025', '코디악 폴딩 웨건', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '수납/운반' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 45800, 79000, 69500,
        44, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 폴딩 웨건은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 수납/운반, 코디악 폴딩 웨건, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-026', '그린란드 멀티 수납박스', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '수납/운반' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 51600, 89000, NULL,
        45, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 멀티 수납박스은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 수납/운반, 그린란드 멀티 수납박스, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-027', '스웨덴 캠핑 기어백', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '수납/운반' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 57400, 99000, NULL,
        46, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 캠핑 기어백은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 수납/운반, 스웨덴 캠핑 기어백, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-028', '아이슬란드 파워뱅크 600', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '전기/충전' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 63200, 109000, NULL,
        47, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 파워뱅크 600은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 전기/충전, 아이슬란드 파워뱅크 600, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-029', '폴라 태양광 충전 패널', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '전기/충전' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 69000, 119000, 104700,
        48, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 태양광 충전 패널은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 전기/충전, 폴라 태양광 충전 패널, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-030', '캐나다 캠핑 연장선 20m', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '전기/충전' AND P.CATEGORY_NAME = '캠핑' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 74800, 129000, NULL,
        49, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '캠핑 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 캠핑 연장선 20m은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '캠핑, 전기/충전, 캐나다 캠핑 연장선 20m, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-031', '서밋 하이킹 백팩 25L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산가방' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 25500, 44000, NULL,
        50, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '서밋 하이킹 백팩 25L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산가방, 서밋 하이킹 백팩 25L, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-032', '툰드라 트레킹 백팩 38L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산가방' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 31300, 54000, NULL,
        20, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '툰드라 트레킹 백팩 38L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산가방, 툰드라 트레킹 백팩 38L, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-033', '코디악 어택 백팩 55L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산가방' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 37100, 64000, 56300,
        21, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 어택 백팩 55L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산가방, 코디악 어택 백팩 55L, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-034', '폴라 트레일 힙색', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산가방' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 42900, 74000, NULL,
        22, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 트레일 힙색은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산가방, 폴라 트레일 힙색, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-035', '피오르드 미드컷 등산화', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산화' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 48700, 84000, NULL,
        23, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '피오르드 미드컷 등산화은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산화, 피오르드 미드컷 등산화, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-036', '그리즐리 방수 트레킹화', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산화' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 54500, 94000, NULL,
        24, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 방수 트레킹화은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산화, 그리즐리 방수 트레킹화, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-037', '라플란드 윈드 셸 재킷', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산의류' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 60300, 104000, 91500,
        25, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 윈드 셸 재킷은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산의류, 라플란드 윈드 셸 재킷, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-038', '노르웨이 알파인 베스트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산의류' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 66100, 114000, NULL,
        26, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 알파인 베스트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산의류, 노르웨이 알파인 베스트, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-039', '폴라 카본 트레킹 폴', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '트레킹 스틱' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 71900, 124000, NULL,
        27, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 카본 트레킹 폴은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 트레킹 스틱, 폴라 카본 트레킹 폴, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-040', '브라운베어 3단 스틱 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '트레킹 스틱' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 77700, 134000, NULL,
        28, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '브라운베어 3단 스틱 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 트레킹 스틱, 브라운베어 3단 스틱 세트, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-041', '그린란드 체인 아이젠', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '아이젠/스패츠' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 28400, 49000, 43100,
        29, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 체인 아이젠은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 아이젠/스패츠, 그린란드 체인 아이젠, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-042', '알래스카 방수 스패츠', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '아이젠/스패츠' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 34200, 59000, NULL,
        30, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '알래스카 방수 스패츠은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 아이젠/스패츠, 알래스카 방수 스패츠, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-043', '오로라 500루멘 헤드램프', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '헤드램프' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 40000, 69000, NULL,
        31, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '오로라 500루멘 헤드램프은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 헤드램프, 오로라 500루멘 헤드램프, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-044', '아이슬란드 센서 헤드램프', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '헤드램프' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 45800, 79000, NULL,
        32, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 센서 헤드램프은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 헤드램프, 아이슬란드 센서 헤드램프, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-045', '캐나다 등산 응급 파우치', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '안전/응급' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 51600, 89000, 78300,
        33, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 등산 응급 파우치은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 안전/응급, 캐나다 등산 응급 파우치, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-046', '코디악 베어벨 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '안전/응급' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 57400, 99000, NULL,
        34, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 베어벨 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 안전/응급, 코디악 베어벨 세트, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-047', '스웨덴 트레일 게이터', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산소품' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 63200, 109000, NULL,
        35, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 트레일 게이터은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산소품, 스웨덴 트레일 게이터, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-048', '폴라 스테인리스 카라비너', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산소품' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 69000, 119000, NULL,
        36, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 스테인리스 카라비너은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산소품, 폴라 스테인리스 카라비너, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-049', '라플란드 넥 게이터', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산소품' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 74800, 129000, 113500,
        37, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 넥 게이터은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산소품, 라플란드 넥 게이터, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-050', '그리즐리 방수 지도 케이스', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '등산소품' AND P.CATEGORY_NAME = '등산/트레킹' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 80600, 139000, NULL,
        38, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '등산/트레킹 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 방수 지도 케이스은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '등산/트레킹, 등산소품, 그리즐리 방수 지도 케이스, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-051', '폴라 라이트 루어 로드', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯대' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 31300, 54000, NULL,
        39, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 라이트 루어 로드은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯대, 폴라 라이트 루어 로드, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-052', '코디악 씨배스 로드', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯대' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 37100, 64000, NULL,
        40, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 씨배스 로드은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯대, 코디악 씨배스 로드, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-053', '그린란드 선상 지깅 로드', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯대' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 42900, 74000, 65100,
        41, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 선상 지깅 로드은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯대, 그린란드 선상 지깅 로드, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-054', '그리즐리 민물 낚싯대 3.2m', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯대' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 48700, 84000, NULL,
        42, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 민물 낚싯대 3.2m은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯대, 그리즐리 민물 낚싯대 3.2m, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-055', '노르웨이 스피닝 릴 2500', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '릴' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 54500, 94000, NULL,
        43, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 스피닝 릴 2500은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 릴, 노르웨이 스피닝 릴 2500, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-056', '알래스카 베이트 릴 150', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '릴' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 60300, 104000, NULL,
        44, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '알래스카 베이트 릴 150은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 릴, 알래스카 베이트 릴 150, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-057', '스웨덴 서프 릴 5000', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '릴' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 66100, 114000, 100300,
        45, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 서프 릴 5000은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 릴, 스웨덴 서프 릴 5000, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-058', '아이슬란드 합사 라인 150m', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯줄/채비' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 71900, 124000, NULL,
        46, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 합사 라인 150m은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯줄/채비, 아이슬란드 합사 라인 150m, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-059', '폴라 카본 쇼크리더', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯줄/채비' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 77700, 134000, NULL,
        47, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 카본 쇼크리더은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯줄/채비, 폴라 카본 쇼크리더, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-060', '캐나다 바다 채비 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚싯줄/채비' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 83500, 144000, NULL,
        48, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 바다 채비 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚싯줄/채비, 캐나다 바다 채비 세트, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-061', '오로라 미노우 루어 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '루어/미끼' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 34200, 59000, 51900,
        49, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '오로라 미노우 루어 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 루어/미끼, 오로라 미노우 루어 세트, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-062', '그리즐리 메탈 지그 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '루어/미끼' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 40000, 69000, NULL,
        50, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 메탈 지그 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 루어/미끼, 그리즐리 메탈 지그 세트, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-063', '라플란드 소프트베이트 팩', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '루어/미끼' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 45800, 79000, NULL,
        20, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 소프트베이트 팩은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 루어/미끼, 라플란드 소프트베이트 팩, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-064', '코디악 태클박스 4단', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시가방/박스' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 51600, 89000, NULL,
        21, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 태클박스 4단은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시가방/박스, 코디악 태클박스 4단, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-065', '브라운베어 로드 케이스', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시가방/박스' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 57400, 99000, 87100,
        22, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '브라운베어 로드 케이스은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시가방/박스, 브라운베어 로드 케이스, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-066', '그린란드 방수 태클백', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시가방/박스' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 63200, 109000, NULL,
        23, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 방수 태클백은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시가방/박스, 그린란드 방수 태클백, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-067', '폴라 컴팩트 낚시의자', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시의자/받침틀' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 69000, 119000, NULL,
        24, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 컴팩트 낚시의자은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시의자/받침틀, 폴라 컴팩트 낚시의자, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-068', '핀란드 원터치 받침틀', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시의자/받침틀' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 74800, 129000, NULL,
        25, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '핀란드 원터치 받침틀은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시의자/받침틀, 핀란드 원터치 받침틀, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-069', '노르웨이 방수 낚시 재킷', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '낚시의류' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 80600, 139000, 122300,
        26, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 방수 낚시 재킷은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 낚시의류, 노르웨이 방수 낚시 재킷, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-070', '알래스카 휴대용 어군탐지기', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '어군탐지/계측' AND P.CATEGORY_NAME = '낚시' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 86400, 149000, NULL,
        27, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '낚시 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '알래스카 휴대용 어군탐지기은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '낚시, 어군탐지/계측, 알래스카 휴대용 어군탐지기, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-071', '그린란드 투어링 카약', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '카약/SUP' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 37100, 64000, NULL,
        28, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 투어링 카약은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 카약/SUP, 그린란드 투어링 카약, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-072', '폴라 인플레이터블 SUP', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '카약/SUP' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 42900, 74000, NULL,
        29, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 인플레이터블 SUP은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 카약/SUP, 폴라 인플레이터블 SUP, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-073', '노르웨이 어드벤처 구명조끼', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '구명조끼' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 48700, 84000, 73900,
        30, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 어드벤처 구명조끼은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 구명조끼, 노르웨이 어드벤처 구명조끼, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-074', '코디악 낚시 구명베스트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '구명조끼' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 54500, 94000, NULL,
        31, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 낚시 구명베스트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 구명조끼, 코디악 낚시 구명베스트, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-075', '아이슬란드 드라이백 20L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '방수백' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 60300, 104000, NULL,
        32, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 드라이백 20L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 방수백, 아이슬란드 드라이백 20L, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-076', '그리즐리 롤탑 드라이백 35L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '방수백' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 66100, 114000, NULL,
        33, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 롤탑 드라이백 35L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 방수백, 그리즐리 롤탑 드라이백 35L, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-077', '스웨덴 스노클링 세트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '스노클링' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 71900, 124000, 109100,
        34, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 스노클링 세트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 스노클링, 스웨덴 스노클링 세트, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-078', '핀란드 안티포그 마스크', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '스노클링' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 77700, 134000, NULL,
        35, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '핀란드 안티포그 마스크은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 스노클링, 핀란드 안티포그 마스크, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-079', '캐나다 해양 비상 키트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '해양안전' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 83500, 144000, NULL,
        36, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 해양 비상 키트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 해양안전, 캐나다 해양 비상 키트, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-080', '알래스카 방수 구조 로프', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '해양안전' AND P.CATEGORY_NAME = '수상/해양' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 89300, 154000, NULL,
        37, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '수상/해양 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '알래스카 방수 구조 로프은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '수상/해양, 해양안전, 알래스카 방수 구조 로프, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-081', '라플란드 방수 피크닉 매트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '피크닉매트' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 40000, 69000, 60700,
        38, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 방수 피크닉 매트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 피크닉매트, 라플란드 방수 피크닉 매트, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-082', '폴라 패딩 피크닉 매트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '피크닉매트' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 45800, 79000, NULL,
        39, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 패딩 피크닉 매트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 피크닉매트, 폴라 패딩 피크닉 매트, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-083', '코디악 하드 쿨러 28L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '쿨러/보냉' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 51600, 89000, NULL,
        40, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 하드 쿨러 28L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 쿨러/보냉, 코디악 하드 쿨러 28L, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-084', '그리즐리 소프트 쿨러 18L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '쿨러/보냉' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 57400, 99000, NULL,
        41, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 소프트 쿨러 18L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 쿨러/보냉, 그리즐리 소프트 쿨러 18L, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-085', '그린란드 경량 해먹', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '해먹' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 63200, 109000, 95900,
        42, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 경량 해먹은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 해먹, 그린란드 경량 해먹, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-086', '노르웨이 더블 해먹', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '해먹' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 69000, 119000, NULL,
        43, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 더블 해먹은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 해먹, 노르웨이 더블 해먹, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-087', '아이슬란드 더플백 45L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '여행가방' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 74800, 129000, NULL,
        44, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 더플백 45L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 여행가방, 아이슬란드 더플백 45L, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-088', '스웨덴 폴더블 트래블백', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '여행가방' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 80600, 139000, NULL,
        45, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 폴더블 트래블백은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 여행가방, 스웨덴 폴더블 트래블백, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-089', '핀란드 진공 보온병 750ml', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그린란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '보온병' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '그린란드', '그린란드', 86400, 149000, 131100,
        46, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '핀란드 진공 보온병 750ml은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 보온병, 핀란드 진공 보온병 750ml, 그린란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-090', '캐나다 와이드 보온병 1L', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '핀란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '보온병' AND P.CATEGORY_NAME = '피크닉/여행' LIMIT 1),
        '폴라베어상점', '핀란드', '핀란드', 92200, 159000, NULL,
        47, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '피크닉/여행 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 와이드 보온병 1L은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '피크닉/여행, 보온병, 캐나다 와이드 보온병 1L, 핀란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-091', '폴라 스톰 방수 재킷', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '노르웨이' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '재킷' AND P.CATEGORY_NAME = '아웃도어 의류' LIMIT 1),
        '폴라베어상점', '노르웨이', '노르웨이', 42900, 74000, NULL,
        48, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 의류 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '폴라 스톰 방수 재킷은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 의류, 재킷, 폴라 스톰 방수 재킷, 노르웨이, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-092', '라플란드 메리노 베이스레이어', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '스웨덴' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '베이스레이어' AND P.CATEGORY_NAME = '아웃도어 의류' LIMIT 1),
        '폴라베어상점', '스웨덴', '스웨덴', 48700, 84000, NULL,
        49, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 의류 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '라플란드 메리노 베이스레이어은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 의류, 베이스레이어, 라플란드 메리노 베이스레이어, 스웨덴, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-093', '그리즐리 스트레치 트레킹 팬츠', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '아이슬란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '팬츠' AND P.CATEGORY_NAME = '아웃도어 의류' LIMIT 1),
        '폴라베어상점', '아이슬란드', '아이슬란드', 54500, 94000, 82700,
        50, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 의류 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그리즐리 스트레치 트레킹 팬츠은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 의류, 팬츠, 그리즐리 스트레치 트레킹 팬츠, 아이슬란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-094', '노르웨이 플리스 장갑', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '캐나다' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '모자/장갑' AND P.CATEGORY_NAME = '아웃도어 의류' LIMIT 1),
        '폴라베어상점', '캐나다', '캐나다', 60300, 104000, NULL,
        20, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 의류 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '노르웨이 플리스 장갑은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 의류, 모자/장갑, 노르웨이 플리스 장갑, 캐나다, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-095', '코디악 로우컷 하이킹화', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '알래스카' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '하이킹화' AND P.CATEGORY_NAME = '아웃도어 신발' LIMIT 1),
        '폴라베어상점', '알래스카', '미국', 66100, 114000, NULL,
        21, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 신발 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '코디악 로우컷 하이킹화은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 신발, 하이킹화, 코디악 로우컷 하이킹화, 알래스카, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-096', '그린란드 인슐레이티드 캠핑슈즈', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '라플란드' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '캠핑슈즈' AND P.CATEGORY_NAME = '아웃도어 신발' LIMIT 1),
        '폴라베어상점', '라플란드', '핀란드', 71900, 124000, NULL,
        22, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 신발 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '그린란드 인슐레이티드 캠핑슈즈은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 신발, 캠핑슈즈, 그린란드 인슐레이티드 캠핑슈즈, 라플란드, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-097', '핀란드 숏 레인부츠', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '폴라' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '레인부츠' AND P.CATEGORY_NAME = '아웃도어 신발' LIMIT 1),
        '폴라베어상점', '폴라', '노르웨이', 77700, 134000, 117900,
        23, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '아웃도어 신발 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '핀란드 숏 레인부츠은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '아웃도어 신발, 레인부츠, 핀란드 숏 레인부츠, 폴라, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-098', '캐나다 올인원 응급키트', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '그리즐리' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '응급키트' AND P.CATEGORY_NAME = '안전/서바이벌' LIMIT 1),
        '폴라베어상점', '그리즐리', '캐나다', 83500, 144000, NULL,
        24, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '안전/서바이벌 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '캐나다 올인원 응급키트은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '안전/서바이벌, 응급키트, 캐나다 올인원 응급키트, 그리즐리, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-099', '스웨덴 12기능 멀티툴', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '코디악' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '나이프/멀티툴' AND P.CATEGORY_NAME = '안전/서바이벌' LIMIT 1),
        '폴라베어상점', '코디악', '미국', 89300, 154000, NULL,
        25, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '안전/서바이벌 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '스웨덴 12기능 멀티툴은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '안전/서바이벌, 나이프/멀티툴, 스웨덴 12기능 멀티툴, 코디악, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed'),
    ('PB-OUT-100', '아이슬란드 베이스플레이트 나침반', NULL,
        (SELECT B.BRAND_NO FROM brand_info B WHERE B.BRAND_NAME_KO = '브라운베어' LIMIT 1),
        (SELECT C.CATEGORY_NO FROM category_info C INNER JOIN category_info P ON P.CATEGORY_NO = C.PARENT_CATEGORY_NO WHERE C.CATEGORY_NAME = '나침반/GPS' AND P.CATEGORY_NAME = '안전/서바이벌' LIMIT 1),
        '폴라베어상점', '브라운베어', '핀란드', 95100, 164000, NULL,
        26, 5, 'TAXABLE', 'FREE', 0,
        'ON_SALE', 'Y', 'Y', NOW(), NULL,
        '안전/서바이벌 활동을 위해 고른 폴라베어상점 큐레이션 상품입니다.', '아이슬란드 베이스플레이트 나침반은(는) 캠핑과 야외 활동에서 편안하게 사용할 수 있도록 구성한 상품입니다. 실제 상품 사진과 상세 사양은 입고 후 업데이트됩니다.', '안전/서바이벌, 나침반/GPS, 아이슬란드 베이스플레이트 나침반, 브라운베어, 아웃도어', NOW(), 'catalog_seed', NOW(), 'catalog_seed')
ON DUPLICATE KEY UPDATE
    GOODS_NAME_KO = VALUES(GOODS_NAME_KO),
    BRAND_NO = VALUES(BRAND_NO),
    CATEGORY_NO = VALUES(CATEGORY_NO),
    SELLER_NAME = VALUES(SELLER_NAME),
    MANUFACTURER = VALUES(MANUFACTURER),
    ORIGIN_COUNTRY = VALUES(ORIGIN_COUNTRY),
    COST_PRICE = VALUES(COST_PRICE),
    SALE_PRICE = VALUES(SALE_PRICE),
    DISCOUNT_PRICE = VALUES(DISCOUNT_PRICE),
    STOCK_QUANTITY = VALUES(STOCK_QUANTITY),
    GOODS_STATUS = 'ON_SALE',
    USE_YN = 'Y',
    DISPLAY_YN = 'Y',
    SHORT_DESCRIPTION = VALUES(SHORT_DESCRIPTION),
    DETAIL_DESCRIPTION = VALUES(DETAIL_DESCRIPTION),
    SEARCH_KEYWORDS = VALUES(SEARCH_KEYWORDS),
    UPDT_DATE = NOW(),
    UPDT_ID = 'catalog_seed';

UPDATE goods_image I
INNER JOIN goods_info G ON G.GOODS_NO = I.GOODS_NO
SET I.IMAGE_URL = '/goods-images/polar-bear-default.jpg',
    I.ORIGINAL_FILE_NAME = 'polar-bear-default.jpg'
WHERE G.GOODS_CODE LIKE 'PB-OUT-%'
  AND I.REG_ID = 'catalog_seed';

INSERT INTO goods_image (GOODS_NO, IMAGE_URL, ORIGINAL_FILE_NAME, REPRESENTATIVE_YN, SORT_ORDER, REG_DATE, REG_ID)
SELECT G.GOODS_NO, '/goods-images/polar-bear-default.jpg', 'polar-bear-default.jpg', 'Y', 0, NOW(), 'catalog_seed'
FROM goods_info G
WHERE G.GOODS_CODE LIKE 'PB-OUT-%'
  AND NOT EXISTS (
      SELECT 1 FROM goods_image I WHERE I.GOODS_NO = G.GOODS_NO
  );

COMMIT;
