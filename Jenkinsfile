pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Clean workspace and fetch latest code
                cleanWs()
                checkout scm
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    // Stop & remove any existing containers to avoid port conflicts
                    sh 'docker-compose -f compose.yml down || true'

                    // Optionally remove orphan containers from previous runs
                    sh 'docker-compose -f compose.yml rm -f || true'

                    // Build and start containers
                    sh 'docker-compose -f compose.yml build --no-cache'
                    sh 'docker-compose -f compose.yml up -d'
                }
            }
        }
    }
}
