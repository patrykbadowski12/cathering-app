CREATE TABLE diet_plans(
    id UUID PRIMARY KEY NOT NULL ,
    tenant_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL ,
    kcal INT NOT NULL CHECK (kcal > 0),
    state VARCHAR(20) NOT NULL CHECK (state IN ('ACTIVE','INACTIVE')),
    created_on TIMESTAMP    NOT NULL DEFAULT now(),
    created_by VARCHAR,
    modified_on TIMESTAMP,
    modified_by VARCHAR
);

CREATE INDEX idx_diet_plans_tenant_id on diet_plans(tenant_id);
