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
                    // Build images and start containers (force rebuild)
                    sh 'docker-compose -f compose.yml up -d --build --no-cache'
                }
            }
        }
    }
}
