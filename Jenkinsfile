pipeline {
    agent any

    environment {
        DOCKER_PROJECT = "friend-management-system"
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Verify SQL folder') {
            steps {
                sh "ls -l ${env.WORKSPACE}/sql"
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    dir(env.WORKSPACE) {
                        withEnv(["COMPOSE_PROJECT_NAME=${DOCKER_PROJECT}"]) {
                            try {
                                sh "docker-compose -f compose.yml down -v"
                                sh "docker-compose -f compose.yml build --no-cache"
                                sh "docker-compose -f compose.yml up -d"
                            } catch (err) {
                                echo '>>> Containers failed to start, showing logs...'
                                sh "docker-compose -f compose.yml logs --tail=100"
                                throw err
                            }
                        }
                    }
                }
            }
        }

        stage('Inspect DB init folder') {
            steps {
                script {
                    dir(env.WORKSPACE) {
                        withEnv(["COMPOSE_PROJECT_NAME=${DOCKER_PROJECT}"]) {
                            echo '>>> Checking contents of /docker-entrypoint-initdb.d inside db container...'
                            sh "docker-compose -f compose.yml run --rm db ls -l /docker-entrypoint-initdb.d/"
                        }
                    }
                }
            }
        }
    }
}
