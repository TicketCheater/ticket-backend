-- Test 계정
INSERT INTO member (id, name, password, email, nickname, role, created_at, updated_at, deleted_at) values
    (1, 'master', '$2a$10$5cHyp1KkG9tr.lPJm4qesO0onrOFE1JlqDoI3dnGjuLmc4gpyEEHy', 'master@naver.com', 'master', 'ADMIN', now(), null, null)
;
