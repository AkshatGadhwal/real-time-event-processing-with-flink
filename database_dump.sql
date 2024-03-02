--
-- PostgreSQL database dump
--

-- Dumped from database version 15.6 (Ubuntu 15.6-0ubuntu0.23.10.1)
-- Dumped by pg_dump version 15.6 (Ubuntu 15.6-0ubuntu0.23.10.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: test-db-1; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "test-db-1" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_IN';


ALTER DATABASE "test-db-1" OWNER TO postgres;

\connect -reuse-previous=on "dbname='test-db-1'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: user_interaction; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_interaction (
    user_id text NOT NULL,
    sessions_cnt integer,
    total_time_spent integer
);


ALTER TABLE public.user_interaction OWNER TO postgres;

--
-- Data for Name: user_interaction; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_interaction (user_id, sessions_cnt, total_time_spent) FROM stdin;
\.


--
-- Name: user_interaction user_interaction_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_interaction
    ADD CONSTRAINT user_interaction_pkey PRIMARY KEY (user_id);


--
-- PostgreSQL database dump complete
--

