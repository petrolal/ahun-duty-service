# Dedicated Service Account for the Application Container
resource "google_service_account" "app_sa" {
  account_id   = "${var.service_name}-app-sa"
  display_name = "Service Account for ${var.service_name} App"
}

# Dedicated Service Account for Cloud Scheduler to invoke Cloud Run securely
resource "google_service_account" "scheduler_sa" {
  account_id   = "${var.service_name}-sched-sa" # Keep it under 30 chars
  display_name = "Cloud Scheduler Invoker Account for ${var.service_name}"
}

# Bind Cloud Run Invoker role to the Scheduler Service Account
resource "google_cloud_run_v2_service_iam_member" "scheduler_invoker" {
  location = google_cloud_run_v2_service.app.location
  project  = google_cloud_run_v2_service.app.project
  name     = google_cloud_run_v2_service.app.name
  role     = "roles/run.invoker"
  member   = "serviceAccount:${google_service_account.scheduler_sa.email}"
}

# Allow the application to read the Secret Manager secrets referenced as env vars
resource "google_secret_manager_secret_iam_member" "secret_accessor" {
  for_each  = var.secret_env_vars
  secret_id = each.value
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.app_sa.email}"
}

# The GitHub Actions CI/CD identity itself (service account + Workload Identity
# Federation + artifactregistry.writer + run.developer) is provisioned in the
# shared IaC repo (ahun-cloud-env, module.github_actions_oidc), the only root
# applied with human credentials. This root just grants it actAs on the runtime
# SA it owns, so the pipeline can roll out new Cloud Run revisions.
resource "google_service_account_iam_member" "github_sa_act_as_app" {
  service_account_id = google_service_account.app_sa.name
  role               = "roles/iam.serviceAccountUser"
  member             = "serviceAccount:${var.github_actions_sa_email}"
}

# Allow unauthenticated (public) access to the service
resource "google_cloud_run_v2_service_iam_member" "public_invoker" {
  location = google_cloud_run_v2_service.app.location
  project  = google_cloud_run_v2_service.app.project
  name     = google_cloud_run_v2_service.app.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
