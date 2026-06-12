# DeliverIt

A small delivery-task tracker: an Android app (Kotlin, Jetpack Compose, MVI, Hilt, Retrofit, Room) backed by a minimal Node/Express server. Users can create delivery tasks, view them in a list, and update a task's status while seeing its full status history.

## Features

- **Task list** — all delivery tasks with pull-to-refresh, status badges.
- **Task detail** — route summary, status update via filter chips, a full status-history timeline.
- **Create task** — validated form with per-field errors.
- **Offline reads** — tasks are cached in Room; the list stays available without a connection.

## Tech stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVI |
| DI | Hilt |
| Networking | Retrofit + Gson |
| Local cache | Room |
| Server | Node.js / Express |

## Project structure

```
app/src/main/java/com/deliverit/app/
├── data/
│   ├── local/        # Room: database, DAO, entities, converters
│   ├── remote/       # Retrofit: API, DTOs, requests, error parsing
│   └── ...           # TaskRepository + default/fake implementations
├── di/               # Hilt modules
├── model/            # Domain models (DeliveryTask, DeliveryStatus, ...)
└── ui/
    ├── common/       # MviViewModel base, UiText, StatusBadge, ...
    ├── tasklist/     # Screen + Contract + ViewModel
    │   └── components/   # TaskListTopBar, TaskListContent, TaskListItem, ...
    ├── taskdetail/
    │   └── components/   # TaskSummaryCard, StatusSelector, StatusTimeline, ...
    └── createtask/
        └── components/   # CreateTaskForm, TaskFormTextField, SubmitTaskButton, ...

server/index.js       # Express server (GET /tasks, POST /tasks, PATCH /tasks/:id/status)
```

Each screen follows the same pattern: the `*Screen` composable is a thin coordinator (collects state, handles one-shot events, wires the Scaffold), while all visual pieces live as small stateless composables in the screen's `components` package. Components receive plain state and callbacks — never a ViewModel — so they are previewable and reusable.

## Getting started

### 1. Run the server

```bash
cd server
npm install
npm run start   # listens on port 3000
```

### 2. Run the app

Open the project in Android Studio and run the `app` configuration on an emulator.

### Server URL per build type

The base URL is injected at build time via `BuildConfig.SERVER_URL`:

- **debug** — `http://10.0.2.2:3000/` (local dev server).
- **release** — taken from the `PROD_API_URL` environment variable at build time, falling back to the hosted instance.

```bash
PROD_API_URL=https://my-server.example.com/ ./gradlew :app:assembleRelease
```

## Assumptions

- **Single user, trusted client** — there is no authentication or authorization; anyone who can reach the server can read and modify all tasks.
- **The server is the source of truth** — the Room database is a local cache of server state. Writes (create / status update) go to the server first, and the local cache is updated only on success.
- **Small data set** — the task list is fetched in full on refresh (`replaceAll`); no pagination or incremental sync.
- **Any status transition is valid** — the server validates that a status is one of the known values, but allows any transition (e.g. `DELIVERED` → `PENDING`).

## Simplifications

- **In-memory server storage** — tasks live in a plain array; restarting the server wipes all data. No database, no input sanitization beyond presence checks.
- **No offline write support** — reads work offline thanks to Room, but creating a task or updating a status requires a live connection; failures surface as user-facing error messages rather than being queued for retry.
- **No sync/conflict strategy** — `refreshTasks` blindly replaces the local cache with the server response; concurrent edits from another client would simply be overwritten locally.
- **Unit tests only** — ViewModels and the repository are covered with a fake repository; there are no UI/instrumentation or server tests.

## What I would improve for production

- **Persistence & API hardening on the server**: a real database (Postgres/SQLite), input validation/sanitization, status-transition rules, idempotency keys for task creation, and pagination on `GET /tasks`.
- **Auth & transport security**: token-based auth (e.g. OAuth/JWT), HTTPS only, per-user data scoping.
- **Offline-first writes**: queue mutations locally (WorkManager) with retry and conflict resolution, so the app is fully usable without connectivity.
- **Real-time updates from the server**: listen for server-side changes (WebSocket / SSE / FCM push) and persist them straight into the local Room database, so the UI — which already observes Room — updates automatically without manual refresh.
- **Smarter sync**: delta sync instead of replacing the whole cache.
- **Modularization**: split the single `app` module into feature and layer modules (e.g. `:core:network`, `:core:database`, `:core:model`) for faster incremental builds, enforced layer boundaries, and easier parallel work as the team grows.
- **Observability**: crash reporting and analytics on the client; structured logging, metrics, and health checks on the server.
- **Testing & CI**: Compose UI tests, end-to-end tests against the real server, server unit tests, and a CI pipeline running lint + tests on every PR.
