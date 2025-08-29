pipeline {
    agent any

    environment {
        DOCKER_PROJECT = "friend-management-system"
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
                echo ">>> Local SQL folder contents:"
                sh "ls -l ${WORKSPACE}/sql"
            }
        }

        stage('Clean SQL mount') {
            steps {
                echo ">>> Ensuring SQL folder only contains valid files"
                sh 'find ${WORKSPACE}/sql -mindepth 1 -type d -exec rm -rf {} +'
            }
        }

        stage('Build Java app') {
            steps {
                dir("${WORKSPACE}") {
                    sh "chmod +x gradlew"
                    sh "./gradlew clean build -x test"
                }
            }
        }

        stage('Clean old containers and volumes') {
            steps {
                script {
                    echo ">>> Stopping and removing old containers and volumes"
                    sh 'docker-compose -f compose.yml down -v || true'
                    sh 'docker volume prune -f || true'
                }
            }
        }

        stage('Build and redeploy with Docker Compose') {
            steps {
                script {
                    dir("${WORKSPACE}") {
                        sh 'docker-compose -f compose.yml build --no-cache'
                        sh 'docker-compose -f compose.yml up -d'
                    }
                }
            }
        }

        stage('Verify DB tables') {
            steps {
                echo ">>> Checking if FriendDB tables exist"
                sh 'docker exec -it frienddb psql -U postgres -d "FriendDB" -c "\\dt" || true'
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
