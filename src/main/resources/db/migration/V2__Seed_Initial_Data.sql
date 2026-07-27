-- Seed initial themes matching the template images
INSERT INTO theme (id, name, description, created_at) VALUES
('ce489ba3-2f01-4dfa-b094-70313c3f5a92', 'Atendimento de Cura', 'Template background: atendimento_de_cura.png', CURRENT_TIMESTAMP),
('6e8437d3-60b8-4966-b0f7-b28b84e78913', 'Gira de Erês', 'Template background: gira_de_eres.png', CURRENT_TIMESTAMP),
('01f3565b-d625-490d-bfcd-f5284f26e0a0', 'Feijoada dos Vovôs', 'Template background: feijoada_dos_vovos.png', CURRENT_TIMESTAMP),
('92f3b2ae-4e92-49df-8e5f-70b1c860cc4a', 'Festa de 7 Saias', 'Template background: festa_de_7_saias.png', CURRENT_TIMESTAMP),
('c29b995e-b025-4fa1-be61-54ac2b28984a', 'Festa de Erês e Pretos Velhos', 'Template background: festa_de_eres_e_pretos_velhos.png', CURRENT_TIMESTAMP),
('fde1d1ab-34fe-4a7c-b700-462b44b7e6e6', 'Gira de Cura Caboclos e Boiadeiros', 'Template background: gira_de_cura_caboclos_e_boiadeiros.png', CURRENT_TIMESTAMP),
('9e8736b6-51a7-4ac8-b027-75e467e95124', 'Gira de Encerramento (Exu)', 'Template background: gira_de_encerramento_exu.png', CURRENT_TIMESTAMP),
('1f521844-771c-4d68-80f9-5a39c8c274cb', 'Gira de Erês e Cura', 'Template background: gira_de_eres_e_cura.png', CURRENT_TIMESTAMP),
('189951bc-a581-4788-a635-2642add90358', 'Gira de Exu e Cura', 'Template background: gira_de_exu_e_cura.png', CURRENT_TIMESTAMP),
('75afc224-3a79-489c-8cbd-13f09237b456', 'Gira de Fechamento', 'Template background: gira_de_fechamento.png', CURRENT_TIMESTAMP),
('3410a41b-f54f-49e8-88ab-bfeb40f8f838', 'Gira de Pretos Velhos e Cura', 'Template background: gira_de_pretos_velhos_e_cura_2.png', CURRENT_TIMESTAMP);

-- Seed duty_events matching schema (id, name, started_at, visible_in_card, description, created_at, updated_at)
INSERT INTO duty_events (id, name, started_at, visible_in_card, description, created_at, updated_at) VALUES
('4f084b7a-2db2-4174-9b9d-c764fc1f04db', 'Chegada na casa', '09:00:00', false, '2026-07-21 23:16:23.021801', CURRENT_TIMESTAMP),
('9aa17c2a-65bd-4a38-9f61-68080c187791', 'Café da manhã', '09:30:00', false, '2026-07-21 23:17:01.110171', CURRENT_TIMESTAMP),
('677a243b-9ffa-43d0-9187-afe275e93fda', 'Almoço', '12:00:00', false, '2026-07-21 23:17:29.921157', CURRENT_TIMESTAMP),
('d4dd41cd-9354-4788-b3c7-8f7fee416fd4', 'Gira de Exu e Pombigira', '16:00:00', true, '2026-07-21 23:19:02.714366', CURRENT_TIMESTAMP),
('4c41fb7c-d704-4a06-9af8-eac2a204e9f5', 'Gira de Pretos Velhos', '16:00:00', true, 'Inicio da gira de Pretos Velhos', '2026-07-21 23:19:16.932076', CURRENT_TIMESTAMP),
('d073e891-6010-4323-85cc-11d108fe35f4', 'Gira de Erês', '16:00:00', true, '2026-07-21 23:19:34.348086', CURRENT_TIMESTAMP),
('fb129a8f-eb7f-45fc-aec1-de35617f1669', 'Gira de Erês e Pretos Velhos', '16:00:00', true, '2026-07-21 23:19:47.289486', CURRENT_TIMESTAMP),
('0dc8f08b-d8ef-4620-8d0a-f45c943703a3', 'Gira de Caboclos e Boiadeiros', '16:00:00', true, '2026-07-21 23:20:04.589951', CURRENT_TIMESTAMP),
('11389f4f-6742-4d78-85fc-05ba576a16ea', 'Atendimento de cura', '14:00:00', true, '2026-07-21 23:18:17.888258', CURRENT_TIMESTAMP),
('8b57ad76-bd4f-4931-a49e-d908beba2427', 'Chegada na casa', '09:00:00', false, '2026-07-21 23:16:23.021801', CURRENT_TIMESTAMP);

-- Seed templates for existing themes
INSERT INTO template (id, name, image_path, theme_id, created_at) VALUES
(gen_random_uuid(), 'Atendimento de Cura Template 1', 'atendimento_de_cura_2.png', 'ce489ba3-2f01-4dfa-b094-70313c3f5a92', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Erês Template 1', 'gira_de_eres.png', '6e8437d3-60b8-4966-b0f7-b28b84e78913', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Feijoada dos Vovôs Template 1', 'feijoada_dos_vovos.png', '01f3565b-d625-490d-bfcd-f5284f26e0a0', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Feijoada dos Vovôs Template 2', 'feijoada_dos_vovos_2.png', '01f3565b-d625-490d-bfcd-f5284f26e0a0', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Festa de 7 Saias Template 1', 'festa_de_7_saias.png', '92f3b2ae-4e92-49df-8e5f-70b1c860cc4a', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Festa de Erês e Pretos Velhos Template 1', 'festa_de_eres_e_pretos_velhos.png', 'c29b995e-b025-4fa1-be61-54ac2b28984a', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Cura Caboclos e Boiadeiros Template 1', 'gira_de_cura_caboclos_e_boiadeiros.png', 'fde1d1ab-34fe-4a7c-b700-462b44b7e6e6', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Encerramento (Exu) Template 1', 'gira_de_encerramento_exu.png', '9e8736b6-51a7-4ac8-b027-75e467e95124', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Erês e Cura Template 1', 'gira_de_eres_e_cura.png', '1f521844-771c-4d68-80f9-5a39c8c274cb', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Exu e Cura Template 1', 'gira_de_exu_e_cura.png', '189951bc-a581-4788-a635-2642add90358', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Exu e Cura Template 2', 'gira_de_exu_e_cura_2.png', '189951bc-a581-4788-a635-2642add90358', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Exu e Cura Template 3', 'gira_de_exu_e_cura_3.png', '189951bc-a581-4788-a635-2642add90358', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Gira de Pretos Velhos e Cura Template 1', 'gira_de_pretos_velhos_e_cura_2.png', '3410a41b-f54f-49e8-88ab-bfeb40f8f838', CURRENT_TIMESTAMP);
