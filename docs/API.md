# BBSK API — Документация REST API

> Сервис оперативного планирования и управления выездными бригадами (Кейс №05 «Ростелеком»).  
> Базовый путь: `/api/v1`, порт по умолчанию: `8080`.  
> Интерактивный просмотр без запуска сервера: откройте файл **[index.html](index.html)** в любом браузере или загрузите **[openapi.yaml](openapi.yaml)** в [Swagger Editor](https://editor.swagger.io).

---

## Содержание

1. [Служебные методы](#1-служебные-методы)
2. [Аутентификация и безопасность](#2-аутентификация-и-безопасность)
3. [Сотрудники и смены (Staff & Shifts)](#3-сотрудники-и-смены-staff--shifts)
4. [Инфраструктура и здания (Buildings, Access Nodes, Service Centers)](#4-инфраструктура-и-здания)
5. [Типы работ (Work Types)](#5-типы-работ-work-types)
6. [Пакетная инициализация справочников (Bulk Reference & Travel Matrix)](#6-пакетная-инициализация-справочников)

---

## 1. Служебные методы

### `GET /api/v1/health`
Проверка готовности сервиса (опрашивается оркестратором кейса до 120 с после старта).

* **Ответ `200 OK`**:
```json
{
  "status": "ok",
  "team": "BBSK",
  "version": "1.0.0"
}
```

---

## 2. Аутентификация и безопасность

Аутентификация построена на базе JWT токенов (алгоритм HS256). Токен передаётся в защищённой `HttpOnly` cookie с именем `access_token` на 24 часа. UUID пользователя зашит внутри токена и не раскрывается на клиенте.

### `POST /api/v1/auth/register`
Регистрация диспетчера.
* **Тело запроса**: `username`, `password`, `email`.
* **Ответ `201 Created`**: `username`, `email`, `role`, `created_at`.

### `POST /api/v1/auth/login`
Авторизация в системе.
* **Тело запроса**: `username`, `password`.
* **Ответ `200 OK`**: профиль пользователя + заголовок `Set-Cookie: access_token=...; HttpOnly; SameSite=Lax`.

### `POST /api/v1/auth/logout`
Выход из системы (очистка cookie `access_token`).

### `GET /api/v1/auth/me`
Получение профиля текущего пользователя по JWT из cookie.

---

## 3. Сотрудники и смены (Staff & Shifts)

Все операции мутации и поиска выполняются по первичному ключу `id: UUID`.

### Эндпоинты сотрудников (`/api/v1/staff`)
* **`GET /api/v1/staff`** — список всех сотрудников (опциональный фильтр `?service_center_id={UUID}`).
* **`GET /api/v1/staff/{id}`** — получение сотрудника по UUID.
* **`POST /api/v1/staff`** — создание сотрудника (проверяет наличие СЦ и смены по UUID, уникальность кода).
* **`PUT /api/v1/staff/{id}`** — частичное обновление параметров сотрудника.
* **`DELETE /api/v1/staff/{id}`** — удаление сотрудника.

### Эндпоинты смен (`/api/v1/staff/shifts`)
* **`GET /api/v1/staff/shifts`** — список всех графиков смен.
* **`GET /api/v1/staff/shifts/{id}`** — смене по UUID.
* **`POST /api/v1/staff/shifts`** — создание нового графика смены (`start_time`, `end_time`, `break_window_from`, `break_window_to`, `break_duration_min`).
* **`DELETE /api/v1/staff/shifts/{id}`** — удаление смены (с проверкой: если смена привязана к сотрудникам, возвращается `409 Conflict`).

---

## 4. Инфраструктура и здания

Контроллер объединяет все объекты адресной программы и сетевой топологии (`/api/v1/buildings`).

### Здания (Buildings)
* **`GET /api/v1/buildings`** — список зданий с фильтрами `building_type` (`MKD`/`PRIVATE`), `access_node_id`, `service_center_id`.
* **`GET /api/v1/buildings/{id}`** — здание по UUID.
* **`POST /api/v1/buildings`** — создание здания (привязка к узлу доступа и СЦ зоны).
* **`PUT /api/v1/buildings/{id}`** — обновление здания по UUID.
* **`DELETE /api/v1/buildings/{id}`** — удаление здания по UUID.

### Узлы доступа связи (Access Nodes)
* **`GET /api/v1/buildings/access-nodes`** — список узлов доступа (фильтр по `technology`: `PON`, `FTTB`, `XDSL`, `DOCSIS`).
* **`GET /api/v1/buildings/access-nodes/{id}`** — узел доступа по UUID.
* **`POST /api/v1/buildings/access-nodes`** — создание узла (`OLT`, `SWITCH`, `DSLAM`, `CMTS`).
* **`PUT /api/v1/buildings/access-nodes/{id}`** — обновление параметров узла.
* **`DELETE /api/v1/buildings/access-nodes/{id}`** — удаление узла (с проверкой: если к узлу привязаны здания, возвращается `409 Conflict`).

### Сервисные центры (Service Centers)
* **`GET /api/v1/buildings/service-centers`** — список СЦ (базовых депо).
* **`GET /api/v1/buildings/service-centers/{id}`** — СЦ по UUID.
* **`POST /api/v1/buildings/service-centers`** — создание СЦ (`openTime`, `closeTime`, `restockDurationMin`).
* **`PUT /api/v1/buildings/service-centers/{id}`** — обновление параметров СЦ.
* **`DELETE /api/v1/buildings/service-centers/{id}`** — удаление СЦ (с проверкой: если к СЦ привязаны сотрудники, возвращается `409 Conflict`).

---

## 5. Типы работ (Work Types)

Справочник нормативных работ со сроками выполнения и требуемыми квалификациями (`/api/v1/work-types`). В БД предзаполнены все 8 типов работ миграцией `V2__seed_work_types.sql`.

* **`GET /api/v1/work-types`** — список типов работ со всеми параметрами.
* **`GET /api/v1/work-types/{id}`** — тип работ по UUID.
* **`POST /api/v1/work-types`** — создание нового типа работ.
* **`PUT /api/v1/work-types/{id}`** — обновление типа работ.
* **`DELETE /api/v1/work-types/{id}`** — удаление типа работ.

---

## 6. Пакетная инициализация справочников

Методы для быстрого развёртывания тестовых прогонов и бенчмарков оркестратора (`/api/v1/reference`).

### `PUT /api/v1/reference`
Полная инициализация справочников. Удаляет все предыдущие дни, планы и события, загружает в транзакции:
1. Сервисные центры
2. Узлы доступа
3. Дома (с автоматической дедупликацией)
4. Типы работ
5. Графики смен и сотрудников

* **Ответ `200 OK`**:
```json
{
  "status": "loaded",
  "counts": {
    "service_centers": 5,
    "staff": 40,
    "access_nodes": 142,
    "buildings": 1260,
    "work_types": 8
  }
}
```

### `PUT /api/v1/reference/travel-matrix`
Загрузка матрицы времени в пути (`durations_sec`) и расстояний (`distances_m`) между всеми точками (1265 точек).

* **Ответ `200 OK`**:
```json
{
  "status": "loaded",
  "counts": {
    "points": 1265
  }
}
```
