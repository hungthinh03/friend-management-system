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

        stage('Debug Workspace') {
            steps {
                script {
                    echo ">>> Jenkins WORKSPACE = ${env.WORKSPACE}"
                    sh "pwd"
                    sh "ls -l"
                    sh "ls -l ${env.WORKSPACE}"
                    sh "ls -l ${env.WORKSPACE}/sql || echo 'sql folder not found!'"
                    sh "ls -l ${env.WORKSPACE}/sql/frienddb.sql || echo 'frienddb.sql not found!'"
                }
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    dir(env.WORKSPACE) {
                        withEnv(["COMPOSE_PROJECT_NAME=${DOCKER_PROJECT}"]) {
                            echo ">>> Running docker-compose from: ${pwd()}"
                            sh "docker-compose -f compose.yml config"  // dump resolved config
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
