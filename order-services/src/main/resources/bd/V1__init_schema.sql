CREATE SCHEMA IF NOT EXISTS "orden";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

DROP TYPE IF EXISTS order_status;
CREATE TYPE order_status AS ENUM ('PENDING', 'PAID', 'APPROVED', 'CANCELLED', 'CANCELLING');

DROP TABLE IF EXISTS "orden".orden CASCADE;

CREATE TABLE "orden".orden
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    restaurant_id uuid NOT NULL,
    tracking_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    order_status order_status NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    CONSTRAINT orden_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "orden".order_items CASCADE;

CREATE TABLE "orden".order_items
(
    id bigint NOT NULL,
    order_id uuid NOT NULL,
    product_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    quantity integer NOT NULL,
    sub_total numeric(10,2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id)
);

DROP TABLE IF EXISTS "orden".order_address CASCADE;

CREATE TABLE "orden".order_address
(
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    street character varying COLLATE pg_catalog."default" NOT NULL,
    postal_code character varying COLLATE pg_catalog."default" NOT NULL,
    city character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT order_address_pkey PRIMARY KEY (id)
);

ALTER TABLE "orden".order_items
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
    REFERENCES "orden".orden (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT
    NOT VALID;

ALTER TABLE "orden".order_address
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
    REFERENCES "orden".orden (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT
    NOT VALID;

CREATE INDEX "IDX_ORDER_CUSTOMER_ID" ON "orden".orden USING btree
    (customer_id ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX "IDX_ORDER_RESTAURANT_ID" ON "orden".orden USING btree
    (restaurant_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Outbox Pattern Table



DROP TABLE IF EXISTS "orden".outbox_events CASCADE;


-- Create the outbox_events table
CREATE TABLE orden.outbox_events
(
    id uuid NOT NULL DEFAULT public.uuid_generate_v4(),
    aggregate_id uuid NOT NULL,
    aggregate_type character varying(255) NOT NULL,
    event_type character varying(255) NOT NULL,
    event_data jsonb NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    processed boolean NOT NULL DEFAULT false,
    CONSTRAINT outbox_events_pkey PRIMARY KEY (id)
);



CREATE INDEX "IDX_OUTBOX_AGGREGATE_ID" ON "orden".outbox_events USING btree
    (aggregate_id ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX "IDX_OUTBOX_PROCESSED" ON "orden".outbox_events USING btree
    (processed ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX "IDX_OUTBOX_CREATED_AT" ON "orden".outbox_events USING btree
    (created_at ASC NULLS LAST)
    TABLESPACE pg_default;