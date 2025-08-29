pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_FILE = 'compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Gradle') {
            steps {
                sh 'chmod +x ./gradlew'          // make gradlew executable
                sh './gradlew clean build -x test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh "docker-compose -f ${DOCKER_COMPOSE_FILE} build"
            }
        }

        stage('Recreate DB') {
            steps {
                // Stop containers and remove volumes to force fresh DB
                sh "docker-compose -f ${DOCKER_COMPOSE_FILE} down -v || true"
            }
        }

        stage('Up Containers') {
            steps {
                sh "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
            }
        }

        stage('Health Check') {
            steps {
                script {
                    // Wait until DB is healthy
                    sh """
                    RETRIES=10
                    until docker inspect --format='{{.State.Health.Status}}' frienddb | grep -q "healthy" || [ \$RETRIES -eq 0 ]; do
                        echo "Waiting for DB to become healthy..."
                        sleep 5
                        RETRIES=\$((RETRIES-1))
                    done
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Build and deployment succeeded!"
        }
        failure {
            echo "Build failed."
        }
        always {
            sh "docker ps"
        }
    }
}
