pipeline {
    agent any
    environment {
        DOCKER_PROJECT = "friend-management-system"
        WORKSPACE_DIR = "${env.WORKSPACE}"
        SQL_DIR = "${WORKSPACE_DIR}/sql"
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
                    if (!fileExists(SQL_DIR)) {
                        echo ">>> SQL folder not found, skipping SQL cleanup"
                        env.SKIP_SQL_CLEAN = "true"
                    } else {
                        env.SKIP_SQL_CLEAN = "false"
                        sh "ls -l ${SQL_DIR}"
                    }
                }
            }
        }

        stage('Clean SQL mount') {
            when {
                expression { env.SKIP_SQL_CLEAN == "false" }
            }
            steps {
                echo ">>> Ensuring SQL folder only contains valid files"
                sh "find ${SQL_DIR} -mindepth 1 -type d -exec rm -rf {} +"
            }
        }

        stage('Build Java app') {
            steps {
                dir("${WORKSPACE_DIR}") {
                    script {
                        if (fileExists("${WORKSPACE_DIR}/gradlew")) {
                            sh "chmod +x gradlew"
                            sh "./gradlew clean build -x test"
                        } else {
                            error "gradlew not found! Commit gradlew to repository."
                        }
                    }
                }
            }
        }

        stage('Clean old containers and volumes') {
            steps {
                script {
                    echo ">>> Removing old containers"
                    sh 'docker rm -f $(docker ps -a -q --filter name=${DOCKER_PROJECT}-* || true) || true'
                    echo ">>> Removing unused volumes"
                    sh 'docker volume prune -f'
                }
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
            steps {
                script {
                    echo ">>> Checking database tables"
                    sh "docker exec -it ${DOCKER_PROJECT}-db psql -U postgres -d FriendDB -c '\\dt'"
                }
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
