pipeline {
    agent any

    environment {
        DOCKER_PROJECT = "friend-management-system"
        WORKSPACE_DIR = "${env.WORKSPACE}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Verify SQL folder') {
            steps {
                script {
                    if (fileExists("${WORKSPACE_DIR}/sql")) {
                        echo ">>> Local SQL folder contents:"
                        sh "ls -l ${WORKSPACE_DIR}/sql"
                        env.SQL_EXISTS = "true"
                    } else {
                        echo ">>> SQL folder not found, skipping SQL cleanup"
                        env.SQL_EXISTS = "false"
                    }
                }
            }
        }

        stage('Clean SQL mount') {
            when {
                expression { env.SQL_EXISTS == "true" }
            }
            steps {
                echo ">>> Ensuring SQL folder only contains valid files"
                sh "find ${WORKSPACE_DIR}/sql -mindepth 1 -type d -exec rm -rf {} +"
            }
        }

        stage('Build Java app') {
            steps {
                dir("${WORKSPACE_DIR}") {
                    sh "chmod +x gradlew"
                    sh "./gradlew clean build -x test"
                }
            }
        }

        stage('Clean old containers and volumes') {
            steps {
                echo ">>> Removing old containers and volumes"
                sh """
                    docker-compose -f compose.yml down -v || true
                    docker ps -a -q --filter name=${DOCKER_PROJECT}-* | xargs -r docker rm -f || true
                    docker volume prune -f || true
                """
            }
        }

        stage('Build and redeploy with Docker Compose') {
            steps {
                dir("${WORKSPACE_DIR}") {
                    sh "docker-compose -f compose.yml build --no-cache"
                    sh "docker-compose -f compose.yml up -d"
                }
            }
        }

        stage('Verify DB tables') {
            when {
                expression { env.SQL_EXISTS == "true" }
            }
            steps {
                echo ">>> Checking database tables..."
                sh """
                    docker exec -it frienddb psql -U postgres -d FriendDB -c "\\dt" || echo "No tables found"
                """
            }
        }
    }

    post {
        always {
            echo ">>> Pipeline finished"
        }
        failure {
            echo ">>> Pipeline failed, check logs"
        }
    }
}
