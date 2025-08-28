pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Clean workspace and pull fresh code
                cleanWs()
                checkout scm
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    // Stop and remove old containers
                    sh 'docker-compose -f compose.yml down'

                    // Build images without cache
                    sh 'docker-compose -f compose.yml build --no-cache'

                    // Start containers
                    sh 'docker-compose -f compose.yml up -d'
                }
            }
        }

    }
}
