-- 1️ Enable UUID generator extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 2️ Create schema
CREATE SCHEMA IF NOT EXISTS profile_schema;

-- ============================================================
-- 3️ COMPANIES
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.companies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    industry        VARCHAR(150),
    description     TEXT,
    logo_url        VARCHAR(500),
    website_url     VARCHAR(300),
    address         VARCHAR(255),
    company_size    VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 4️ EMPLOYERS
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.employers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    company_id      UUID NOT NULL,
    name            VARCHAR(150) NOT NULL,
    position        VARCHAR(100),
    is_admin        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_employer_user
        FOREIGN KEY (user_id)
        REFERENCES auth_schema.user_accounts(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_employer_company
        FOREIGN KEY (company_id)
        REFERENCES profile_schema.companies(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_employers_user_id ON profile_schema.employers(user_id);
CREATE INDEX IF NOT EXISTS idx_employers_company_id ON profile_schema.employers(company_id);

-- ============================================================
-- 5️ STUDENT_PROFILES
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.student_profiles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    avatar_url      VARCHAR(500),
    dob             DATE,
    gender          VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    address         VARCHAR(255),
    bio             TEXT,
    cv_url          VARCHAR(500),
    cv_text         TEXT,
    is_visible      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_student_user
        FOREIGN KEY (user_id)
        REFERENCES auth_schema.user_accounts(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_student_profiles_user_id ON profile_schema.student_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_student_profiles_visible ON profile_schema.student_profiles(is_visible);

-- ============================================================
-- 6️ EDUCATIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.educations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id      UUID NOT NULL,
    school_name     VARCHAR(200) NOT NULL,
    major           VARCHAR(150),
    degree          VARCHAR(30) CHECK (degree IN ('HIGH_SCHOOL', 'COLLEGE', 'BACHELOR', 'MASTER', 'PHD')),
    start_date      DATE,
    end_date        DATE,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_education_student
        FOREIGN KEY (student_id)
        REFERENCES profile_schema.student_profiles(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_educations_student_id ON profile_schema.educations(student_id);

-- ============================================================
-- 7️ EXPERIENCES
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.experiences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id      UUID NOT NULL,
    project_name    VARCHAR(200),
    company_name    VARCHAR(200),
    role            VARCHAR(150),
    description     TEXT,
    start_date      DATE,
    end_date        DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_experience_student
        FOREIGN KEY (student_id)
        REFERENCES profile_schema.student_profiles(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_experiences_student_id ON profile_schema.experiences(student_id);

-- ============================================================
-- 8️ SKILLS
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.skills (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) UNIQUE NOT NULL,
    category        VARCHAR(100)
);

-- ============================================================
-- 9️ STUDENT_SKILLS
-- ============================================================
CREATE TABLE IF NOT EXISTS profile_schema.student_skills (
    student_id      UUID NOT NULL,
    skill_id        UUID NOT NULL,
    level           INTEGER CHECK (level BETWEEN 1 AND 5),
    PRIMARY KEY (student_id, skill_id),

    CONSTRAINT fk_student_skill_student
        FOREIGN KEY (student_id)
        REFERENCES profile_schema.student_profiles(id)
        ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_student_skill_skill
        FOREIGN KEY (skill_id)
        REFERENCES profile_schema.skills(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_student_skills_student_id ON profile_schema.student_skills(student_id);
CREATE INDEX IF NOT EXISTS idx_student_skills_skill_id ON profile_schema.student_skills(skill_id);

-- ============================================================
-- 10 Trigger cập nhật updated_at tự động
-- ============================================================
CREATE OR REPLACE FUNCTION profile_schema.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    tbl TEXT;
BEGIN
    FOR tbl IN
        SELECT table_name
        FROM information_schema.columns
        WHERE table_schema = 'profile_schema' AND column_name = 'updated_at'
    LOOP
        EXECUTE format('
            DROP TRIGGER IF EXISTS set_updated_at_%1$I ON profile_schema.%1$I;
            CREATE TRIGGER set_updated_at_%1$I
            BEFORE UPDATE ON profile_schema.%1$I
            FOR EACH ROW
            EXECUTE FUNCTION profile_schema.set_updated_at();
        ', tbl);
    END LOOP;
END $$;
