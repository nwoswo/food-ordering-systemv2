-- Create schema for consulta service
CREATE SCHEMA IF NOT EXISTS consulta;

-- Create the order_outbox table
CREATE TABLE consulta.order_outbox
(
    id uuid NOT NULL,
    aggregate_id uuid NOT NULL,
    aggregate_type character varying(255) NOT NULL,
    event_type character varying(255) NOT NULL,
    event_data jsonb NOT NULL,
    created_at timestamp without time zone NOT NULL,
    processed boolean NOT NULL DEFAULT false,
    order_id uuid,
    customer_id uuid,
    restaurant_id uuid,
    price numeric(10,2),
    order_status character varying(50),
    failure_messages text,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

-- Create indexes for better performance
CREATE INDEX "IDX_ORDER_OUTBOX_ORDER_ID" ON consulta.order_outbox USING btree
    (order_id ASC NULLS LAST);

CREATE INDEX "IDX_ORDER_OUTBOX_AGGREGATE_ID" ON consulta.order_outbox USING btree
    (aggregate_id ASC NULLS LAST);

CREATE INDEX "IDX_ORDER_OUTBOX_EVENT_TYPE" ON consulta.order_outbox USING btree
    (event_type ASC NULLS LAST);

CREATE INDEX "IDX_ORDER_OUTBOX_PROCESSED" ON consulta.order_outbox USING btree
    (processed ASC NULLS LAST);

CREATE INDEX "IDX_ORDER_OUTBOX_CREATED_AT" ON consulta.order_outbox USING btree
    (created_at ASC NULLS LAST);
