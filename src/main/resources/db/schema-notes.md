# StudyFlow Database Notes

## Database

- PostgreSQL database: `studyflow_db`
- Hibernate `ddl-auto=update` creates/updates schema on startup
- All domain tables are scoped by `user_id` (ownership)

## Tables

| Table | Purpose |
|-------|---------|
| `users` | Accounts (BCrypt password, unique email) |
| `subjects` | Courses/subjects owned by a user |
| `study_sessions` | Timed study blocks; overlap checked in service |
| `tasks` | To-dos with priority/status |
| `assignments` | Deadlines with submission status |
| `notes` | Free-form notes (TEXT content) |
| `pomodoro_sessions` | Focus/break timer records |
| `password_reset_tokens` | One-time reset tokens |

## Indexes

- Unique: `users.email`, `password_reset_tokens.token`
- Query helpers on `user_id` + date/status/subject columns

## Password reset (no SMTP)

SMTP is **not** configured. `POST /api/auth/forgot-password` generates a token and logs it to the application console for development. Configure an email provider before production.
