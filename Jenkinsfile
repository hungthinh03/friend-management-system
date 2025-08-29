pipeline {
    agent any

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Verify SQL file') {
            steps {
                sh 'ls -l ./sql'
                sh 'head -n 5 ./sql/frienddb.sql'
            }
        }

        stage('Build Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose -f compose.yml build'
            }
        }

        stage('Recreate DB') {
            steps {
                // Stop and remove containers
                sh 'docker-compose -f compose.yml down'

                // Remove old volume to force DB re-initialization
                sh 'docker volume rm frienddb_data || true'
            }
        }

        stage('Up Containers') {
            steps {
                sh 'docker-compose -f compose.yml up -d'
            }
        }

        stage('Health Check') {
            steps {
                sh 'docker ps'
            }
        }
    }

    post {
        always {
            sh 'docker ps'
        }
    }
}
