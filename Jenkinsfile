pipeline {
    agent any

    environment {
        DOCKER_PROJECT = "friend-management-system"
        SQL_FOLDER = "sql"
    }

    stages {
        stage('Declarative: Checkout SCM') {
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
                    if (fileExists("${SQL_FOLDER}")) {
                        echo ">>> Local SQL folder contents:"
                        sh "ls -l ${SQL_FOLDER}"
                    } else {
                        echo ">>> SQL folder not found, skipping SQL cleanup"
                    }
                }
            }
        }

        stage('Clean SQL mount') {
            when {
                expression { fileExists("${SQL_FOLDER}") }
            }
            steps {
                sh "rm -rf /docker_sql_mount/* || true"
            }
        }

        stage('Build Java app') {
            steps {
                dir("${WORKSPACE}") {
                    script {
                        if (!fileExists('gradlew')) {
                            error "ERROR: gradlew not found! Commit gradlew to repository."
                        }
                        sh "chmod +x gradlew"
                        sh "./gradlew clean build -x test"
                    }
                }
            }
        }

        stage('Clean old containers and volumes') {
            steps {
                sh """
                docker-compose -f docker-compose.yml down -v
                docker rm -f \$(docker ps -a -q --filter name=${DOCKER_PROJECT}-* || true) || true
                """
            }
        }

        stage('Build and redeploy with Docker Compose') {
            steps {
                sh "docker-compose -f docker-compose.yml build"
                sh "docker-compose -f docker-compose.yml up -d"
            }
        }

        stage('Verify DB tables') {
            steps {
                script {
                    sh "docker exec -it ${DOCKER_PROJECT}db psql -U postgres -d FriendDB -c '\\dt' || true"
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
