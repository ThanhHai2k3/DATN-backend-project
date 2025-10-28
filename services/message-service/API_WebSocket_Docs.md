có vẻ như file bị lỗi format JSON
<!-- API và luồng hoạt động của 1 tin nhắn: --> 

<!-- 1. Lấy danh sách các đoạn chat: -->
GET /api/v1/conversations/user/{userId}    //UserId kiểu UUID
[ //List<ConversationResponse> nhé
{
"id": 1,
"receiver": { "id": "7cb253b8-ce2c-41c4-b4a2-e6191ac2f4b9", "fullName": "Nguyễn Văn B", "avatarUrl": "..." },
"lastMessage": { /* ... MessageResponse ... */ },
"unreadCount": 2
},
{
"id": 1,
"receiver": { "id": "7cb253b8-ce2c-41c4-b4a2-e6191ac2f4b9", "fullName": "Lê Thị C", "avatarUrl": "..." },
"lastMessage": null,
"unreadCount": 0
}
]
<!-- 2. Lấy lịch sử tin nhắn (chưa phân trang) -->
GET /api/v1/messages/conversation/{conversationId}
[
{
"id": 25,
"conversationId": 1,
"sender": {
"id": "569e6f40-cd87-432a-bfd0-c257b00b0ddf",  //UUID
"fullName": "Duke",
"avatarUrl": "https://example.com/avatar/duke.png"
},
"content": "https://duke-bucket-datn.s3.ap-southeast-2.amazonaws.com/2e919f41-f485-4126-a1d6-711b027a8165.png",
"messageType": "IMAGE",
"sentAt": "2025-10-27T20:19:34.517200Z",
"reactions": []
},

    {...},

    {
        "id": 1,
        "conversationId": 1,
        "sender": {
            "id": "569e6f40-cd87-432a-bfd0-c257b00b0ddf",
            "fullName": "Duke",
            "avatarUrl": "https://example.com/avatar/duke.png"
        },
        "content": "17312",
        "messageType": "TEXT",
        "sentAt": "2025-10-23T13:27:09.170055Z",
        "reactions": []
    }
]

<!-- 3. Kết nối và lắng nghe các event từ Websocket -->
3.1 Kết nối đến server: const stompClient = new StompJs.Client({ brokerURL: 'ws://localhost:8084/ws' });
stompClient.subscribe(`/topic/conversation/{conversationId}`, (message) => {
// Xử lý tất cả các sự kiện real-time tại đây
const event = JSON.parse(message.body);
handleWebSocketEvent(event);
});

<!-- 4a. Gọi API để gửi -->
POST /api/v1/messages
{
"conversationId": 1,
"senderId": "569e6f40-cd87-432a-bfd0-c257b00b0ddf",
"content": "hellooo mấy mom",
"messageType": "TEXT" // Hoặc IMAGE, FILE, RECALLED, (hiện tại chưa có STICKER)
}

<!-- 4b. Nhận tin nhắn -->
/topic/conversation/{conversationId}

payload (MessageResponse):
{
"type": "NEW_MESSAGE",
"payload": {
"id": 721,
"conversationId": 1, // kiểu Long
"sender": { "id": "569e6f40-cd87-432a-bfd0-c257b00b0ddf", "fullName": "Duke", ... },
"content": "Mình nhận được rồi nhé!",
"messageType": "TEXT",
"sentAt": "2025-10-28T11:05:00Z",
"reactions": []
}
}


<!-- Gửi event thêm hoặc thu hồi reaction -->
POST /api/v1/reactions
{ "messageId": 721, "userId": "569e6f40-cd87-432a-bfd0-c257b00b0ddf", "reactionType": "LIKE" } <!--có 4 loại reaction là: LIKE, LOVE, HAHA, SAD-->

DELETE /api/v1/reactions/{reactionId}?userId=569e6f40-cd87-432a-bfd0-c257b00b0ddf

<!-- Nhận event thêm hoặc thu hồi reaction từ người khác -->
{
"type": "ADD_REACTION",
"payload": {
"id": 150,
"userId": "569e6f40-cd87-432a-bfd0-c257b00b0ddf",
"reactionType": "LIKE",
"messageId": 721
}
}

{
"type": "REMOVE_REACTION",
"payload": {
"messageId": 721,
"reactionId": 150
}
}

<!-- Gửi/Nhận event thu hồi tin nhắn -->

<!-- Gửi ảnh -->
POST /api/v1/messages/image
form-data: file, conversationId, senderId

<!-- Kết nối socket -->
postman: ws://localhost:8084/ws + bỏ method .withSockJS() ở file WebSocketConfig.java (test đang lỗi)
trình duyệt: file resources/static/STOMP_Client.html (test cách này đã ok, có sử dụng .withSockJS())
resources/static/Index.html (test cách này đã ok, có sử dụng .withSockJS())