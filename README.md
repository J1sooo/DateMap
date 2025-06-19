# DateMap

## 🗺️ 프로젝트 소개

DateMap은 AI 기반 가상 소개팅과 데이트 장소 추천을 통해 사용자들의 연애 경험을 돕는 서비스입니다. AI 캐릭터와의 소개팅, 사용자간 일대일 매칭, 데이트 코스 추천, 랭킹 시스템 등 다양한 기능을 통해 연애 콘텐츠를 손쉽고 재미있게 체험할 수 있습니다.

https://www.est-datemap.com/main

---
## 🎥 시연 영상

![2025-06-16-14-25-19 (1)](https://github.com/user-attachments/assets/ed82df7c-f60d-465a-8bdc-129a1e29bf55)

[YouTube](https://www.youtube.com/watch?v=00T_Zzqtlj8)

---

## 📌 주요 기능

### 🔹 네비게이션 바

* **로고**: 클릭 시 메인 페이지로 이동
* **회원가입 / 로그인**: 비회원은 로그인 혹은 회원가입 가능
* **마이페이지**: 로그인 유저 전용, 닉네임 클릭 시 이동
* **소개팅 설정 / 데이트 코스 추천 / 랭킹 / 알림**: 핵심 기능들 접근 경로 제공

### 🔹 메인 페이지

* **가상 소개팅**: AI 캐릭터와 대화하며 소개팅 시뮬레이션 체험
* **데이트 장소 추천**: 선호 지역 정보 기반으로 맞춤 데이트 코스 제안

### 🔹 회원가입 & 로그인

* **입력 필드 유효성 검사** 포함 (아이디, 비밀번호, 이메일 등)
* **자동 로그인, 아이디 저장, 비밀번호 재설정 기능** 제공

### 🔹 데이트 장소 추천

* 지역/예산/취미/교통수단 등 입력 후 최적의 장소 추천
* 추천 결과를 코스 컬렉션으로 저장 및 공유 가능

### 🔹 소개팅 상대 설정

* 연령, 성격, 취미 등을 설정해 맞춤형 AI 소개팅 캐릭터 구성
* 설정 완료 후 대화 시작

### 🔹 소개팅 대화 페이지

* 메시지 주고받기, 대화 종료, 대화 평가 기능 제공
* 소개팅 팁 제공

### 🔹 랭킹 페이지

* 최고점수 & 소개팅 횟수 기준으로 랭킹 표시
* 일대일 채팅 기능으로 실 사용자와 대화 가능

### 🔹 일대일 매칭

* 실 사용자와의 프로필 확인 후 채팅 가능
* 대화 종료 기능 포함

### 🔹 종합 분석 페이지

* 점수 평균, 소개팅 횟수, AI 종합 평가 결과 표시

### 🔹 마이페이지

* 회원정보 수정, 회원탈퇴, 코스 컬렉션 관리
* 내가 설정한 AI 캐릭터 및 피드백 확인
* 일대일 매칭 기능 연결

---

## ⚙️ 기술 스택

* **Frontend**: html, css, Thymeleaf, Bootstrap, WebSocket (SockJS, STOMP.js)
* **Backend**: Spring Boot, Spring Security, JPA, WebSocket (Spring WebSocket, STOMP)
* **DB**: AWS RDS (MySQL)
* **etc**: Postman (API 테스트)
  Git, GitHub (버전 관리)
  IntelliJ IDEA (IDE)
  AWS EC2, S3 (배포 및 저장소)

---
<details>
<summary> 📑 기능 명세서 </summary>

네비바

| No. | 구분 | 기능 | 설명 |
| --- | --- | --- | --- |
| 1 | 헤더 | 로고 | 클릭 시 메인 페이지로 이동 |
| 2 |  | 회원가입, 로그인 | 회원 가입 또는 로그인 페이지로 이동 |
| 3 |  | 마이페이지 | 로그인 후 닉네임 클릭시 이동 |
| 4 |  | 소개팅 설정 |  |
| 5 |  | 데이트 코스 추천 | 사용자의 선호 지역에 맞게 추천 |
| 6 |  | 랭킹 |  |
| 7 |  | 알림 | 일대일 채팅방 알림 |

메인페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 8 | 가상 소개팅 | AI와 채팅하는 페이지 |
| 9 |  | 가상 소개팅 설정 |
| 10 | 데이트 장소 추천 | 데이트 장소 페이지 |
| 11 |  | 데이트 장소 설정 |

회원 가입

| No. | 항목 | 설명 | 유효성 |
| --- | --- | --- | --- |
| 12 | 아이디 | 로그인에 사용할 ID 입력 |  필수, 특수 문자/ 공백 사용 불가, 영문 + 숫자 최대 12자, 변경 및 중복 불가 |
| 13 | 닉네임 | 닉네임 입력 | 한글/영문/숫자 최대 8자 |
| 14 | 이메일 | 이메일 주소 입력 | 필수 |
| 15 | 비밀번호 | 비밀번호 입력 | 필수, 영문 + 숫자 포함 최대 20자 |
| 16 | 비밀번호 확인 | 비밀번호 재입력 | 비밀번호 일치 확인 |
| 17 | 성별 | 성별 선택 |  |
| 18 | 생년월일 | 연도, 월, 일 입력 |  |
| 19 | 선호 지역 | 사용자의 선호지역 중심으로 메인페이지 코스 컬렉션 추천 (최대 2개까지) |  |
| 20 | 약관 동의 | 이용약관, 개인정보처리방침 |  |
| 21 | 회원 가입 | 유효성 통과 시 회원 가입 disabled 해제 |  |
| 22 | 로그인 링크 | 회원가입 후 로그인 페이지로 이동 |  |

로그인 페이지

| No. | 항목 | 설명 | 유효성 |
| --- | --- | --- | --- |
| 23 | 아이디(이메일) 입력 | 아이디(이메일) 입력 필드 | 필수, 공백 불가 |
| 24 | 비밀번호 입력 | 비밀번호 입력 필드 | 필수 |
| 25 | 아이디 저장 | 로그인 한 아이디 저장 | 선택(체크 박스) |
| 26 | 아이디 찾기 | 닉네임과 이메일 입력을 통해 찾기 |  |
| 27 | 비밀번호 재설정 | 아이디와 이메일 입력을 통해 재설정 |  |
| 28 | 로그인 버튼 | 로그인 요청 | 아이디/비번 모두 입력 시 버튼 활성화 |
| 29 | 회원 가입 버튼 | 회원 가입 페이지로 이동 |  |

데이트 장소 추천 설정 페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 30 | 지역 | 선호 지역 / 지역 정보만으로도 코스짜기 가능 |
| 31 | 예산 | 데이트 비용 |
| 32 | 취미 | 커플의 취미 |
| 33 | 데이트 날짜 | 데이트 날짜 |
| 34 | 교통수단 | 어떤 교통수단을 이용하는지 (버스, 지하철 등등) |

데이트 장소 추천 페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 35 | 지역 | 데이트 지역 |
| 36 | 장소 | 데이트 장소 |
| 37 | 설명 | 장소에 대한 설명 |
| 38 | 저장하기 | 코스 콜렉션으로 만들기 / 제목과 사진을 기입 후 저장 |
| 39 | 공유 | url 공유하기 |
| 40 | 뒤로가기 | 데이트 장소 추천 설정 페이지로 돌아감 |

소개팅 상대방 설정 페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 41 | 대화 상대 설정 | 캐릭터 프리셋 및 커스텀 가능 |
| 42 | 나이 | 연령대별로 설정 / 20대부터 50대까지 설정 |
| 43 | 성격 | 성격 설정: 내향적, 외향적 |
| 44 | 취미 | 취미 선택 : 운동, 반려동물, 게임, 드라이브, 술 , 독서 등 |
| 45 | 사진 | 상대방 이미지 설정 |
| 46 | 대화 시작 | 설정한 상대방과 대화 시작 / 나이, 성격,취미를 기반으로 대화 |

소개팅(대화) 페이지

| No. | 항목 | 설명 | 세부 정보 |
| --- | --- | --- | --- |
| 47 | 채팅 | 상대방과 대화 | 상대방이 보낸 메시지와 사용자가 보낸 메시지를 확인 |
| 48 | 메시지 전송 | 상대방에게 메시지 전송 | 상대방에게 메시지 전송 |
| 49 | 대화 종료 | 대화를 종료 | 현재 상대방과의 대화를 종료, 이어서 대화 불가 |
| 50 | 평가 하기 | 전체적인 대화 내용 평가 | 사용자와 AI의 대화를 전체적으로 평가하여 대화를 잘 했는지 평가 |
| 51 | 팁 | 소개팅 팁 제공 |  |

랭킹 페이지

| No. | 항목 | 설명 | 세부 정보 |
| --- | --- | --- | --- |
| 52 | 랭킹 | 10등까지 등수 표시 | 최고 점수 , 소개팅 횟수 기준 |
| 53 | 랭킹 분류 | 최고 점수, 소개팅 횟수로 랭킹 기준 설정 | 점수가 높은 순으로 정렬하거나 , 소개팅기능을 사용한 횟수로 정렬 |
| 54 | 일대일 채팅 | 랭킹에 등록 되어있는 사용자와 일대일 채팅 가능 |  |

일대일 매칭 페이지 

| No. | 항목 | 설명 |
| --- | --- | --- |
| 55 | 프로필  | 상대방과 내 프로필 표시 |
| 56 | 채팅 | 일대일 채팅 |
| 57 | 대화 종료 | 대화 종료 |

종합 분석 페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 58 | 점수 | 받은 점수들의 평균 |
| 59 | 소개팅 횟수 | 소개팅한 횟수 |
| 60 | 종합 평가 | ai의 종합 평가 |

마이페이지

| No. | 항목 | 설명 |
| --- | --- | --- |
| 61 | 회원 정보 수정 | 프로필 이미지, 아이디, 이메일 등등 수정 가능 |
| 62 | 회원 탈퇴 |  |
| 63 | 종합 분석 평가 | 종합 분석 평가 페이지로 이동 |
| 64 | 코스 컬렉션 | 나의 데이트 코스 추천 컬렉션(이미지 수정, 코스 삭제 가능) |
| 65 | 소개팅 상대 | 지금까지 한 ai 소개팅 상대 & 피드백 |
| 66 | 매칭 서비스 | 현재 가입되어 있는 이성의 사용자들과 일대일 채팅 가능 |

</details>

<details>
<summary> 📖 API 명세서 </summary>

회원 CRUD(로그인/회원가입/마이페이지)

User

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 로그인 페이지 진입 | GET | /login | 쿠키에 저장된 ID 확인 후 로그인 페이지 렌더링 |
| 로그인 요청 처리 | POST | /login | 로그인 및 세션 저장 + rememberMe 처리 |
| 로그아웃 | GET | /logout | 세션 무효화 후 메인 페이지로 이동 |
| 회원가입 페이지 진입 | GET | /join | 회원가입 입력 폼 페이지 렌더링 |
| 회원가입 요청 처리 | POST | /join | 회원가입 입력 검증 및 가입 처리 |
| 아이디 중복 확인 | GET | /api/user/checkUserId | userId 중복 여부 확인 |
| 닉네임 중복 확인 | GET | /api/user/checkNickName | nickName 중복 여부 확인 |
| 이메일 중복 확인 | GET | /api/user/checkEmail | email 중복 여부 확인 |
| 회원가입 API 처리 | POST | /api/user | 회원가입 요청 처리 (API 방식) |
| 아이디 찾기 | POST | /api/user/findId | 이름과 이메일로 아이디 찾기 |
| 비밀번호 재설정 본인확인 | POST | /api/user/verifyPassword | 아이디+이메일로 비밀번호 재설정 본인 확인 |
| 비밀번호 재설정 | POST | /api/user/resetPassword | usn으로 비밀번호 변경 |
| 내 프로필 조회 | GET | /api/user/profile | 로그인된 유저의 정보 반환 (API) |
| 내 프로필 수정 | PATCH | /api/user/profile | 유저 정보 및 이미지 수정 (multipart/form-data) |
| 회원 탈퇴 | DELETE | /api/user/{usn} | 로그인 유저 본인의 계정 탈퇴 처리 |
| 프로필 수정 페이지 진입 | GET | /profile/edit | 현재 유저의 정보를 폼에 미리 담아 렌더링 |

blind_date 소개팅 상대 CRUD

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 파트너 조회 | GET | /api/partner/{id} | ID로 특정 파트너 정보 조회 |
| 파트너 삭제 | DELETE | /api/partner/{id} | ID로 특정 파트너 삭제 (채팅방과 연결된 경우 주의 필요) |
| 파트너 조건 설정 페이지  | GET | /partner-setting | 로그인한 사용자가 데이트 조건 설정 페이지로 이동 |

date_recommend

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 추천 코스 생성 | POST | /api/recommendations | 새로운 추천 코스 등록, 이미지 업로드 포함 |
| 추천 코스 전체 조회 | GET | /api/recommendations | 저장된 모든 추천 코스 목록 조회 |
| 추천 코스 단건 조회 | GET | /api/recommendations/{id} | ID로 특정 추천 코스 조회 |
| 추천 코스 전체 삭제 | DELETE | /api/recommendations | 모든 추천 코스를 삭제 |
| 추천 코스 단건 삭제 | DELETE | /api/recommendations/{id} | ID로 특정 추천 코스를 삭제 |
| 추천 코스 이미지 수정 | PATCH | /api/recommendations/{id} | ID로 특정 추천 코스의 이미지 파일 수정 |
| 추천 코스 상세 페이지 보기 | GET | /recommendations/{id} | 추천 코스 ID에 해당하는 상세 정보 페이지 렌더링 |

ranking

| 🏷NAME | ⚙METHOD | 📎URL               | 📖DESCRIPTION |
| --- | --- |---------------------| --- |
| 전체 랭킹 조회 | GET | /api/rankings       | 기본 랭킹 정보 조회 |
| 점수 기반 랭킹 조회 | GET | /api/rankings/score | 점수 기준 랭킹 정보 조회 |
| 참여 횟수 랭킹 조회 | GET | /api/rankings/count | 소개팅 참여 횟수 기준 랭킹 조회 |
| 랭킹 페이지 진입 | GET | /rankings           | 점수 기준 랭킹 리스트를 HTML 페이지로 렌더링 |

analyze

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 종합분석 | GET | /analyze | 모든 피드백을 기준으로 종합평가 |

blind_date_chat

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 메시지 전송 | POST | /api/chat/{chatroomId} | 사용자의 메시지를 Gemini에게 보내고 저장 |
| 채팅방 삭제 | DELETE | /api/chat/{chatroomId} | 채팅방 및 메시지 삭제 |
| 피드백 생성 요청 | POST | /api/chat/{chatroomId}/feedback | Gemini에게 피드백 생성 요청 후 저장 및 리다이렉트 |
| 채팅방 보기 | GET | /chat/{chatroomId} | 채팅 메시지와 상대방 정보 출력 |
| 피드백 보기 | GET | /chat/{chatroomId}/feedback | 해당 채팅방의 Gemini 피드백 결과 출력 |

Chatroom

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 채팅방 생성 및 시작 | POST | /api/chatroom | Partner 저장 후 채팅방 생성 + 첫 메시지 전송 |
| 모든 채팅방 목록 조회 | GET | /api/chatroom | 전체 채팅방 목록을 조회 |
| 특정 채팅방 상세 조회 | GET | /api/chatroom/{id} | ID로 특정 채팅방 정보 조회 |
| 특정 채팅방 삭제 | DELETE | /api/chatroom/{id} | ID로 채팅방 삭제 (Partner 삭제 예정 포함) |

matching_chat

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 일대일 채팅방 생성 또는 조회 | POST | /matchchat/createOrGetRoom | 상대 닉네임 기반으로 채팅방 생성 또는 기존 방 반환 |
| 일대일 채팅방 입장 | GET | /matchchat/room/{chatroomId} | 채팅방 입장 및 상대방 프로필, 메시지 출력 |
| 일대일 채팅 종료 처리 | POST | /matchchat/{chatroomId}/end | 채팅 종료 후 채팅방 제거 및 메인으로 리다이렉트 |
| 매칭 가능한 사용자 목록 조회 | GET | /api/matchableUsers | 가입되어있는 이성의 사용자 목록 조회  |

Notification

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 읽지 않은 알림 개수 조회 | GET | /api/notifications/unread-count | 로그인된 사용자의 읽지 않은 알림 개수 반환 |
| 전체 알림 목록 조회 | GET | /api/notifications | 로그인된 사용자의 전체 알림 목록 반환 |
| 전체 알림 읽음 처리 | POST | /api/notifications/mark-all-read | 사용자의 모든 알림을 읽음 상태로 변경 |

Alan_ai

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| Alan 응답 요청 | GET | /api/alan | Alan AI로부터 응답 받기 |

Main

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 메인 페이지 진입 | GET | /main | 최신 파트너 및 추천 정보 4개씩 로드 후 메인 페이지 렌더링 |

MyPage

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| 마이페이지 보기 | GET | /mypage | 로그인한 유저의 프로필, 평가, 추천 코스 등을 마이페이지에 표시 |
---
</details>
<details> 
<summary>📊 ERD </summary>

![image](https://github.com/user-attachments/assets/a5393814-80eb-4321-93ee-8a4a7ed5340e)

## 테이블 리스트

## 📁 user (회원)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| usn | BIGINT | PK, AUTO_INCREMENT, UNIQUE | 유저 고유 번호 |
| user_id | VARCHAR(50) | NOT NULL, UNIQUE | 아이디 |
| nickName | VARCHAR(80) | NOT NULL, UNIQUE | 닉네임 |
| password | VARCHAR(100) | NOT NULL | 비밀번호 |
| email | VARCHAR(255) | NOT NULL, UNIQUE | 이메일 주소 |
| gender | VARCHAR(2) | NOT NULL | 성별 |
| date_of_birth | DATE | NOT NULL | 생년월일 |
| prefer_area | VARCHAR(100) | NOT NULL | 선호 지역 |
| prefer_area_detail | VARCHAR(100) | NOT NULL | 상세 지역 |
| profile_img | VARCHAR(255) | NOT NULL | 프로필 이미지 |
| join_date | TIMESTAMP | NOT NULL | 가입일시 |

---

## 💬 chatroom (채팅방)

| 제목 | 1열 | 2열 | 3열 |
| --- | --- | --- | --- |
| 컬럼명 | 타입 | 제약조건 | 설명 |
| id | BIGINT | PK, AUTO_INCREMENT | 채팅방 ID |
| usn | BIGINT | FK → user(usn) | 내 유저번호 |
| partner_id | BIGINT | FK → user(usn) | 상대 유저번호 |

---

## 💬 chat_message (채팅 메시지)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 메시지 ID |
| room_id | BIGINT | FK → chatroom(id) | 채팅방 ID |
| role | ENUM | NOT NULL | 발신자 역할 (USER/AI 등) |
| message | TEXT | NOT NULL | 메시지 내용 |
| created_at | TIMESTAMP | NOT NULL | 발신 시각 |

---

## 🧠 blind_data_character (소개팅 캐릭터)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| char_id | BIGINT | PK, AUTO_INCREMENT | 캐릭터 ID |
| gender | VARCHAR(2) | NOT NULL | 성별 |
| age_group | VARCHAR(10) | NOT NULL | 연령대 |
| personal_type | VARCHAR(10) | NOT NULL | 성격 유형 |
| hobby | VARCHAR(50) | NOT NULL | 취미 |
| image_url | VARCHAR(255) | NOT NULL | 캐릭터 이미지 URL |
| created_at | TIMESTAMP | NOT NULL | 생성일시 |

---

## 📝 blind_date_feedback (소개팅 평가)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 평가 ID |
| char_id | BIGINT | FK → blind_date_character | 캐릭터 ID |
| usn | BIGINT | FK → user(usn) | 유저 번호 |
| created_at | TIMESTAMP |  | 작성 시각 |
| summary | TEXT | NOT NULL | 대화 요약 |
| feedback | TEXT | NOT NULL | 피드백 내용 |
| score | INT | NOT NULL | 점수 (평가) |

---

## 🧭 date_course (데이트 코스)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| course_id | BIGINT | PK, AUTO_INCREMENT | 코스 ID |
| usn | BIGINT | FK → user(usn) | 작성자 |
| image_url | VARCHAR(255) | NOT NULL | 대표 이미지 URL |
| title | VARCHAR(50) | NOT NULL | 코스 제목 |
| content1 ~ 4 | LONGTEXT | NOT NULL | 코스 상세 설명 |
| saved_at | TIMESTAMP | NOT NULL | 저장일시 |
| area | VARCHAR(20) | NOT NULL | 지역 정보 |

---

## 🏆 ranking (랭킹)

| 컬럼명 | 타입 | 제약조건 | 설명 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AUTO_INCREMENT | 랭킹 ID |
| usn | BIGINT | FK → user(usn) | 유저 번호 |
| nickname | VARCHAR(50) | NOT NULL | 유저 닉네임 |
| gender | VARCHAR(10) | NOT NULL | 성별 |
| profile_img | VARCHAR(255) | NOT NULL | 프로필 이미지 |
| score | INT UNSIGNED | NOT NULL | 점수 |
| achieved_time | TIMESTAMP | NOT NULL | 달성 시각 |

</details>

## 📁 프로젝트 구조

```
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com.est.back
 ┃ ┣ 📂resources
 ┃ ┃ ┣ 📜application.properties
 ┃ ┃ ┗ 📜templates
 ┗ 📂test
```

---

## 🙋‍♀️ 팀원 소개

| 이름  | 역할                   |
| --- | -------------------- |
| [김지수](https://github.com/J1sooo) | 앨런 AI 담당, CI/CD 구축, AWS 관리 |
| [문종일](https://github.com/tetsuya0083) | 전체적인 CRUD, 테스트 코드, 랭킹 시스템   |
| [변규리](https://github.com/gyuri0124) | 회원 CRUD, 1대1 실시간 채팅 시스템   |
| [이주학](https://github.com/wngkr38) | 제미나이 AI 담당, 가상 소개팅 시스템  |

---
