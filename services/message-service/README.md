DEMO dự án:
tạo schema thủ công:
CREATE SCHEMA message_schema;

SQL, cả 4 dòng để cấp quyền sửa db (sửa tên user của db "duke" nếu cần nhé)
-- 1. Cấp quyền cho user 'duke' được phép "nhìn thấy" và "sử dụng" schema 'message_schema'
GRANT USAGE ON SCHEMA message_schema TO duke;
-- 2. Cấp quyền cho user 'duke' được phép TẠO BẢNG bên trong schema đó
GRANT CREATE ON SCHEMA message_schema TO duke;
-- 3. Cấp tất cả các quyền (SELECT, INSERT, UPDATE, DELETE) trên TẤT CẢ các bảng BÊN TRONG schema 'message_schema' cho user 'duke'
GRANT ALL ON ALL TABLES IN SCHEMA message_schema TO duke;
-- 4. Đặt quyền mặc định: Bất kỳ bảng nào được TẠO MỚI trong tương lai cũng sẽ tự động cấp quyền cho 'duke'
ALTER DEFAULT PRIVILEGES IN SCHEMA message_schema GRANT ALL ON TABLES TO duke;



cổng mà service sử dụng: 8084 (8083 vẫn trống)

các bước demo nhắn tin: 
tắt/comment/xóa dependency spring security trong message-service/pom.xml
sửa dòng này trong application.properties: "spring.jpa.hibernate.ddl-auto=update" thành "spring.jpa.hibernate.ddl-auto=create"
chạy MessageServiceApplication.java 1 lần để hibernate tạo message_schema, sau đó stop
chạy query tạo data để demo:

INSERT INTO message_schema.users (id, full_name, avatar_url, is_active)
VALUES (1, 'Duke', 'https://example.com/avatar/dukedeptrai.png', true)
ON CONFLICT (id) DO NOTHING;
INSERT INTO message_schema.users (id, full_name, avatar_url, is_active)
VALUES (2, 'Hari', 'https://example.com/haideptrai.png', true)
ON CONFLICT (id) DO NOTHING;
INSERT INTO message_schema.conversations (id, user1_id, user2_id)
VALUES (1, 1, 2)
ON CONFLICT (id) DO NOTHING;

api demo: POST http://localhost:8084/api/v1/messages

raw json mẫu:
{
"conversationId": 1,
"senderId": 1,
"content": "Chào Hari, đây là tin nhắn test từ Duke!",
"messageType": "TEXT"
}





hướng mở rộng db tổng:
project sử dụng 1 db duy nhất với mỗi schema riêng cho từng service
trong tương lại có thể tách db ra thành các db con tương ứng với các schema => hệ thống ổn định hơn, các schema độc lập hơn

về service này:
if (conversation.getUser1().getId() > conversation.getUser2().getId()) {
// Hoán đổi để luôn đảm bảo user1_id < user2_id
User temp = conversation.getUser1();
conversation.setUser1(conversation.getUser2());
conversation.setUser2(temp);
}
conversationRepository.save(conversation); //đảm bảo u1id<u2id