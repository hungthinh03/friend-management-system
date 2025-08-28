pipeline {
    agent any

    stages {
        // Stage 1: Checkout repository and ensure clean workspace
        stage('Checkout & Clean') {
            steps {
                // Clone the repo explicitly
                git branch: 'master', url: 'https://github.com/hungthinh03/friend-management-system.git'

                // Ensure workspace is clean (remove leftover files)
                script {
                    sh 'git reset --hard'
                    sh 'git clean -fd'
                }
            }
        }

        // Stage 2: Redeploy Docker containers
        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    // Stop and remove old containers
                    sh 'docker-compose -f compose.yml down'

                    // Build and start containers
                    sh 'docker-compose -f compose.yml up -d --build'
                }
            }
        }
    }
}
