CREATE TYPE auth_schema.role_type AS ENUM (
  'STUDENT','EMPLOYER','MODERATOR','SUPPORT_ADMIN','BUSINESS_ADMIN','SYSTEM_ADMIN'
);

CREATE TYPE auth_schema.account_status AS ENUM ('ACTIVE','INACTIVE','BANNED');

CREATE TABLE IF NOT EXISTS auth_schema.user_accounts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email CITEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role auth_schema.role_type NOT NULL,
  status auth_schema.account_status NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
