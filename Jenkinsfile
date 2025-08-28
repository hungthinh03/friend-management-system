pipeline {
    agent any

    stages {
        stage('Checkout and Clean') {
            steps {
                // Checkout repo
                checkout scm

                // Ensure workspace is clean and up-to-date
                script {
                    sh 'git reset --hard'
                    sh 'git clean -fd'
                }
            }
        }

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
