pipeline {
    agent any

    environment {
        DOCKER_PROJECT = "friend-management-system"  // project name for docker compose
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Verify SQL file') {
            steps {
                sh "ls -l ${env.WORKSPACE}/sql/"
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    dir(env.WORKSPACE) {
                        withEnv(["COMPOSE_PROJECT_NAME=${DOCKER_PROJECT}"]) {
                            sh "docker-compose -f compose.yml down -v"
                            sh "docker-compose -f compose.yml build --no-cache"
                            sh "docker-compose -f compose.yml up -d"
                        }
                    }
                }
            }
        }
    }
}
