CREATE TABLE message_schema.users (
    id BIGINT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE message_schema.conversations (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL REFERENCES message_schema.users(id) ON DELETE CASCADE,
    user2_id BIGINT NOT NULL REFERENCES message_schema.users(id) ON DELETE CASCADE,
    user1_last_read_message_id BIGINT,
    user2_last_read_message_id BIGINT,
    last_message_id BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user1_id, user2_id),
    CONSTRAINT check_user_order CHECK (user1_id < user2_id)
);

CREATE TABLE message_schema.messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES message_schema.conversations(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES message_schema.users(id) ON DELETE CASCADE,
    message_type VARCHAR(20) NOT NULL DEFAULT 'text',
    content TEXT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE message_schema.reactions (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL REFERENCES message_schema.messages(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES message_schema.users(id) ON DELETE CASCADE,
    reaction_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (message_id, user_id, reaction_type)
);