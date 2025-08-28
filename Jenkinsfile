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
                    // Stop containers and remove volumes to reinitialize DB
                    sh 'docker-compose -f compose.yml down -v'

                    // Build images without cache
                    sh 'docker-compose -f compose.yml build --no-cache'

                    // Start containers
                    sh 'docker-compose -f compose.yml up -d'
                }
            }
        }

    }
}