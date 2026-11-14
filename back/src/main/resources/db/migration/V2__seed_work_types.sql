INSERT INTO work_type (
    id,
    code,
    name,
    duration_min,
    max_start_delay_min,
    deadline_kind,
    deadline_value,
    repeat_deadline_value,
    pgp_relevant
) VALUES 
(
    'a8cd0c29-c108-470c-98a2-f158e2d9b1a0',
    'INST_PON_MKD',
    'Инсталляция ШПД/IPTV, МКД, PON',
    90,
    10,
    'WORKING_DAYS',
    3,
    NULL,
    FALSE
),
(
    'ab17fd6f-fc97-4c0e-a362-1efa364dacdb',
    'INST_FTTB_MKD',
    'Инсталляция ШПД/IPTV, МКД, FTTB',
    60,
    10,
    'WORKING_DAYS',
    3,
    NULL,
    FALSE
),
(
    'b1018262-1a7d-48ff-8396-86ffa1d4974a',
    'INST_PRIVATE_VLS',
    'Инсталляция, частный сектор, с ВЛС',
    180,
    10,
    'WORKING_DAYS',
    5,
    NULL,
    FALSE
),
(
    '1984fdb7-c4d5-49de-9506-f18ddca4b364',
    'ADD_SERVICE',
    'Доподключение услуги (IPTV, Wi-Fi-роутер)',
    40,
    NULL,
    'SLOT',
    NULL,
    NULL,
    FALSE
),
(
    '4b0ae6d0-6613-4077-9e19-f73601ff891f',
    'SURVEY_PRIVATE',
    'Обследование (частный сектор)',
    45,
    NULL,
    'WORKING_DAYS',
    2,
    NULL,
    FALSE
),
(
    'f6cf6779-e6ca-4bad-be2c-afd483d66562',
    'REPAIR',
    'Устранение неисправности (обращение абонента)',
    60,
    NULL,
    'HOURS',
    24,
    12,
    TRUE
),
(
    '254fa057-a1b2-4a38-a174-6f17aed6cd87',
    'DEGRADATION',
    'Устранение деградации, выявленной системой мониторинга качества',
    45,
    NULL,
    'HOURS',
    48,
    NULL,
    TRUE
),
(
    '75f02bfd-482e-4798-9b2a-b1bbfc690682',
    'DISMANTLE',
    'Демонтаж АО',
    30,
    NULL,
    'WORKING_DAYS',
    5,
    NULL,
    FALSE
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO work_type_profile (work_type_id, profile) VALUES
('a8cd0c29-c108-470c-98a2-f158e2d9b1a0', 'INSTALL'),
('ab17fd6f-fc97-4c0e-a362-1efa364dacdb', 'INSTALL'),
('b1018262-1a7d-48ff-8396-86ffa1d4974a', 'INSTALL'),
('1984fdb7-c4d5-49de-9506-f18ddca4b364', 'INSTALL'),
('4b0ae6d0-6613-4077-9e19-f73601ff891f', 'INSTALL'),
('4b0ae6d0-6613-4077-9e19-f73601ff891f', 'LTP3'),
('f6cf6779-e6ca-4bad-be2c-afd483d66562', 'LTP3'),
('254fa057-a1b2-4a38-a174-6f17aed6cd87', 'LTP3'),
('75f02bfd-482e-4798-9b2a-b1bbfc690682', 'LTP3')
ON CONFLICT (work_type_id, profile) DO NOTHING;
