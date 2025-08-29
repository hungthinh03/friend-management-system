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
            }
        }

        stage('Clean SQL mount') {
            steps {
                echo '>>> Ensuring SQL folder only contains valid files'
                sh """
                    # Remove any directories inside the SQL folder
                    find ${env.WORKSPACE}/sql -mindepth 1 -type d -exec rm -rf {} +
                """
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
                                // Ensure old containers and named volumes are removed
                                sh "docker-compose -f compose.yml down -v"
                                // Optional: prune any unused volumes to avoid conflicts
                                sh "docker volume prune -f || true"
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
                            echo '>>> Checking contents of /docker-entrypoint-initdb.d inside db container...'
                            sh "docker-compose -f compose.yml run --rm db ls -l /docker-entrypoint-initdb.d/"
                        }
                    }
                }
            }
        }
    }
}
