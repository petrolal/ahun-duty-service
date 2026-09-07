# Cloud Run Service (Always Free eligible under usage limits)
resource "google_cloud_run_v2_service" "app" {
  name     = var.service_name
  location = var.region
  ingress  = "INGRESS_TRAFFIC_ALL"

  template {
    service_account = google_service_account.app_sa.email

    # Scale to 0 to guarantee Always Free eligibility when idle
    scaling {
      min_instance_count = 0
      max_instance_count = 5
    }

    containers {
      # Initial placeholder image. The actual image will be deployed by the CI/CD pipeline.
      image = "us-docker.pkg.dev/cloudrun/container/hello"

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      ports {
        container_port = 8080
      }

      # Inject all plaintext application environment variables dynamically
      dynamic "env" {
        for_each = var.env_vars
        content {
          name  = env.key
          value = env.value
        }
      }

      # Inject secret-backed environment variables from Secret Manager
      dynamic "env" {
        for_each = var.secret_env_vars
        content {
          name = env.key
          value_source {
            secret_key_ref {
              secret  = env.value
              version = "latest"
            }
          }
        }
      }
    }
  }

  # Ignore changes to the image since it is managed by the CI/CD pipeline
  lifecycle {
    ignore_changes = [
      template[0].containers[0].image
    ]
  }

  # Ensure the service starts after the app SA can read the referenced secrets.
  depends_on = [
    google_secret_manager_secret_iam_member.secret_accessor
  ]
}
