CREATE TABLE public.email (
    id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    emailuser character varying(255)
);

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.telefone (
    id bigint NOT NULL,
    telefone character varying(255),
    usuario_id bigint NOT NULL,
    tipo character varying(255)
);

CREATE TABLE public.usuario (
    id bigint NOT NULL,
    idade integer,
    login character varying(20) NOT NULL,
    nome character varying(20) NOT NULL,
    senha character varying(20) NOT NULL,
    sobrenome character varying(50) NOT NULL,
    sexo character varying(255),
    bairro character varying(255),
    cep character varying(255),
    complemento character varying(255),
    gia character varying(255),
    ibge character varying(255),
    localidade character varying(255),
    logradouro character varying(255),
    uf character varying(255),
    numero character varying(255),
    salario double precision,
    imagem text
);
