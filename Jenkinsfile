pipeline {
    agent any

    environment {
        WORKSPACE = "${env.WORKSPACE}"  // Jenkins workspace
        DOCKER_PROJECT = "friend-management-system"  // fixed project name
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
                    // Stop and remove old containers + volumes
                    sh "docker-compose -f compose.yml -p ${DOCKER_PROJECT} down -v"

                    // Build images without cache
                    sh "docker-compose -f compose.yml -p ${DOCKER_PROJECT} build --no-cache"

                    // Start containers
                    sh "docker-compose -f compose.yml -p ${DOCKER_PROJECT} up -d"
                }
            }
        }
    }
}
