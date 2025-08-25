--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-08-25 09:41:52

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 221 (class 1259 OID 16469)
-- Name: account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.account_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 16417)
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    user_id integer DEFAULT nextval('public.account_id_seq'::regclass) NOT NULL,
    email character varying(50)
);


ALTER TABLE public.account OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16428)
-- Name: block; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.block (
    blocker_id integer NOT NULL,
    blocked_id integer NOT NULL
);


ALTER TABLE public.block OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16425)
-- Name: follower; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.follower (
    follower_id integer NOT NULL,
    followee_id integer NOT NULL
);


ALTER TABLE public.follower OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16422)
-- Name: friend; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friend (
    user_id1 integer NOT NULL,
    user_id2 integer NOT NULL
);


ALTER TABLE public.friend OWNER TO postgres;

--
-- TOC entry 4813 (class 0 OID 16417)
-- Dependencies: 217
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.account (user_id, email) FROM stdin;
1	andy@example.com
2	john@example.com
3	alice@example.com
4	bob@example.com
5	carol@example.com
\.


--
-- TOC entry 4816 (class 0 OID 16428)
-- Dependencies: 220
-- Data for Name: block; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.block (blocker_id, blocked_id) FROM stdin;
\.


--
-- TOC entry 4815 (class 0 OID 16425)
-- Dependencies: 219
-- Data for Name: follower; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.follower (follower_id, followee_id) FROM stdin;
\.


--
-- TOC entry 4814 (class 0 OID 16422)
-- Dependencies: 218
-- Data for Name: friend; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friend (user_id1, user_id2) FROM stdin;
\.


--
-- TOC entry 4823 (class 0 OID 0)
-- Dependencies: 221
-- Name: account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.account_id_seq', 5, true);


--
-- TOC entry 4661 (class 2606 OID 16458)
-- Name: block block_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block
    ADD CONSTRAINT block_pkey PRIMARY KEY (blocker_id, blocked_id);


--
-- TOC entry 4659 (class 2606 OID 16446)
-- Name: follower follower_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follower
    ADD CONSTRAINT follower_pkey PRIMARY KEY (follower_id, followee_id);


--
-- TOC entry 4657 (class 2606 OID 16434)
-- Name: friend friend_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friend
    ADD CONSTRAINT friend_pkey PRIMARY KEY (user_id1, user_id2);


--
-- TOC entry 4655 (class 2606 OID 16421)
-- Name: account user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4666 (class 2606 OID 16464)
-- Name: block fk_blocked_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block
    ADD CONSTRAINT fk_blocked_user FOREIGN KEY (blocked_id) REFERENCES public.account(user_id) ON DELETE CASCADE;


--
-- TOC entry 4667 (class 2606 OID 16459)
-- Name: block fk_blocker_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block
    ADD CONSTRAINT fk_blocker_user FOREIGN KEY (blocker_id) REFERENCES public.account(user_id) ON DELETE CASCADE;


--
-- TOC entry 4664 (class 2606 OID 16452)
-- Name: follower fk_followee_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follower
    ADD CONSTRAINT fk_followee_user FOREIGN KEY (followee_id) REFERENCES public.account(user_id) ON DELETE CASCADE;


--
-- TOC entry 4665 (class 2606 OID 16447)
-- Name: follower fk_follower_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follower
    ADD CONSTRAINT fk_follower_user FOREIGN KEY (follower_id) REFERENCES public.account(user_id) ON DELETE CASCADE;


--
-- TOC entry 4662 (class 2606 OID 16435)
-- Name: friend fk_friend_user1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friend
    ADD CONSTRAINT fk_friend_user1 FOREIGN KEY (user_id1) REFERENCES public.account(user_id) ON DELETE CASCADE;


--
-- TOC entry 4663 (class 2606 OID 16440)
-- Name: friend fk_friend_user2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friend
    ADD CONSTRAINT fk_friend_user2 FOREIGN KEY (user_id2) REFERENCES public.account(user_id) ON DELETE CASCADE;


-- Completed on 2025-08-25 09:41:53

--
-- PostgreSQL database dump complete
--

