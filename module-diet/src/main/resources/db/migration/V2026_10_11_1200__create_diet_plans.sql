CREATE TABLE diet_plans
(
    id          UUID         NOT NULL,
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(200) NOT NULL,
    kcal        INTEGER      NOT NULL,
    state       VARCHAR(20)  NOT NULL,

    created_on  TIMESTAMPTZ  NOT NULL,
    created_by  VARCHAR(255) NOT NULL,
    modified_on TIMESTAMPTZ,
    modified_by VARCHAR(255),

    CONSTRAINT pk_diet_plans PRIMARY KEY (id),
    CONSTRAINT ck_diet_plans_kcal_positive CHECK (kcal > 0),
    CONSTRAINT ck_diet_plans_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT ck_diet_plans_state CHECK (state IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_diet_plans_tenant_id ON diet_plans (tenant_id);
