variable "service_name" {
  description = "Name of the Cloud Run service"
  type        = string
}

variable "region" {
  description = "The region for resources"
  type        = string
}

variable "project_id" {
  description = "The GCP Project ID"
  type        = string
}

variable "github_actions_sa_email" {
  description = "Email of the GitHub Actions CI/CD service account, provisioned in the shared IaC repo (ahun-cloud-env) via Workload Identity Federation. Granted actAs on the runtime SA so the pipeline can deploy new revisions."
  type        = string
}

# --- Application Configuration Variables ---

variable "env_vars" {
  description = "A map of plaintext environment variables to inject into the container"
  type        = map(string)
  default     = {}
}

variable "secret_env_vars" {
  description = "A map of environment variable name => Secret Manager secret id. Injected as secret references and the app SA is granted secretAccessor on each."
  type        = map(string)
  default     = {}
}

variable "scheduler_jobs" {
  description = "A map of scheduler jobs to invoke the Cloud Run service"
  type = map(object({
    description = string
    schedule    = string
    time_zone   = string
    uri_path    = string
    http_method = string
    body        = string
  }))
  default = {}
}
