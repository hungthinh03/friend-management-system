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
                echo '>>> Local SQL folder contents:'
                sh "ls -l ${env.WORKSPACE}/sql"
                sh "file ${env.WORKSPACE}/sql/* || true"   // show file types
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

        stage('Inspect DB init folder (optional)') {
            steps {
                script {
                    dir(env.WORKSPACE) {
                        withEnv(["COMPOSE_PROJECT_NAME=${DOCKER_PROJECT}"]) {
                            echo '>>> Checking contents of /docker-entrypoint-initdb.d inside db container:'
                            sh "docker-compose -f compose.yml run --rm db ls -l /docker-entrypoint-initdb.d/"
                            sh "docker-compose -f compose.yml run --rm db file /docker-entrypoint-initdb.d/* || true"
                        }
                    }
                }
            }
        }
    }
}
