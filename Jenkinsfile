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

        stage('Clean SQL mount') {
            steps {
                echo '>>> Ensuring SQL folder only contains valid files'
                sh "find ${env.WORKSPACE}/sql -mindepth 1 -type d -exec rm -rf {} +"
            }
        }

        stage('Build Java app') {
            steps {
                dir(env.WORKSPACE) {
                    sh "chmod +x gradlew"
                    sh "./gradlew clean build -x test"
                }
            }
        }

        stage('Clean old Docker containers & volumes') {
            steps {
                script {
                    echo '>>> Removing any old containers'
                    sh "docker rm -f $(docker ps -a -q --filter name=${DOCKER_PROJECT}-* || true) || true"
                    echo '>>> Removing any old volumes'
                    sh "docker volume rm -f $(docker volume ls -q --filter name=${DOCKER_PROJECT}-* || true) || true"
                }
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
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

        stage('Inspect DB init folder (optional)') {
            steps {
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
