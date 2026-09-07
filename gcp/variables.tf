variable "project_id" {
  description = "The GCP Project ID where the Cloud Run service is created"
  type        = string
}

variable "region" {
  description = "The region for the resources"
  type        = string
  default     = "us-central1"
}

variable "github_actions_sa_email" {
  description = "Email of the GitHub Actions CI/CD service account (shared IaC output github_actions_sa_emails[\"ahun-duty-service\"]). Passed by the pipeline as TF_VAR_github_actions_sa_email; also the value of the WIF_SERVICE_ACCOUNT secret."
  type        = string
  default     = ""
}

# --- Application configuration (injected by the pipeline as TF_VAR_*) ---

variable "spring_datasource_url" {
  description = "JDBC URL for this service's Supabase database (from the shared IaC output spring_datasource_urls)"
  type        = string
  default     = ""
}

variable "spring_datasource_username" {
  description = "Database username"
  type        = string
  default     = "postgres"
}

variable "spring_datasource_password" {
  description = "Database password (Supabase project db password)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "telegram_chat_id" {
  description = "Target Telegram chat/group id this service sends to"
  type        = string
  sensitive   = true
  default     = ""
}

variable "bot_token_secret_id" {
  description = "Secret Manager secret id holding the shared Telegram bot token (shared IaC output bot_token_secret, e.g. ahun-telegram-bot-token)"
  type        = string
  default     = ""
}

variable "extra_env_vars" {
  description = "Additional plaintext env vars to inject, merged over the built-in set"
  type        = map(string)
  default     = {}
}
