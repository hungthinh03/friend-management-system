pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = "${env.JOB_NAME}-${env.BUILD_ID}"
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    // Export the WORKSPACE variable for use in docker-compose
                    sh "export WORKSPACE=${env.WORKSPACE}"

                    // Stop and remove old containers
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
