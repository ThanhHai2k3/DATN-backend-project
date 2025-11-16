CREATE TABLE IF NOT EXISTS skill_schema.skills (
    id UUID PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    category_id UUID NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_skill_category
        FOREIGN KEY (category_id)
        REFERENCES skill_schema.skill_categories(id)
        ON DELETE RESTRICT
);
